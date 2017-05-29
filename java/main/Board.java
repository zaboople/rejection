package main;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents Board state; makes very little effort to enforce
 * game rules. Exposed as public only for testing.
 */
public class Board implements BoardView {

  /////////////////////
  // INITIALIZATION: //
  /////////////////////

  private final Cell[] cells;
  private final int width, height;
  private final int startPos, finishPos;

  // State is this simple, really... well, watch out for Cell:
  private int prev=-1;
  private int current=-1;

  public Board(SecureRandom randomizer, int width, int height, int keyCount, int bonusCount) {
    this.width=width;
    this.height=height;

    // Create cells:
    this.cells=new Cell[width * height];
    for (int i=0; i<cells.length; i++) this.cells[i]=new Cell();

    // Randomize start/finish/keys/bonuses:
    RandomNoRepeat random=new RandomNoRepeat(randomizer, cells.length-2);
    int start=random.next(), finish=random.next();
    int[] keys=random.fill(keyCount), bonuses=random.fill(bonusCount);

    // Initialize the rest of our state:
    this.startPos=start;
    this.finishPos=finish;
    getCell(startPos).setUsed();
    getCell(finishPos).setUsed();
    reserve(Cell::setKey, keys);
    reserve(Cell::setBonus, bonuses);
  }

  private void reserve(java.util.function.Consumer<Cell> cellFunction, int... cellIndices) {
    for (int index: cellIndices)
      if (!cells[index].isEmpty())
        throw new IllegalStateException("Cell is already used "+cells[index]);
      else
        cellFunction.accept(cells[index]);
  }

  ////////////////////////////////////
  // BoardView read-only overrides: //
  ////////////////////////////////////

  public @Override int getWidth() {return width;}
  public @Override int getHeight() {return height;}
  public @Override Cell getCell(int i) {return cells[i];}
  public @Override Cell getCell(int row, int col) {return getCell(toIndex(row, col));}
  public @Override boolean isStart(int row, int col) {
    return toIndex(row, col)==startPos;
  }
  public @Override boolean isFinish(int row, int col) {
    return toIndex(row, col)==finishPos;
  }
  public @Override boolean onFinish() {
    return current==finishPos;
  }
  public @Override int getCellIndex(int row, int col) {
    return toIndex(row, col);
  }

  /////////////////////////////////////////////////////////////
  // Package-private logic, for the Game class and/or tests: //
  /////////////////////////////////////////////////////////////

  int getCellCount() {return cells.length;}
  Card getCard(int index) {
    return getCell(index).getCard();
  }

  Card getCurrentCard() {
    return current==-1 ?null :getCell(current).getCard();
  }

  /** Not used */
  int getDistanceTo(int index) {
    int currRow=getCurrentRow();
    int currCol=getCurrentCol();
    int toRow=index / width;
    int toCol=index % width;
    return Math.abs(toRow-currRow) + Math.abs(toCol-currCol);
  }

  boolean onKey() {return cells[current].isKey();}
  boolean onBonus() {return cells[current].isBonus();}
  boolean hasCurrent() {
    return current!=-1;
  }

  boolean canPlay(byte direction) {
    return getTarget(current, direction) > 0;
  }
  byte whereCanIPlayTo() {
    return whereCanIPlayToFrom(current);
  }

  void rotateCard() {
    Card nowCard=getCurrentCard();
    Card newCard=nowCard.rotate();
    if (prev==-1){
    }
    else
    if (prev==current-1)
      while (!newCard.hasPathLeft()) newCard=newCard.rotate();
    else
    if (prev==current+1)
      while (!newCard.hasPathRight()) newCard=newCard.rotate();
    else
    if (prev==current-width)
      while (!newCard.hasPathUp()) newCard=newCard.rotate();
    else
    if (prev==current+width)
      while (!newCard.hasPathDown()) newCard=newCard.rotate();
    else
      throw new IllegalStateException("Previous card "+prev+" doesn't seem to align to new "+current);
    setCard(newCard);
  }

  void playFirstCard(Card card) {
    card=card.getOptimalRotationFor(whereCanIPlayToFrom(startPos), (byte)0, null);
    setCard(startPos, card);
    current=startPos;
  }

  /**
   * When the user wants to swap the current-not-committed card to the next available
   * spot, call this.
   */
  byte switchPlay() {
    if (current==startPos)
      return -1;
    byte[] toTry;
    if (current==prev+1)
      return trySwitch(Dir.DOWN, Dir.LEFT, Dir.UP);
    if (current==prev+width)
      return trySwitch(Dir.LEFT, Dir.UP, Dir.RIGHT);
    if (current==prev-1)
      return trySwitch(Dir.UP, Dir.RIGHT, Dir.DOWN);
    if (current==prev-width)
      return trySwitch(Dir.RIGHT, Dir.DOWN, Dir.LEFT);
    throw new IllegalStateException("Current / prev mismatch "+current+" "+prev);
  }

  /**
   * Only public because of residual test in another package
   * @param card The card to play
   * @param direction The direction to play the card, starting from the current card's position.
   * @param prevCard A previous card to match the new card's rotation to, if possible.
   */
  public void playCard(Card card, final byte direction, Card prevCard) {
    prev=current;
    int target=getTarget(current, direction);
    if (target<0)
      throw new IllegalStateException("Not a legal card placement");
    card=getOptimalRotation(card, target, direction, prevCard);
    setCard(target, card);
    current=target;
  }

  ////////////////////////
  // PRIVATE GAME PLAY: //
  ////////////////////////

  private Card getOptimalRotation(Card card, int target, byte direction, Card prevCard) {
    return card.getOptimalRotationFor(
      whereCanIPlayToFrom(target), Dir.OPPOSITES[direction], prevCard
    );
  }
  private byte whereCanIPlayToFrom(int fromPos) {
    return (byte) (
      canPlayTo(fromPos, Dir.LEFT) |
      canPlayTo(fromPos, Dir.RIGHT)|
      canPlayTo(fromPos, Dir.UP)   |
      canPlayTo(fromPos, Dir.DOWN)
    );
  }
  private byte canPlayTo(int fromPosition, byte direction) {
    return getTarget(fromPosition, direction)>-1 ?direction :(byte)0;
  }
  private int getTarget(int fromPosition, byte direction) {
    int target;
    boolean pastEdge;
    switch (direction) {
      case Dir.UP:
        pastEdge=fromPosition < width; //Top row
        target=fromPosition-width;
        break;
      case Dir.DOWN:
        pastEdge=fromPosition / width==height-1; //Bottom row
        target=fromPosition+width;
        break;
      case Dir.LEFT:
        pastEdge=fromPosition % width==0; //First column
        target=fromPosition-1;
        break;
      case Dir.RIGHT:
        pastEdge=fromPosition % width==width-1; // Last column
        target=fromPosition+1;
        break;
      default:
        throw new IllegalArgumentException(""+direction);
    }
    return (
        !pastEdge
        &&
        (getCard(fromPosition)==null || getCard(fromPosition).hasPath(direction))
        &&
        getCard(target)==null
      )
      ?target :-1;
  }

  private byte trySwitch(byte... toTry){
    Card card=getCard(current);
    for (byte direction: toTry) {
      int target=getTarget(prev, direction);
      if (target > -1) {
        card=getOptimalRotation(card, target, direction, null);
        setCard(target, card);
        setCard(current, null);
        current=target;
        return direction;
      }
    }
    return -1;
  }

  ///////////////////////////
  // OTHER PRIVATE THINGS: //
  ///////////////////////////

  private int toIndex(int row, int col) {
    return col+(row*width);
  }
  private int getCurrentRow() {
    return current / width;
  }
  private int getCurrentCol() {
    return current % width;
  }

  /** This is only exposed for testing. */
  public Board setCard(int row, int col, Card card) {
    return setCard(toIndex(row, col), card);
  }
  private Board setCard(Card card) {
    return setCard(current, card);
  }
  private Board setCard(int index, Card card) {
    cells[index].setPrevious(card==null ?-1 :prev);
    cells[index].setCard(card);
    return this;
  }

}
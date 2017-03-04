package main;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents Board state; makes very little effort to enforce
 * game rules.
 */
public class Board {

  /////////////////////
  // INITIALIZATION: //
  /////////////////////

  private final Cell[] cells;
  private final int width, height;

  private int prev=-1;
  private int current=-1;
  private int[] keyCells;
  private int[] bonusCells;
  private int startPos, finishPos;
  private final SecureRandom randomizer;

  public Board(SecureRandom randomizer, int width, int height) {
    this.randomizer=randomizer;
    this.width=width;
    this.height=height;
    this.cells=new Cell[width * height];
    for (int i=0; i<cells.length; i++) this.cells[i]=new Cell();
  }
  public Board reset(int keyCount, int bonusCount) {
    RandomNoRepeat random=new RandomNoRepeat(randomizer, cells.length-2);
    reset(
      random.next(),
      random.next(),
      random.fill(keyCount),
      random.fill(bonusCount)
    );
    return this;
  }
  public Board reset(int start, int finish, int[] keys, int[] bonuses) {
    for (int i=0; i<cells.length; i++)
      this.cells[i].clear();
    prev=-1;
    current=-1;
    this.startPos=start;
    this.finishPos=finish;
    getCell(startPos).setUsed();
    getCell(finishPos).setUsed();
    setKeys(keys);
    setBonus(bonuses);
    return this;
  }

  ////////////////////////////////////////
  // PUBLIC GAME PLAY, READ-ONLY STATE: //
  ////////////////////////////////////////

  public int getWidth() {return width;}
  public int getHeight() {return height;}
  public int[] getKeyCells() {return keyCells;}
  public int[] getBonusCells() {return bonusCells;}
  public Cell getCell(int row, int col) {return getCell(toIndex(row, col));}
  public Cell getCell(int i) {return cells[i];}

  public Card getCard(int row, int col) {
    return getCard(toIndex(row, col));
  }
  public Card getCard(int index) {
    return getCell(index).getCard();
  }
  public Card getCurrentCard() {
    return getCard(current);
  }

  public int getDistanceTo(int index) {
    int currRow=getCurrentRow();
    int currCol=getCurrentCol();
    int toRow=index / width;
    int toCol=index % width;
    return Math.abs(toRow-currRow) + Math.abs(toCol-currCol);
  }

  public boolean onKey() {return cells[current].isKey();}
  public boolean onBonus() {return cells[current].isBonus();}
  public boolean onFinish() {
    return current==finishPos;
  }
  public boolean isStart(int row, int col) {
    return toIndex(row, col)==startPos;
  }
  public boolean isFinish(int row, int col) {
    return toIndex(row, col)==finishPos;
  }

  public boolean canPlay(byte direction) {
    return getTarget(current, direction) > 0;
  }
  public byte whereCanIPlayTo() {
    return whereCanIPlayToFrom(current);
  }
  public byte whereCanIPlayToFrom(int fromPos) {
    return (byte) (
      canPlayTo(fromPos, Dir.LEFT) |
      canPlayTo(fromPos, Dir.RIGHT)|
      canPlayTo(fromPos, Dir.UP)   |
      canPlayTo(fromPos, Dir.DOWN)
    );
  }

  ////////////////////////////////////////
  // PUBLIC GAME PLAY STATE MANAGEMENT: //
  ////////////////////////////////////////

  public void rotateCard() {
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
  public void playFirstCard(Card card) {
    card=card.getOptimalRotationFor(whereCanIPlayToFrom(startPos), (byte)0);
    setCard(startPos, card);
    current=startPos;
  }
  public void play(Card card, final byte direction) {
    int target=getTarget(current, direction);
    if (target<0)
      throw new IllegalStateException("Not a legal card placement");;
    card=getOptimalRotation(card, target, direction);
    setCard(target, card);
    prev=current;
    current=target;
  }
  public boolean switchPlay() {
    if (current==startPos)
      return false;
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

  ////////////////////////
  // PRIVATE GAME PLAY: //
  ////////////////////////

  private Card getOptimalRotation(Card card, int target, byte direction) {
    return card.getOptimalRotationFor(
      whereCanIPlayToFrom(target), Dir.OPPOSITES[direction]
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

  private boolean trySwitch(byte... toTry){
    Card card=getCard(current);
    for (byte direction: toTry) {
      int target=getTarget(prev, direction);
      if (target > -1) {
        card=getOptimalRotation(card, target, direction);
        setCard(target, card);
        setCard(current, null);
        current=target;
        return true;
      }
    }
    return false;
  }

  /////////////////////////////
  // PRIVATE INITIALIZATION: //
  /////////////////////////////

  private void setKeys(int... cellIndices) {
    keyCells=cellIndices;
    reserve(Cell::setKey, cellIndices);
  }
  private void setBonus(int... cellIndices) {
    bonusCells=cellIndices;
    reserve(Cell::setBonus, cellIndices);
  }
  private void reserve(java.util.function.Consumer<Cell> cellFunction, int... cellIndices) {
    for (int index: cellIndices)
      if (!cells[index].isEmpty())
        throw new IllegalStateException("Cell is already used "+cells[index]);
      else
        cellFunction.accept(cells[index]);
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
    cells[index].setCard(card);
    return this;
  }

}
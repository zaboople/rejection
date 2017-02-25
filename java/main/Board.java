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
  public final static int STD_WIDTH=8;
  public final static int STD_HEIGHT=8;
  public final static int STD_KEYS=3;
  public final static int STD_BONUSES=2;


  private final Cell[] cells;
  private final int width, height;


  private int prev=-1;
  private int current=-1;
  private int[] keyCells;
  private int[] bonusCells;
  private final SecureRandom randomizer;

  public Board() {
    this(new SecureRandom(), STD_WIDTH, STD_HEIGHT, STD_KEYS, STD_BONUSES);
  }
  public Board(SecureRandom randomizer, int width, int height, int keys, int bonuses) {
    this.randomizer=randomizer;
    this.width=width;
    this.height=height;
    this.cells=new Cell[width * height];
    for (int i=0; i<cells.length; i++) this.cells[i]=new Cell();
    reset();
  }
  public int getWidth() {return width;}
  public int getHeight() {return height;}
  public int[] getKeyCells() {return keyCells;}
  public int[] getBonusCells() {return bonusCells;}

  private int toIndex(int row, int col) {
    return col+(row*width);
  }

  public Board reset() {
    for (int i=0; i<cells.length; i++)
      this.cells[i].clear();
    prev=-1;
    current=-1;
    RandomNoRepeat random=new RandomNoRepeat(randomizer, cells.length-2);
    setKeys(random.next(), random.next(), random.next());
    setBonus(random.next(), random.next());
    return this;
  }

  public Board setCard(Card card) {
    return setCard(current, card);
  }
  public Board setCard(int row, int col, Card card) {
    return setCard(toIndex(row, col), card);
  }
  public Board setCard(int index, Card card) {
    cells[index].setCard(card);
    return this;
  }
  public Card getCard(int row, int col) {
    return getCard(toIndex(row, col));
  }
  public Card getCard(int index) {
    return getCell(index).getCard();
  }
  public Card getCurrentCard() {
    return getCard(current);
  }
  public Cell getCell(int row, int col) {return getCell(toIndex(row, col));}
  public Cell getCell(int i) {return cells[i];}

  public int getDistanceTo(int index) {
    int currRow=getCurrentRow();
    int currCol=getCurrentCol();
    int toRow=index / width;
    int toCol=index % width;
    return Math.abs(toRow-currRow) + Math.abs(toCol-currCol);
  }

  public boolean onKey() {return cells[current].isKey();}
  public boolean onBonus() {return cells[current].isBonus();}


  public int getCurrentRow() {
    return current / width;
  }
  public int getCurrentCol() {
    return current % width;
  }
  public boolean onFinish() {
    return current==cells.length-1;
  }
  public boolean onStart() {
    return current==-1;
  }

  public boolean canPlay(byte direction) {
    return getTarget(direction) > 0;
  }
  public byte whereCanIPlayTo() {
    return (byte) (
      canPlayTo(Dir.LEFT) |
      canPlayTo(Dir.RIGHT)|
      canPlayTo(Dir.UP)   |
      canPlayTo(Dir.DOWN)
    );
  }
  private byte canPlayTo(byte direction) {
    return getTarget(direction)>0 ?direction :(byte)0;
  }
  private int getTarget(byte direction) {
    int target;
    boolean pastEdge;
    switch (direction) {
      case Dir.UP:
        pastEdge=current < width; //Top row
        target=current-width;
        break;
      case Dir.DOWN:
        pastEdge=current / width==height-1; //Bottom row
        target=current+width;
        break;
      case Dir.LEFT:
        pastEdge=current % width==0; //First column
        target=current-1;
        break;
      case Dir.RIGHT:
        pastEdge=current % width==width-1; // Last column
        target=current+1;
        break;
      default:
        throw new IllegalArgumentException(""+direction);
    }
    return (
        !pastEdge
        &&
        getCurrentCard().hasPath(direction)
        &&
        getCard(target)==null
      )
      ?target
      :(byte)-1;
  }

  public void playFirstCard(Card card) {
    setCard(0, card);
    current=0;
  }
  public void play(Card card, final byte direction) {
    int target=getTarget(direction);
    if (target<1)
      throw new IllegalStateException("Not a legal card placement");;
    final byte reversed=Dir.OPPOSITES[direction];
    while (!card.hasPath(reversed))
      card=card.rotate();
    setCard(target, card);
    prev=current;
    current=target;
  }
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

  private void setKeys(int... cellIndices) {
    keyCells=cellIndices;
    if (cellIndices.length != 3) throw new IllegalArgumentException("Only 3 keys");
    for (int index: cellIndices)
      if (!cells[index+1].isEmpty())
        throw new IllegalStateException("Cell is already used");
      else
        cells[index+1].setKey();
  }
  private void setBonus(int... cellIndices) {
    bonusCells=cellIndices;
    if (cellIndices.length != 2) throw new IllegalArgumentException("Only 2 bonuses");
    for (int index: cellIndices)
      if (!cells[index+1].isEmpty())
        throw new IllegalStateException("Cell is already used");
      else
        cells[index+1].setBonus();
  }
}
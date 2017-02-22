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
  public final static Cell EMPTY=Cell.empty();

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
      if (this.cells[i]!=EMPTY) // Silly maybe-optimization
        this.cells[i]=EMPTY;
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
    cells[index]=cells[index].fromCard(card);
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
  public boolean canPlayUp() {
    if (current < width) return false;//On first row
    return getCurrentCard().hasPathUp() && getCard(current-width)==null;
  }
  public boolean canPlayDown() {
    if (current / width==height-1) return false;//On last row
    return getCurrentCard().hasPathDown() && getCard(current+width)==null;
  }
  public boolean canPlayLeft() {
    if (current % width==0) return false;//On first col
    return getCurrentCard().hasPathLeft() && getCard(current-1)==null;
  }
  public boolean canPlayRight() {
    if (current % width==width-1) return false;//On last col
    return getCurrentCard().hasPathRight() && getCard(current+1)==null;
  }

  public void playFirstCard(Card card) {
    setCard(0, card);
    current=0;
  }
  public void playUp(Card card) {
    playCard(card, current-width, this::canPlayUp, Card::hasPathDown);
  }
  public void playDown(Card card) {
    playCard(card, current+width, this::canPlayDown, Card::hasPathUp);
  }
  public void playLeft(Card card) {
    playCard(card, current-1, this::canPlayLeft, Card::hasPathRight);
  }
  public void playRight(Card card) {
    playCard(card, current+1, this::canPlayRight, Card::hasPathLeft);
  }
  private void playCard(
      Card card,
      int toPosition,
      Supplier<Boolean> checkCan,
      Function<Card, Boolean> rotateChecker
    ) {
    if (!checkCan.get()) throw new IllegalStateException("Not a legal card placement");;
    while (!rotateChecker.apply(card))
      card=card.rotate();
    setCard(toPosition, card);
    prev=current;
    current=toPosition;
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
      if (cells[index+1]!=EMPTY)
        throw new IllegalStateException("Cell is already used");
      else
        cells[index+1]=Cell.key();
  }
  private void setBonus(int... cellIndices) {
    bonusCells=cellIndices;
    if (cellIndices.length != 2) throw new IllegalArgumentException("Only 2 bonuses");
    for (int index: cellIndices)
      if (cells[index+1]!=EMPTY)
        throw new IllegalStateException("Cell is already used");
      else
        cells[index+1]=Cell.bonus();
  }
}
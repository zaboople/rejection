package main;

import java.security.SecureRandom;
import java.awt.Point;
import java.util.function.Supplier;
import java.util.function.Function;

public class Board {
  public final static int STD_WIDTH=8;
  public final static int STD_HEIGHT=8;
  public final static Cell EMPTY=Cell.empty();

  private final Cell[] cells;
  private final int width, height;
  private int prev=-1;
  private int current=-1;
  private int[] keyCells;
  private int[] bonusCells;

  public Board() {
    this(STD_WIDTH, STD_HEIGHT);
  }
  public Board(int width, int height) {
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
    RandomNoRepeat random=new RandomNoRepeat(cells.length-2);
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
    prev=current;
    current=index;
    return this;
  }
  public Card getCard(int row, int col) {
    return getCard(toIndex(row, col));
  }
  public Card getCard(int index) {
    return getCell(index).getCard();
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
  public boolean canPutUp() {
    if (current < width) return false;//On first row
    return getCard(current).hasPathUp() && getCard(current-width)==null;
  }
  public boolean canPutDown() {
    if (current / width==height-1) return false;//On last row
    return getCard(current).hasPathDown() && getCard(current+width)==null;
  }
  public boolean canPutLeft() {
    if (current % width==0) return false;//On first col
    return getCard(current).hasPathLeft() && getCard(current-1)==null;
  }
  public boolean canPutRight() {
    if (current % width==width-1) return false;//On last col
    return getCard(current).hasPathRight() && getCard(current+1)==null;
  }

  public void putUp(Card card) {
    putCard(card, current-width, this::canPutUp, Card::hasPathDown);
  }
  public void putDown(Card card) {
    putCard(card, current+width, this::canPutDown, Card::hasPathUp);
  }
  public void putLeft(Card card) {
    putCard(card, current-1, this::canPutLeft, Card::hasPathRight);
  }
  public void putRight(Card card) {
    putCard(card, current+1, this::canPutRight, Card::hasPathLeft);
  }
  private void putCard(
      Card card,
      int toPosition,
      Supplier<Boolean> checkCan,
      Function<Card, Boolean> rotateChecker
    ) {
    if (!checkCan.get()) throw new IllegalStateException("Not a legal card placement");;
    while (!rotateChecker.apply(card))
      card=card.rotate();
    setCard(toPosition, card);
  }

  public void rotateCard() {
    Card nowCard=getCard(current);
    Card newCard=nowCard.rotate();
    if (prev==-1){
    }
    else
    if (prev==current-1)
      while (!newCard.hasPathLeft()) newCard.rotate();
    else
    if (prev==current+1)
      while (!newCard.hasPathRight()) newCard.rotate();
    else
    if (prev==current-width)
      while (!newCard.hasPathUp()) newCard.rotate();
    else
    if (prev==current+width)
      while (!newCard.hasPathDown()) newCard.rotate();
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
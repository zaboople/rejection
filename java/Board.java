public class Board {
  public final static int STD_WIDTH=8;
  public final static int STD_HEIGHT=8;
  public final static Cell EMPTY=Cell.empty();

  private final Cell[] cells;
  private final int width, height;

  public Board() {
    this(STD_WIDTH, STD_HEIGHT);
  }
  public Board(int width, int height) {
    this.width=width;
    this.height=height;
    this.cells=new Cell[width * height];
    clear();
  }


  public Board clear() {
    for (int i=0; i<cells.length; i++)
      if (this.cells[i]!=EMPTY) // Silly maybe-optimization
        this.cells[i]=EMPTY;
    return this;
  }
  public Board setKeys(int... cellIndices) {
    if (cellIndices.length != 3) throw new IllegalArgumentException("Only 3 keys");
    for (int index: cellIndices)
      if (cells[index+1].isBonus())
        throw new RuntimeException("Cannot be key & bonus");
      else
        cells[index+1]=Cell.key();
    return this;
  }
  public Board setBonus(int... cellIndices) {
    if (cellIndices.length != 2) throw new IllegalArgumentException("Only 2 bonuses");
    for (int index: cellIndices)
      if (cells[index+1].isKey())
        throw new RuntimeException("Cannot be key & bonus");
      else
        cells[index+1]=Cell.bonus();
    return this;
  }
  public Board setCard(int index, Card card) {
    cells[index]=cells[index].fromCard(card);
    return this;
  }

  public int getWidth() {return width;}
  public int getHeight() {return height;}
  public Cell getCell(int i) {return cells[i];}

  public static class Cell {
    boolean key, bonus;
    Card card;
    public static Cell key() {return new Cell(true, false);}
    public static Cell bonus() {return new Cell(false, true);}
    public static Cell empty() {return new Cell(false, false);}
    private Cell(boolean key, boolean bonus) {
      if (key && bonus) throw new IllegalArgumentException("Cannot be both key & bonus");
      this.key=key;
      this.bonus=bonus;
    }
    public boolean isKey() {return key;}
    public boolean isBonus() {return bonus;}
    public boolean isCard() {return card!=null;}
    public Card getCard() {return card;}
    public Cell fromCard(Card card) {
      Cell cell=new Cell(key, bonus);
      cell.card=card;
      return cell;
    }
  }
}
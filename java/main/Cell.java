package main;

/**
 * Represents a (potentially empty) cell on the playing board.
 * Cells are immutable.
 */
public class Cell {
  private final boolean key, bonus;
  private final Card card;

  public static Cell key() {return new Cell(true, false, null);}
  public static Cell bonus() {return new Cell(false, true, null);}
  public static Cell empty() {return new Cell(false, false, null);}
  private Cell(boolean key, boolean bonus, Card card) {
    if (key && bonus) throw new IllegalArgumentException("Cannot be both key & bonus");
    this.key=key;
    this.bonus=bonus;
    this.card=card;
  }
  public boolean isKey() {return key;}
  public boolean isBonus() {return bonus;}
  public boolean isCard() {return card!=null;}
  public Card getCard() {return card;}
  public Cell fromCard(Card card) {
    return new Cell(key, bonus, card);
  }
}

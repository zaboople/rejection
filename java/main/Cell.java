package main;

/**
 * Represents a (potentially empty) cell on the playing board.
 * Cells are immutable.
 */
public class Cell {
  private boolean key, bonus, used;
  private Card card;

  public boolean isKey() {return key;}
  public boolean isBonus() {return bonus;}
  public boolean isCard() {return card!=null;}
  public Card getCard() {return card;}
  public void setKey(){key=true;}
  public void setBonus(){bonus=true;}
  public void setCard(Card card){this.card=card;}
  public void setUsed(){this.used=true;}
  public void clear(){
    this.key=false;
    this.bonus=false;
    this.used=false;
    this.card=null;
  }
  public boolean isEmpty() {
    return !key && !bonus && card==null && !used;
  }
  public String toString() {
    return ""+key+" "+bonus+" "+used+" "+card;
  }
}

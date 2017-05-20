package main;

/**
 * Represents a (potentially empty) cell on the playing board.
 */
public class Cell {
  private boolean key, bonus, used;
  private Card card;

  // Read-only functions:
  public boolean isKey() {return key;}
  public boolean isBonus() {return bonus;}
  public boolean isCard() {return card!=null;}
  public Card getCard() {return card;}
  public boolean isEmpty() {
    return !key && !bonus && card==null && !used;
  }
  public String toString() {
    return ""+key+" "+bonus+" "+used+" "+card;
  }


  // Initialization functions:
  public void setKey(){key=true;}
  public void setBonus(){bonus=true;}
  public void setUsed(){this.used=true;}

  // Game-play functions:
  public void setCard(Card card){this.card=card;}

}

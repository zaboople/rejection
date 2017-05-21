package main;

/**
 * Represents a (potentially empty) cell on the playing board.
 */
public class Cell {

  // Internal state:
  private boolean key, bonus, used;
  private Card card;
  private int previous=-1;

  // Read-only functions:
  public boolean isKey() {return key;}
  public boolean isBonus() {return bonus;}
  public boolean isCard() {return card!=null;}
  public Card getCard() {return card;}
  public int getPrevious() {return previous;}
  public boolean isEmpty() {
    return !key && !bonus && card==null && !used;
  }
  public String toString() {
    return "key="+key+".bonus="+bonus+".used="+used+".card="+card+".previous="+previous;
  }

  // Initialization functions:
  void setKey(){key=true;}
  void setBonus(){bonus=true;}
  void setUsed(){this.used=true;}

  // Game-play functions:
  void setCard(Card card){this.card=card;}
  void setPrevious(int p){this.previous=p;}

}

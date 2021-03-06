package main;

/** Represents the overall amount wagered, and the bet for the current game round.*/
public class Gamble {
  final int startWith;
  private int total;
  public int bet;
  public Gamble(int start) {
    this.startWith=start;
    this.total=start;
  }
  public boolean winOrLose(boolean win, boolean bonus){
    total+=win
      ?(bonus ?bet*10 :bet)
      :-bet;
    return total>0;
  }
  public boolean canDoubleDown() {
    return bet<total;
  }
  public void doubleDown() {
    bet=bet*2;
    if (bet>total) bet=total;
  }
  public int getTotal() {
    return total;
  }
  public int getBet() {
    return bet;
  }
  public void setBet(int bet) {
    this.bet=bet;
  }
}
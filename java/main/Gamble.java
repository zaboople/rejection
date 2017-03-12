package main;
public class Gamble {
  final int startWith;
  int total;
  int bet;
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
  public void doubleDown() {
    bet=bet*2;
    if (bet>total) bet=total;
  }
}
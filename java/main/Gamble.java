package main;
public class Gamble {
  final int startWith;
  int total;
  int bet;
  public Gamble(int start) {
    this.startWith=start;
    this.total=start;
  }
  public boolean winOrLose(boolean win){
    total+=win ?bet :-bet;
    return total>0;
  }
}
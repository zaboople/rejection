package main;

public class Session {
  private int hoard;
  public Session(int hoard) {
    this.hoard=hoard;
  }
  public Game newGame(int bet) {
    return new Game(bet);
  }

}
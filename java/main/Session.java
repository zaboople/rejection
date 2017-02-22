package main;

/** Currently not used, because I don't have time for actual gambling. */
public class Session {
  private int hoard;
  public Session(int hoard) {
    this.hoard=hoard;
  }
  public Game newGame(int bet) {
    return new Game();
  }
}

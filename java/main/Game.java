package main;

public class Game {
  private int bet;
  private Board board=new Board();
  private Deck deck=new Deck();
  public Game(int bet) {
    this.bet=bet;
    board.init();
  }
}
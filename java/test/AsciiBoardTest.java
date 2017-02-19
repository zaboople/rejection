package test;
import main.AsciiBoard;
import main.Board;
import main.Card;

public class AsciiBoardTest {
  public static void main(String[] args) throws Exception {
    Board board=new Board();
    board.setCard(0, 0, Card.pathBar());
    board.putRight(Card.pathCorner());
    board.putDown(Card.pathCorner().rotate().rotate());
    board
      .setCard(1, 2, Card.pathTee())
      .setCard(2, 2, Card.pathCross())
      ;
    AsciiBoard.draw(board, System.out);
    System.out.flush();
  }
}
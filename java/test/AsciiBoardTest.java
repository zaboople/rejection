package test;
import main.AsciiBoard;
import main.Board;
import main.Card;

public class AsciiBoardTest {
  public static void main(String[] args) throws Exception {
    Board board=new Board();
    board.setCard(0, 0, Card.pathBar());
    board.playRight(Card.pathCorner());
    board.playDown(Card.pathCorner().rotate().rotate());
    AsciiBoard.draw(board, System.out);
    System.out.flush();
  }
}
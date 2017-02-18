package test;
import main.*;

public class AsciiBoardTest {
  public static void main(String[] args) throws Exception {
    Board board=new Board();
    board.setKeys(12, 13, 9);
    board.setBonus(45, 20);
    board
      .setCard(0, Card.path(true, true, false, false))
      .setCard(1, Card.path(false, false, true, true))
      .setCard(9, Card.path(true, true, true, true))
      .setCard(10, Card.path(true, true, true, true))
      .setCard(2, Card.path(false, true, true, false))
      .setCard(3, Card.path(false, true, false, true))
      ;
    AsciiBoard.draw(board, System.out);
    System.out.flush();
  }
}
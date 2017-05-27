package test;
import main.cmdline.AsciiBoard;
import main.Board;
import main.Card;
import main.Dir;
import java.security.SecureRandom;

/** This test is broken */
public class AsciiBoardTest {
  public static void main(String[] args) throws Exception {
    Board board=new Board(new SecureRandom(), 5, 5, 1, 1);
    board.setCard(0, 0, Card.pathBar());
    board.playCard(Card.pathCorner(), Dir.RIGHT);
    board.playCard(Card.pathCorner().rotate().rotate(), Dir.DOWN);
    new AsciiBoard(System.out, true).draw(board, System.out);
    System.out.flush();
  }
}
package test;
import main.AsciiBoard;
import main.Board;
import main.Card;
import main.Dir;
import java.security.SecureRandom;

/** This test is broken */
public class AsciiBoardTest {
  public static void main(String[] args) throws Exception {
    Board board=new Board(new SecureRandom(), 5, 5).reset(1, 1);
    board.setCard(0, 0, Card.pathBar());
    board.play(Card.pathCorner(), Dir.RIGHT);
    board.play(Card.pathCorner().rotate().rotate(), Dir.DOWN);
    new AsciiBoard(System.out, true).draw(board, System.out);
    System.out.flush();
  }
}
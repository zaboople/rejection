package test;
import main.AsciiBoard;
import main.Board;
import main.Card;
import main.Dir;

public class AsciiBoardTest {
  public static void main(String[] args) throws Exception {
    Board board=new Board();
    board.setCard(0, 0, Card.pathBar());
    board.play(Card.pathCorner(), Dir.RIGHT);
    board.play(Card.pathCorner().rotate().rotate(), Dir.DOWN);
    AsciiBoard.draw(board, System.out);
    System.out.flush();
  }
}
package test;
import main.cmdline.AsciiBoard;
import main.Board;
import main.Card;
import main.Dir;

public class RotationTest {
  public static void main(String[] args) throws Exception {
    test(Card.pathCorner(), Dir.LEFT, Dir.UP, Dir.RIGHT);
    test(Card.pathTee(), Dir.DOWN, Dir.UP, Dir.RIGHT);
    test(Card.pathTee(), Dir.DOWN, Dir.LEFT, Dir.UP, Dir.RIGHT);
    test(Card.pathBar(), Dir.UP, Dir.LEFT, Dir.RIGHT);
    test(Card.pathBar(), Dir.DOWN, Dir.UP);
    test(Card.pathBar(), Dir.LEFT, Dir.DOWN, Dir.UP);
    test(Card.pathCross(), Dir.DOWN, Dir.UP, Dir.RIGHT);
  }

  private static void test(Card card, byte from, byte... toDirs) {
    String toDirNames="";
    for (byte d: toDirs) toDirNames+=Dir.DIR_TO_NAME[d];
    System.out.println(
      "FROM "+Dir.DIR_TO_NAME[from]+" TO "+toDirNames+": " +
      card.getOptimalRotationFor(getDirs(toDirs), from, null)
    );
  }




  private static byte getDirs(byte... dirs) {
    byte r=0;
    for (int i=0; i<dirs.length; i++)
      r |= dirs[i];
    return r;
  }
}
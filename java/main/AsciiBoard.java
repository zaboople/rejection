package main;

import java.util.function.Function;
public class AsciiBoard {

  private final Terminal out;
  public AsciiBoard(Appendable out, boolean colored) {
    this.out=new Terminal(out, colored);
  }

  public void draw(Board board, Appendable app) throws Exception {
    final int width=board.getWidth(), height=board.getHeight();
    out.append(' ');
    for (int i=0; i<width -1 + (width*3); i++) out.append('_');
    out.append('\n');
    for (int r=0; r<height; r++){
      for (int innerRow=0; innerRow<3; innerRow++) {
        // There are three "inner" rows for each row. We render these
        // one a time, going across all columns before going to the
        // next "inner row":
        out.append('|');
        for (int c=0; c<width; c++){
          boolean first=board.isStart(r, c);
          boolean last=!first && board.isFinish(r, c);
          Cell cell=board.getCell(r, c);
          doInnerRow(app, innerRow, cell, first, last);
        }
        out.append("\n");
      }
    }
  }

  /**
   * This draws one of three lines for a cell: Top, Middle, or Bottom.
   * It does NOT draw the first character of every line, which is "|".
   * It DOES draw the last character of every line - also "|".
   *
   * The bottom inner row forms the top of the next line, or at the very end,
   * the bottom of the board.
   *
   * The very first line of the board (just a series of _'s) is rendered elsewhere.
   *
   * I originally thought it would be a good idea to draw a - left-right across cells
   * for left-right plays, but that's hard and it doesn't actually look good.
   */
  private void doInnerRow(
      Appendable app, int innerRow,
      Cell cell,
      boolean veryFirst, boolean veryLast
    ) throws Exception {
    final char corner=veryFirst || veryLast ?'*' :' ';
    final char bottomCorner=veryFirst || veryLast ?'*' :'_';
    switch (innerRow) {
      case 0:
        out.append(corner)
          .append(getEdge(cell, Card::hasPathUp, "|", " "))
          .append(corner)
          .append("|");
        break;
      case 1:
        final char center=getCenter(cell, veryFirst, veryLast);
        out.append(getEdge(cell, Card::hasPathLeft, "-", " "))
          .addColored(
            center=='K'
              ?Terminal.FG_GREEN
              :(center=='S' || center=='F' ?Terminal.FG_MAGENTA :null),
            center
          )
          .append(getEdge(cell, Card::hasPathRight, "-", " "))
          .append('|');
        break;
      case 2:
        out.append(bottomCorner)
          .append(getEdge(cell, Card::hasPathDown, "|", "_"))
          .append(bottomCorner)
          .append("|");
        break;
    }
  }


  private static String getEdge(Cell cell, Function<Card, Boolean> checker, String ifSo, String ifNot) {
    return checkCard(cell, checker) ?ifSo :ifNot;
  }
  private static boolean checkCard(Cell cell, Function<Card, Boolean> checker) {
    return cell!=null &&
      !cell.isEmpty() &&
      cell.isCard()   &&
      checker.apply(cell.getCard());
  }

  private static char getCenter(Cell cell, boolean veryFirst, boolean veryLast) {
    if (veryFirst) return 'S';
    if (veryLast) return 'F';
    if (cell.isEmpty()) return ' ';
    if (cell.isBonus()) return 'B';
    if (cell.isKey()) return 'K';
    return '+';
  }
}

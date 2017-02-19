package main;

import java.util.function.Function;
public class AsciiBoard {

  public static void draw(Board board, Appendable app) throws Exception {
    final int width=board.getWidth(), height=board.getHeight();
    app.append(' ');
    for (int i=0; i<width -1 + (width*3); i++) app.append('_');
    app.append('\n');
    for (int r=0; r<height; r++){
      for (int innerLine=0; innerLine<3; innerLine++) {
        app.append('|');
        for (int c=0; c<width; c++){
          boolean first=r==0 && c==0;
          boolean last=r==height-1 && c==width-1;
          doInnerLine(app, innerLine, board.getCell(r, c), first, last);
        }
        app.append("\n");
      }
    }
  }

  private static void doInnerLine(
      Appendable app, int innerLine, Cell cell,
      boolean veryFirst, boolean veryLast
    ) throws Exception {
    switch (innerLine) {
      case 0:
        app.append(' ')
          .append(getEdge(cell, Card::hasPathUp, "| ", "  "));
        break;
      case 1:
        app.append(getEdge(cell, Card::hasPathLeft, "-", " "))
          .append(getCenter(cell, veryFirst, veryLast))
          .append(getEdge(cell, Card::hasPathRight, "-", " "));
        break;
      case 2:
        app.append('_')
          .append(getEdge(cell, Card::hasPathDown, "|_", "__"));
        break;
    }
    app.append("|");
  }


  private static String getEdge(Cell cell, Function<Card, Boolean> checker, String ifSo, String ifNot) {
    if (cell==Board.EMPTY) return ifNot;
    if (!cell.isCard()) return ifNot;
    if (!checker.apply(cell.getCard())) return ifNot;
    return ifSo;
  }
  private static char getCenter(Cell cell, boolean veryFirst, boolean veryLast) {
    if (veryFirst) return 'S';
    if (veryLast) return 'F';
    if (cell==Board.EMPTY) return ' ';
    if (cell.isBonus()) return 'B';
    if (cell.isKey()) return 'K';
    return '*';
  }
}

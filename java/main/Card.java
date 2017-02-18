package main;

/** Immutable */
public final class Card {

  public static Card strike() {
    return new Card(true, false, false, false, false);
  }
  public static Card path(boolean up, boolean right, boolean down, boolean left) {
    return new Card(false, up, right, down, left);
  }

  private final boolean up, down, left, right;
  private final boolean strike;
  private Card(boolean strike, boolean up, boolean right, boolean down, boolean left) {
    if (strike && (up || down || right || left))
      throw new IllegalArgumentException("Cannot be both strike & path");
    this.strike=strike;
    this.up=up;
    this.down=down;
    this.left=left;
    this.right=right;
  }

  public boolean isStrike() {return strike;}
  public boolean isPath() {return !strike;}

  public boolean isPathLeft() {return left;}
  public boolean isPathRight() {return right;}
  public boolean isPathUp() {return up;}
  public boolean isPathDown() {return down;}

  // DEBUGGING: //

  private String string=null;
  public String toString() {
    if (string==null) string=makeString();
    return string;
  }
  private String makeString() {
    if (isStrike()) return "S";
    StringBuilder sb=new StringBuilder();
    if (isPathUp()) sb.append("U");
    if (isPathDown()) sb.append("D");
    if (isPathLeft()) sb.append("L");
    if (isPathRight()) sb.append("R");
    return sb.toString();
  }

}

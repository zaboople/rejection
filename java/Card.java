public final class Card {
  public static Card strike() {
    return new Card(true, false, false, false, false);
  }
  public static Card path(boolean up, boolean right, boolean down, boolean left) {
    return new Card(false, up, right, down, left);
  }

  private boolean up, down, left, right;
  private boolean strike;
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

  public static void main(String[] args) {
    System.out.println("|");
    System.out.println("+");
    System.out.println("|");
  }
}

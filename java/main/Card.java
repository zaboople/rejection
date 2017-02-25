package main;

/** Immutable */
public final class Card {
  private final static int CORNER=0, BAR=1, TEE=2, CROSS=3;

  public static Card strike() {
    return new Card(true, (byte)-1, (byte)-1);
  }
  public static Card pathCorner() {
    return new Card(false, CORNER, Dir.LEFT);
  }
  public static Card pathBar() {
    return new Card(false, BAR, Dir.LEFT);
  }
  public static Card pathTee() {
    return new Card(false, TEE, Dir.LEFT);
  }
  public static Card pathCross() {
    return new Card(false, CROSS, Dir.LEFT);
  }

  // The hasRight... hasDown booleans are "effectively" final,
  // it's just more convenient to leave that out:
  private final boolean strike;
  private final int pathType;
  private final byte rotation;
  private boolean hasRight, hasLeft, hasUp, hasDown;

  private Card(boolean strike, int pathType, byte rotation) {
    if (strike && pathType!=-1)
      throw new IllegalArgumentException("Cannot be both strike & path");
    if (!strike && pathType!=CORNER && pathType!=BAR && pathType!=TEE && pathType!=CROSS)
      throw new IllegalArgumentException("Path type "+pathType+" is illegal");
    if (!strike &&
        rotation!=Dir.LEFT &&
        rotation!=Dir.RIGHT &&
        rotation!=Dir.UP &&
        rotation!=Dir.DOWN)
      throw new IllegalArgumentException("Rotation "+rotation+" is illegal");
    this.strike=strike;
    this.pathType=pathType;
    this.rotation=rotation;
    if (pathType==CROSS)
      hasRight=hasLeft=hasUp=hasDown=true;
    else
    if (pathType==BAR)
      hasUp=hasDown=!(
        hasRight=hasLeft=(rotation==Dir.LEFT || rotation ==Dir.RIGHT)
      );
    else
    if (pathType==TEE) {
      if (rotation==Dir.LEFT) hasLeft=hasDown=hasRight=true;
      if (rotation==Dir.UP)   hasDown=hasLeft=hasUp=true;
      if (rotation==Dir.RIGHT)hasLeft=hasUp=hasRight=true;
      if (rotation==Dir.DOWN) hasUp=hasRight=hasDown=true;
    }
    else
    if (pathType==CORNER) {
      if (rotation==Dir.LEFT) hasRight=hasDown=true;
      if (rotation==Dir.UP)   hasDown=hasLeft=true;
      if (rotation==Dir.RIGHT)hasLeft=hasUp=true;
      if (rotation==Dir.DOWN) hasUp=hasRight=true;
    }
  }
  public Card rotate() {
    if (strike) throw new IllegalStateException("Cannot rotate a strike");
    return new Card(
      strike, pathType,
      rotation==Dir.LAST
        ?Dir.FIRST
        :(byte)(rotation*2)
    );
  }

  public boolean isStrike() {return strike;}
  public boolean isPath() {return !strike;}

  public boolean isPathBar() {return pathType==BAR;}
  public boolean isPathCross() {return pathType==CROSS;}
  public boolean isPathCorner() {return pathType==CORNER;}
  public boolean isPathTee() {return pathType==TEE;}

  public boolean hasPathRight() {return hasRight;}
  public boolean hasPathLeft() {return hasLeft;}
  public boolean hasPathUp() {return hasUp;}
  public boolean hasPathDown() {return hasDown;}

  public boolean hasPath(byte direction) {
    switch (direction) {
      case Dir.LEFT: return hasLeft;
      case Dir.RIGHT: return hasRight;
      case Dir.UP: return hasUp;
      case Dir.DOWN: return hasDown;
      default: throw new RuntimeException("Invalid "+direction);
    }
  }


  // DEBUGGING: //

  private String string=null;
  public String toString() {
    if (string==null) string=makeString();
    return string;
  }
  private String makeString() {
    if (isStrike()) return "!";
    StringBuilder sb=new StringBuilder();
    if (isPathCorner()) sb.append("L");
    if (isPathTee()) sb.append("T");
    if (isPathBar()) sb.append("-");
    if (isPathCross()) sb.append("+");
    return sb.toString();
  }

}

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
  public byte hasPaths() {
    return (byte) (
      (hasLeft ? Dir.LEFT :0)
      |
      (hasRight ? Dir.RIGHT :0)
      |
      (hasUp ? Dir.UP :0)
      |
      (hasDown ? Dir.DOWN :0)
    );
  }
  public Card getOptimalRotationFor(byte canPlayTo, byte cameFrom) {
    boolean[] counter={false, false, false, false};
    if (pathType==CROSS) return this;
    if (pathType==BAR) {
      Card other=this.rotate();
      return other.intersectCount(canPlayTo, cameFrom) >
              this.intersectCount(canPlayTo, cameFrom)
        ?other
        :this;
    }
    Card bestCard=this;
    int bestCount=this.intersectCount(canPlayTo, cameFrom);
    Card nextCard=this;
    for (int i=0; i<3; i++) {
      nextCard=nextCard.rotate();
      int count=nextCard.intersectCount(canPlayTo, cameFrom);
      if (count > bestCount) {
        bestCard=nextCard;
        bestCount=count;
      }
    }
    return bestCard;
  }
  private int intersectCount(byte directions, byte cameFrom) {
    byte hasPaths=this.hasPaths();
    if (cameFrom != 0 && (hasPaths & cameFrom)==0) return -1;
    directions &=hasPaths;
    return
      count(directions, Dir.LEFT)
      +
      count(directions, Dir.RIGHT)
      +
      count(directions, Dir.UP)
      +
      count(directions, Dir.DOWN);
  }
  private int count(byte directions, byte direction) {
    return (directions & direction)==0 ?0 :1;
  }


  // DEBUGGING: //

  private String string=null;
  public String toString() {
    if (string==null) string=makeString();
    return string;
  }
  private String makeString() {
    if (isStrike()) return "!     ";
    StringBuilder sb=new StringBuilder();
    if (isPathCorner()) sb.append("L");
    if (isPathTee()) sb.append("T");
    if (isPathBar()) sb.append("-");
    if (isPathCross()) sb.append("+");
    sb.append(" ");
    sb.append(hasPathLeft() ? "L" :"_");
    sb.append(hasPathRight() ? "R" :"_");
    sb.append(hasPathDown() ? "D" :"_");
    sb.append(hasPathUp() ? "U" :"_");
    return sb.toString();
  }

}

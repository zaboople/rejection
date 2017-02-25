package main;

/** Immutable */
public final class Card {
  private final static int CORNER=0, BAR=1, TEE=2, CROSS=3;
  private final static int LEFT=0, UP=1, RIGHT=2, DOWN=3;

  public static Card strike() {
    return new Card(true, -1, -1);
  }
  public static Card pathCorner() {
    return new Card(false, CORNER, LEFT);
  }
  public static Card pathBar() {
    return new Card(false, BAR, LEFT);
  }
  public static Card pathTee() {
    return new Card(false, TEE, LEFT);
  }
  public static Card pathCross() {
    return new Card(false, CROSS, LEFT);
  }

  // The hasRight... hasDown booleans are "effectively" final,
  // it's just more convenient to leave that out:
  private final boolean strike;
  private final int pathType, rotation;
  private boolean hasRight, hasLeft, hasUp, hasDown;

  private Card(boolean strike, int pathType, int rotation) {
    if (strike && pathType!=-1)
      throw new IllegalArgumentException("Cannot be both strike & path");
    if (!strike && pathType!=CORNER && pathType!=BAR && pathType!=TEE && pathType!=CROSS)
      throw new IllegalArgumentException("Path type "+pathType+" is illegal");
    if (!strike && rotation!=LEFT && rotation!=RIGHT && rotation!=UP && rotation!=DOWN)
      throw new IllegalArgumentException("Roatation "+rotation+" is illegal");
    this.strike=strike;
    this.pathType=pathType;
    this.rotation=rotation;
    if (pathType==CROSS)
      hasRight=hasLeft=hasUp=hasDown=true;
    else
    if (pathType==BAR)
      hasUp=hasDown=!(
        hasRight=hasLeft=(rotation==LEFT || rotation ==RIGHT)
      );
    else
    if (pathType==TEE) {
      if (rotation==LEFT) hasLeft=hasDown=hasRight=true;
      if (rotation==UP)   hasDown=hasLeft=hasUp=true;
      if (rotation==RIGHT)hasLeft=hasUp=hasRight=true;
      if (rotation==DOWN) hasUp=hasRight=hasDown=true;
    }
    else
    if (pathType==CORNER) {
      if (rotation==LEFT) hasRight=hasDown=true;
      if (rotation==UP)   hasDown=hasLeft=true;
      if (rotation==RIGHT)hasLeft=hasUp=true;
      if (rotation==DOWN) hasUp=hasRight=true;
    }
  }
  public Card rotate() {
    if (strike) throw new IllegalStateException("Cannot rotate a strike");
    int r=rotation+1;
    if (r>3) r=0;
    return new Card(strike, pathType, r);
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

package main;
public class Dir {
  public static final byte LEFT=1;
  public static final byte UP=2;
  public static final byte RIGHT=4;
  public static final byte DOWN=8;

  public static final byte FIRST=1;
  public static final byte LAST=8;

  public final static byte[] OPPOSITES=new byte[LAST+1];
  static {
    for (int i=0; i<OPPOSITES.length; i++) OPPOSITES[i]=-1;
    OPPOSITES[LEFT]=RIGHT;
    OPPOSITES[UP]=DOWN;
    OPPOSITES[RIGHT]=LEFT;
    OPPOSITES[DOWN]=UP;
  }


  private static byte ALL=LEFT + RIGHT + UP + DOWN;
  public static boolean isLegal(byte dir) {
    return (ALL | dir) == ALL;
  }
  private static boolean isLegal(int dir) {
    return (ALL | (byte)dir) == ALL;
  }

  private final static int[] INDICES=new int[LAST+1];
  static {
    for (int i=0; i<INDICES.length; i++) INDICES[i]=-1;
    INDICES[LEFT]=0;
    INDICES[UP]=1;
    INDICES[RIGHT]=2;
    INDICES[DOWN]=3;
  }
  public static int index(byte direction) {
    return INDICES[direction];
  }

  public static void main(String[] args) {
    System.out.println(isLegal(RIGHT));
    System.out.println(isLegal(12));
    System.out.println(isLegal(77)+" "+isLegal(-1)+" "+isLegal(16));
  }
}
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
  public final static char[] DIR_TO_NAME=new char[LAST+1];
  static {
    for (int i=0; i<DIR_TO_NAME.length; i++) DIR_TO_NAME[i]='?';
    DIR_TO_NAME[LEFT]='L';
    DIR_TO_NAME[UP]='U';
    DIR_TO_NAME[RIGHT]='R';
    DIR_TO_NAME[DOWN]='D';
  }

}
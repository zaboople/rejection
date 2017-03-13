package main;
public class TTY {

  public final static String
    FG_RED      ="31",
    FG_GREEN    ="32",
    FG_BLUE     ="34"
    ;

  private final static String
    start="\u001B[",
    end="m";

  private final Appendable out;
  private final boolean colored;

  public TTY(Appendable out, boolean colored) {
    this.out=out;
    this.colored=colored;
  }
  public TTY append(String text) throws Exception {
    return add(text);
  }
  public TTY append(char text) throws Exception {
    out.append(text);
    return this;
  }
  public TTY add(String text) throws Exception {
    out.append(text);
    return this;
  }
  public TTY add(int text) throws Exception {
    out.append(String.valueOf(text));
    return this;
  }

  public TTY addGreen(String text) throws Exception {
    return addColored(FG_GREEN, text);
  }

  private TTY addColored(String color, String text) throws Exception {
    if (colored)
      out.append(text);
    else
      out.append(start).append(color).append(end).append(text).append(start).append('0').append(end);
    return this;
  }

  public static void main(String[] args) throws Exception {
    new TTY(System.out, true).addGreen("Hello world").add("\nHi\n");
    System.out.flush();
  }
}
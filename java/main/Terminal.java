package main;
public class Terminal {

  public final static String
    FG_RED      ="31",
    FG_GREEN    ="32",
    FG_BLUE     ="34",
    FG_MAGENTA  ="35",
    FG_DEFAULT  ="0"
    ;

  private final static String
    start="\u001B[",
    end="m";

  private final Appendable out;
  private final boolean colored;

  public Terminal(Appendable out, boolean colored) {
    this.out=out;
    this.colored=colored;
  }
  public Terminal append(String text) throws Exception {
    return add(text);
  }
  public Terminal append(char text) throws Exception {
    out.append(text);
    return this;
  }
  public Terminal add(String text) throws Exception {
    out.append(text);
    return this;
  }
  public Terminal add(int text) throws Exception {
    out.append(String.valueOf(text));
    return this;
  }

  public Terminal addGreen(String text) throws Exception {
    return addColored(FG_GREEN, text);
  }

  public Terminal addColored(String color, String text) throws Exception {
    if (!colored || color==null)
      out.append(text);
    else
      setColor(color).append(text).setColorDefault();
    return this;
  }
	public Terminal addColored(String color, char text) throws Exception {
    if (!colored || color==null)
      out.append(text);
    else
      setColor(color).append(text).setColorDefault();
    return this;
  }

  public Terminal setColor(String color) throws Exception {
    if (color!=null)
      out.append(start).append(color).append(end);
    return this;
  }

  public Terminal setColorDefault() throws Exception {
    out.append(start).append(FG_DEFAULT).append(end);
    return this;
  }

  public static void main(String[] args) throws Exception {
    new Terminal(System.out, true).addGreen("Hello world").add("\nHi\n");
    System.out.flush();
  }
}
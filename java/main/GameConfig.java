package main;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Configures the game according to a variety of tuneable parameters,
 * for testing/verification. Defaults should be reasonable.
 */
public class GameConfig {
  private static Pattern numberIsolator=Pattern.compile("\\D");

  public int
    BOARD_WIDTH=8,
    BOARD_HEIGHT=8,
    STRIKE_CARDS=6,
    STRIKE_LIMIT=3,
    KEYS=4,
    BONUSES=6,
    CARD_CORNERS=3,
    CARD_BARS=1,
    CARD_TEES=3,
    CARD_CROSSES=1;

  /** Sets things up with built-in default configuration. */
  public GameConfig() {
    ensureEnoughCards();
  }
  /** Configures using the given Properties. */
  public GameConfig(Properties props) throws InvalidPropertyException {
    for (String key: props.stringPropertyNames()){
      String s=key.toLowerCase();
      if (s.startsWith("board_w") || s.startsWith("w")) BOARD_WIDTH=getInt(props, key);
      else
      if (s.startsWith("board_h") || s.startsWith("h")) BOARD_HEIGHT=getInt(props, key);
      else
      if (s.startsWith("strike_c")) STRIKE_CARDS=getInt(props, key);
      else
      if (s.startsWith("strike_l")) STRIKE_LIMIT=getInt(props, key);
      else
      if (s.startsWith("k")) KEYS=getInt(props, key);
      else
      if (s.startsWith("bo")) BONUSES=getInt(props, key);
      else
      if (s.startsWith("card_corners") || s.startsWith("corner")) CARD_CORNERS=getInt(props, key);
      else
      if (s.startsWith("card_bars") || s.startsWith("bar")) CARD_BARS=getInt(props, key);
      else
      if (s.startsWith("card_tees") || s.startsWith("tee")) CARD_TEES=getInt(props, key);
      else
      if (s.startsWith("card_crosses") || s.startsWith("cross")) CARD_CROSSES=getInt(props, key);
      else
        throw new InvalidPropertyException("Don't know what to do with: "+key);
    }
    ensureEnoughCards();
  }
  public GameConfig(InputStream inStream) throws InvalidPropertyException, IOException {
    this(getProps(inStream));
  }

  //////////////////////
  // PRIVATE METHODS: //
  //////////////////////

  /**
   * Only _relative_ card counts have to be specified. We'll allocate according
   * to the total cards necessary and the ratio between the requested values.
   * (Note that strike cards have to be an actual card count and aren't affected here).
   */
  private void ensureEnoughCards() {
    int[] revised=ensureEnough(
      BOARD_WIDTH * BOARD_HEIGHT,
      CARD_BARS,
      CARD_CORNERS,
      CARD_CROSSES,
      CARD_TEES
    );
    CARD_BARS=revised[0];
    CARD_CORNERS=revised[1];
    CARD_CROSSES=revised[2];
    CARD_TEES=revised[3];
    if (CARD_CORNERS+CARD_BARS+CARD_TEES+CARD_CROSSES < (BOARD_WIDTH*BOARD_HEIGHT))
      throw new RuntimeException("Invalid configuration: There are less cards than there are cells to put them in.");
  }
  private int[] ensureEnough(int boardSize, int... typeCounts) {

    // Ratio:
    int ratioBottom=0;
    for (int tc: typeCounts) ratioBottom+=tc;
    if (ratioBottom==0) throw new IllegalArgumentException("All card counts are zero");

    // Allocate:
    int[] decided=new int[typeCounts.length];
    for (int i=0; i<typeCounts.length; i++)
      decided[i]=(boardSize * typeCounts[i]) / ratioBottom;

    // Off by 1 is the hardest part:
    int remaining=boardSize;
    for (int i: decided)
      remaining-=i;
    while (remaining>0)
      for (int i=0; remaining>0 && i<decided.length; i++)
        if (decided[i]!=0) {
          decided[i]++;
          remaining--;
        }

    return decided;
  }


  private static int getInt(Properties p, String key) throws InvalidPropertyException {
    String s=p.getProperty(key);
    if (s==null)
      throw new InvalidPropertyException("Null/blank value for: \""+key+"\"");
    s=numberIsolator.matcher(s).replaceAll("");
    try {
      return Integer.parseInt(s);
    } catch (Exception e) {
      throw new InvalidPropertyException("Key \""+key+"\" has invalid number: \""+s+"\"");
    }
  }

  private static class InvalidPropertyException extends Exception {
    public InvalidPropertyException(String msg) {super(msg);}
  }

  private static Properties getProps(InputStream inStream) throws IOException {
    Properties props=new Properties();
    props.load(new InputStreamReader(inStream));
    return props;
  }

}


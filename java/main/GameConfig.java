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
    CARD_CORNERS=24,
    CARD_BARS=8,
    CARD_TEES=24,
    CARD_CROSSES=8;

  public void ensureEnoughCards() {
    int leftOver=CARD_CORNERS + CARD_BARS + CARD_TEES + CARD_CROSSES - (BOARD_WIDTH * BOARD_HEIGHT);
    while (leftOver < 0) {
      if (leftOver < 0) {leftOver++;CARD_CORNERS++;}
      if (leftOver < 0) {leftOver++;CARD_BARS++;}
      if (leftOver < 0) {leftOver++;CARD_TEES++;}
      if (leftOver < 0) {leftOver++;CARD_CROSSES++;}
    }
  }
  public GameConfig load(java.io.InputStream inStream) throws InvalidPropertyException, IOException {
    Properties props=new Properties();
    props.load(new InputStreamReader(inStream));
    return load(props);
  }
  public GameConfig load(Properties props) throws InvalidPropertyException {
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
    return this;
  }

  private int getInt(Properties p, String key) throws InvalidPropertyException {
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
  private class InvalidPropertyException extends Exception {
    public InvalidPropertyException(String msg) {super(msg);}
  }
}


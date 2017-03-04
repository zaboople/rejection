package main;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class ConsolePlay {
  public static void main(String[] args) throws Exception {
    GameConfig config;
    if (args.length>0) {
      String a=args[0];
      File file=new File(a);
      if (!file.exists()) {
        System.out.println("Not a file: "+a);
        System.exit(1);
        return;
      }
      try {
        config=new GameConfig().load(new FileInputStream(file));
      } catch (Exception e) {
        System.out.println(e.getMessage());
        System.exit(1);
        return;
      }
    }
    else
      config=new GameConfig();
    new ConsolePlay(config).play();
  }


  private final BufferedReader reader;
  private final GameConfig config;
  private final String strikeStars;
  private final StringBuilder outBuffer=new StringBuilder();
  private Game game;

  private ConsolePlay(GameConfig config) throws Exception {
    this.config=config;
    reader=new BufferedReader(new InputStreamReader(System.in));
    {
      int strikeStarCount=
        (
          (config.BOARD_WIDTH * 4) + 1 - " STRIKE ".length()
        ) / 2;
      StringBuilder sb=new StringBuilder(strikeStarCount);
      for (int i=0; i<strikeStarCount; i++) sb.append("*");
      strikeStars=sb.toString();
    }
  }

  private void play() throws Exception {
    game=new Game(config);
    while (!game.isOver()){
      if (game.isWaiting()){
        game.nextCard();
      }
      else if (game.isWaitingStriked()) {
        prompt("Strike card hit. Press enter: ", null);
        game.ackStrike();
        game.nextCard();
      }
      else if (game.isCardUp()) {
        if (game.atVeryBeginning()){
          prompt("Press enter to play first card:", null);
          game.playFirstCard();
        }
        else
        if (!game.tryPlayCard())
          game.playCardWherever();
      }
      else if (game.isCardPlaced()) {
        Card card=game.getPlacedCard();
        promptCardRotate();
        game.finishPlayCard();
      }
    }
    drawGame();
    if (game.isLost()) outBuffer.append("LOSE");
    if (game.isWon()) outBuffer.append("******* WIN *******");
    if (game.isGiveUp()) outBuffer.append("Gave up");
    flush();
  }

  private void promptCardRotate() throws Exception {
    String error=null;
    boolean done=false;
    while (!done) {
      String res=prompt("[R]otate, [S]witch or [ ]Accept: ", error).toLowerCase();
      if (res.length()==0)
        done=true;
      else
      if (res.toLowerCase().startsWith("r"))
        game.rotateCard();
      else
      if (res.toLowerCase().startsWith("s"))
        game.switchPlayCard();
      else
        error = "Error: Invalid entry";
    }
  }

  private String prompt(String p, String error) throws Exception {
    drawGame();
    if (error!=null) outBuffer.append(error).append("     ");
    outBuffer.append(p);
    flush();
    return reader.readLine();
  }
  private void drawGame() throws Exception {
    AsciiBoard.draw(game.getBoard(), outBuffer);
    Card card=game.getUpCard();
    if (card!=null && card.isStrike())
      outBuffer.append(strikeStars).append(" STRIKE ").append(strikeStars).append("\n");
    else
      outBuffer.append("\n");
    outBuffer.append("Strikes: ").append(""+game.getStrikes()).append(" / ").append(""+game.getStrikeLimit()).append("\n");
    outBuffer.append("Keys:    ").append(""+game.getKeys()).append(" / ").append(""+game.getKeyLimit()).append("\n");
    outBuffer.append("Moved:   ").append(""+game.getMoved()).append("  ");
  }

  private void flush() {
    System.out.append(outBuffer);
    System.out.flush();
    outBuffer.setLength(0);
    System.gc();
  }

}

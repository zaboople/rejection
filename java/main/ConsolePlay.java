package main;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class ConsolePlay {

  /////////////////////////////////////////
  // STATIC COMMAND-LINE INITIALIZATION: //
  /////////////////////////////////////////

  public static void main(String[] args) throws Exception {
    if (!go(args)) System.exit(1);
  }
  private static boolean go(String[] args) throws Exception{
    GameConfig config=null;
    Gamble gamble=null;

    for (int i=0; i<args.length; i++)
      if (args[i].equals("-h") || args[i].equals("--help"))
        return help(null);
      else
      if (args[i].equals("-w") || args[i].equals("--wager")){
        i++;
        if (i==args.length)
          return help("Expected a number");
        Integer amount;
        try {amount=Integer.parseInt(args[i]);}
        catch (Exception e) {return help("Not a parseable number: "+args[i]);}
        gamble=new Gamble(amount);
      }
      else
      if (args[i].equals("-c") || args[i].equals("--config")){
        i++;
        if (i==args.length)
          return help("Error: Expected a filename");
        String a=args[i];
        File file=new File(a);
        if (!file.exists())
          return help("Not a file: "+a);
        try {
          config=new GameConfig().load(new FileInputStream(file));
        } catch (Exception e) {
          return help(e.getMessage());
        }
      }
      else
        return help("Unexpected: "+args[i]);

    if (config==null)
      config=new GameConfig();

    new ConsolePlay(config, gamble).play();
    return true;
  }
  private static boolean help(String error) {
    if (error!=null) System.out.println("Error: "+error);
    System.out.println("Usage: ConsolePlay [--config <file>] [--wager <amount>]");
    return error==null;
  }

  /////////////////////////////
  // PRIVATE INITIALIZATION: //
  /////////////////////////////

  private final BufferedReader reader;
  private final GameConfig config;
  private final Gamble gamble;
  private final String strikeStars;
  private final StringBuilder outBuffer=new StringBuilder();
  private Game game;

  private ConsolePlay(GameConfig config, Gamble gamble) throws Exception {
    this.config=config;
    this.gamble=gamble;
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

  ///////////////////////////
  // GAME-PLAYING SESSION: //
  ///////////////////////////

  private void play() throws Exception {
    boolean keepPlaying=true;
    while (keepPlaying){
      if (gamble!=null)
        gamble.bet=promptBet();
      boolean win=playOneGame();
      outBuffer.append("\n");
      if (gamble!=null){
        keepPlaying=gamble.winOrLose(win);
        outBuffer.append(
          keepPlaying
            ?"\nYou have $"+gamble.total+"\n"
            :"\nYou lost everything.\n"
        );
        flush();
      }
      if (keepPlaying)
        keepPlaying=promptReplay();
    }
  }
  private int promptBet() throws Exception {
    int bet=0;
    while (bet==0){
      outBuffer.append("Enter bet for this game, limit $"+gamble.total+": $");
      flush();
      String s=reader.readLine();
      try {bet=Integer.parseInt(s);}
      catch (Exception e) {}
      if (bet<=0 || bet>gamble.total)
        bet=0;
    }
    return bet;
  }
  private boolean promptReplay() throws Exception {
    while (true) {
      outBuffer.append("\nPlay again? Enter [Q]uit or [ ] to continue: ");
      flush();
      String s=reader.readLine().trim().toLowerCase();
      if (s.startsWith("q")){

        return false;
      }
      if (s.equals(""))
        return true;
    }
  }

  ///////////////////////////
  // INDIVIDUAL GAME PLAY: //
  ///////////////////////////

  private boolean playOneGame() throws Exception {
    game=new Game(config);
    while (!game.isOver()){
      if (game.isWaiting()){
        game.nextCard();
      }
      else if (game.isWaitingStriked()) {
        prompt("Strike card hit. Press enter: ");
        game.ackStrike();
        game.nextCard();
      }
      else if (game.isCardUp()) {
        if (game.atVeryBeginning()){
          promptFirstPlay();
          game.playFirstCard();
        }
        else
        if (!game.tryPlayCard())
          game.playCardWherever();
      }
      else if (game.isCardPlaced()) {
        Card card=game.getPlacedCard();
        promptCardAction();
        if (!game.isGiveUp())
          game.finishPlayCard();
      }
    }
    drawGame();
    if (game.isLost()) outBuffer.append("LOSE");
    if (game.isGiveUp()) outBuffer.append(" - Gave up");
    if (game.isWon())
      outBuffer.append(gamble == null ?"******* WIN *******" :"$$$$$$$ WIN $$$$$$$");
    return game.isWon();
  }

  private void promptFirstPlay() throws Exception {
    if (gamble!=null && gamble.bet<gamble.total){
      boolean done=false;
      while (!done) {
        String s=prompt("Enter [D]ouble down or [ ] to play first card:");
        if (s.equals(""))
          done=true;
        else
        if (s.startsWith("d")) {
          done=true;
          gamble.doubleDown();
        }
      }
    }
    else
      prompt("Press enter to play first card:");
  }
  private void promptCardAction() throws Exception {
    boolean done=false;
    while (!done) {
      String res=prompt("[R]otate, [S]witch, [G]ive up or [ ]Accept: ").trim().toLowerCase();
      if (res.length()==0)
        done=true;
      else
      if (res.startsWith("r"))
        game.rotateCard();
      else
      if (res.startsWith("s"))
        game.switchPlayCard();
      else
      if (res.startsWith("g")){
        game.giveUp();
        done=true;
      }
    }
  }

  private String prompt(String p) throws Exception {
    return prompt(p, true);
  }
  private String prompt(String p, boolean drawGame) throws Exception {
    if (drawGame) drawGame();
    outBuffer.append(p);
    if (!p.endsWith(" ")) outBuffer.append(" ");
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
    if (gamble!=null)
      outBuffer.append("Bet:    $").append(gamble.bet+" of $"+gamble.total).append("\n");
    outBuffer.append("Strikes: ").append(""+game.getStrikes()).append(" / ").append(""+game.getStrikeLimit()).append("\n");
    outBuffer.append("Keys:    ").append(""+game.getKeys()).append(" / ").append(""+game.getKeyLimit()).append("\n");
    // This is broken and who cares anyhow:
    //outBuffer.append("Moved:   ").append(""+game.getMoved()).append("  ");
  }

  private void flush() {
    System.out.append(outBuffer);
    System.out.flush();
    outBuffer.setLength(0);
    System.gc();
  }

}

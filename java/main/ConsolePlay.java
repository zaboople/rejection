package main;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;

public class ConsolePlay {

  private static class GameResult {
    final boolean won, bonus;
    GameResult(boolean won, boolean bonus) {
      this.won=won;
      this.bonus=bonus;
    }
  }

  /////////////////////////////////////////
  // STATIC COMMAND-LINE INITIALIZATION: //
  /////////////////////////////////////////

  private static AsciiBoard boardRender;

  public static void main(String[] args) throws Exception {
    GameConfigSetup setup = new GameConfigSetup();
    if (!setup.go(args)) System.exit(1);
    new ConsolePlay(setup.config, setup.gamble).play();
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
    boardRender=new AsciiBoard(System.out, true);
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
      GameResult result=playOneGame();
      outBuffer.append("\n");
      if (gamble!=null){
        keepPlaying=gamble.winOrLose(result.won, result.bonus);
        outBuffer.append(
          keepPlaying
            ?"\nYou have $"+gamble.getTotal()+"\n"
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
      outBuffer.append("Enter bet for this game, limit $"+gamble.getTotal()+": $");
      flush();
      String s=reader.readLine();
      try {bet=Integer.parseInt(s);}
      catch (Exception e) {}
      if (bet<=0 || bet>gamble.getTotal())
        bet=0;
    }
    return bet;
  }
  private boolean promptReplay() throws Exception {
    while (true) {
      outBuffer.append("\nPlay again? Enter [Q]uit or [ ] to continue: ");
      flush();
      String s=reader.readLine().trim().toLowerCase();
      if (s.startsWith("q"))
        return false;
      if (s.equals(""))
        return true;
    }
  }

  ///////////////////////////
  // INDIVIDUAL GAME PLAY: //
  ///////////////////////////

  private GameResult playOneGame() throws Exception {
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

    GameResult gr=new GameResult(game.isWon(), game.allCovered());
    if (game.isLost()) outBuffer.append("LOSE");
    if (game.isGiveUp()) outBuffer.append(" - Gave up");

    if (game.isWon()) {
      String stars=gamble==null ?"*******" :"$$$$$$$";
      String title=gamble!=null && gr.bonus ?" BONUS WIN " :" WIN ";
      outBuffer.append(stars).append(title).append(stars);
    }
    return gr;
  }

  private void promptFirstPlay() throws Exception {
    if (gamble!=null && gamble.bet<gamble.getTotal()){
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
    boardRender.draw(game.getBoard(), outBuffer);
    Card card=game.getUpCard();
    if (card!=null && card.isStrike())
      outBuffer.append(strikeStars).append(" STRIKE ").append(strikeStars).append("\n");
    else
      outBuffer.append("\n");

    final int
      keys=game.getKeys(),
      keyLimit=game.getKeyLimit(),
      strikes=game.getStrikes(),
      strikeLimit=game.getStrikeLimit();
    outBuffer.append("Keys:    ").append(""+keys).append(" / ").append(""+keyLimit)
      .append(keys==keyLimit ?" ******\n" :"\n");
    outBuffer.append("Strikes: ").append(""+strikes).append(" / ").append(""+strikeLimit)
      .append(strikes==strikeLimit-1 ?" !!!!!!\n" :"\n");
    if (gamble!=null)
      outBuffer.append("Bet:    $").append(gamble.bet+" of $"+gamble.getTotal()).append("\n");
  }

  private void flush() {
    System.out.append(outBuffer);
    System.out.flush();
    outBuffer.setLength(0);
    System.gc();
  }

}

package main.cmdline;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;
import main.Card;
import main.Game;
import main.GameConfig;
import main.Gamble;

/**
 * Primary control system for the command-line game. Most of the
 * core game state mgmt remains in the Game class.
 */
public class ConsolePlay {

  /** Minor utility class */
  private static class GameResult {
    final boolean won, bonus;
    GameResult(boolean won, boolean bonus) {
      this.won=won;
      this.bonus=bonus;
    }
  }

  /////////////////////
  // INITIALIZATION: //
  /////////////////////

  private final AsciiBoard boardRender;
  private final BufferedReader reader;
  private final GameConfig config;
  private final Gamble gamble;
  private final String strikeStars;
  private final StringBuilder outBuffer=new StringBuilder();
  private Game game;

  public ConsolePlay(GameConfig config, Gamble gamble) throws Exception {
    boardRender=new AsciiBoard(System.out, true);
    this.config=config;
    this.gamble=gamble;
    reader=new BufferedReader(new InputStreamReader(System.in));
    int strikeStarCount=
      (
        (config.BOARD_WIDTH * 4) + 1 - " STRIKE ".length()
      ) / 2;
    StringBuilder sb=new StringBuilder(strikeStarCount);
    for (int i=0; i<strikeStarCount; i++) sb.append("*");
    strikeStars=sb.toString();
  }

  ///////////////////////////
  // GAME-PLAYING SESSION: //
  ///////////////////////////

  public void play() throws Exception {
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
      if (game.isGameStart()){
        promptFirstPlay();
        game.nextCard();
      }
      else
      if (game.isWaiting()){
        game.nextCard();
      }
      else if (game.isWaitingStriked()) {
        prompt("Strike card hit. Press enter: ");
        game.ackStrike();
      }
      else
      if (game.isCardUp())
        game.playCardWherever();
      else
      if (game.isCardPlaced()) {
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
    if (gamble!=null && gamble.canDoubleDown()){
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
    final int
      keys=game.getKeysCrossed(),
      keyLimit=game.getKeyLimit(),
      strikes=game.getStrikes(),
      strikeLimit=game.getStrikeLimit();
    boardRender.draw(game.getBoard(), outBuffer);

    // On this if condition, the latter case in the || is that we lost on strikes
    // so we've gone past the "struck" state to Lose:
    if (game.isWaitingStriked() || (game.isLost() && strikes==strikeLimit))
      outBuffer.append(strikeStars).append(" STRIKE ").append(strikeStars).append("\n");
    else
      outBuffer.append("\n");

    outBuffer
      .append("Keys:    ").append(""+keys).append(" / ").append(""+keyLimit)
      .append(keys==keyLimit ?" ******\n" :"\n")
      .append("Strikes: ").append(""+strikes).append(" / ").append(""+strikeLimit)
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

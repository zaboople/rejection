package main;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;

public class ConsolePlay {
  public static void main(String[] args) throws Exception {
    GameConfig config;
    if (args.length>0) {
      String a=args[0].toLowerCase();
      if (a.startsWith("--")) a=a.substring(1);
      if (a.equals("-small")) config=new GameConfigTiny();
      else
      if (a.equals("-large")) config=new GameConfigBig();
      else {
        System.out.println("ERROR: "+args[0]);
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
  private Game game;

  private ConsolePlay(GameConfig config) throws Exception {
    this.config=config;
    reader=new BufferedReader(new InputStreamReader(System.in));
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
    if (game.isLost()) System.out.print("LOSE");
    if (game.isWon()) System.out.print("******* WIN *******");
    if (game.isGiveUp()) System.out.print("Gave up");
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
    if (error!=null) System.out.append(error).append("     ");
    System.out.print(p);
    return reader.readLine();
  }
  private void drawGame() throws Exception {
    AsciiBoard.draw(game.getBoard(), System.out);
    Card card=game.getUpCard();
    if (card!=null && card.isStrike())
      System.out.println("******************** STRIKE ********************");
    else
      System.out.println("");
    System.out.append("Strikes: ").append(""+game.getStrikes()).append(" / ").append(""+game.getStrikeLimit()).append("\n");
    System.out.append("Keys:    ").append(""+game.getKeys()).append(" / ").append(""+game.getKeyLimit()).append("\n");
    System.out.append("Moved:   ").append(""+game.getMoved()).append("  ");
    System.out.flush();
  }

  /** No longer in use */
  private void drawUpCard(Card card) throws Exception {
    if (card.isStrike()) {
      System.out.println("       ___");
      System.out.println("Card: |\\ /|");
      System.out.println("      | x |");
      System.out.println("      |/_\\|");
    } else {
      char
        uChar=card.hasPathUp() ? '|' : ' ',
        dChar=card.hasPathDown() ? '|' : '_',
        lChar=card.hasPathLeft() ? '-' : ' ',
        rChar=card.hasPathRight() ? '-' : ' ';
      System.out.append("       ___").append("\n");
      System.out.append("Card: | ").append(uChar).append(" |\n");
      System.out.append("      |").append(lChar).append(" ").append(rChar).append("|\n");
      System.out.append("      |_").append(dChar).append("_|\n");
    }
  }
}

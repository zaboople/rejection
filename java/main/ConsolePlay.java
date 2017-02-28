package main;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;

public class ConsolePlay {
  public static void main(String[] args) throws Exception {
    new ConsolePlay().play();
  }

  private final static Map<Character, Integer> directionMap=new HashMap<>();
  static {
    //up/down/left/right array:
    directionMap.put('u', 0);
    directionMap.put('d', 1);
    directionMap.put('l', 2);
    directionMap.put('r', 3);
  }
  private final static String[] directions={"Up", "Down", "Left", "Right"};


  private final BufferedReader reader;
  private Game game;



  private ConsolePlay() throws Exception {
    reader=new BufferedReader(new InputStreamReader(System.in));
  }

  private void play() throws Exception {
    game=new Game();
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
    if (game.isWon()) System.out.print("WIN");
    if (game.isGiveUp()) System.out.print("Gave up");
  }

  private void promptCardPut() throws Exception {
    boolean[] directions={
      game.canPlayUp(),
      game.canPlayDown(),
      game.canPlayLeft(),
      game.canPlayRight()
    };
    boolean placed=false;
    String error=null;
    while (!placed) {
      String direction=prompt("Enter direction to place card (Up/Down/Left/Right): ", error);
      if (direction.length()==0) {
        error="Error: No entry";
        continue;
      }
      Integer index=directionMap.get(direction.toLowerCase().charAt(0));
      if (index==null || index==-1) {
        error="Invalid entry; type \"up\", \"down\", \"right\" or \"left\", (or just \"u\", \"d\", \"r\" or \"l\"):";
        continue;
      }
      if (!directions[index]) {
        error="Can't go that direction";
        continue;
      }
      switch (index) {
        case 0: game.playUp(); break;
        case 1: game.playDown(); break;
        case 2: game.playLeft(); break;
        case 3: game.playRight(); break;
      }
      placed=true;
    }
  }
  private void promptCardRotate() throws Exception {
    String error=null;
    boolean done=false;
    while (!done) {
      String res=prompt("Enter R to rotate, S to switch or nothing to continue: ", error).toLowerCase();
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
    System.out.append("Moved: ")
      .append(String.valueOf(game.getMoved()))
      .append("  ");
    if (error!=null) System.out.append(error).append("     ");
    System.out.print(p);
    return reader.readLine();
  }
  private void drawGame() throws Exception {
    AsciiBoard.draw(game.getBoard(), System.out);
    Card card=game.getUpCard();
    if (card!=null)
      drawUpCard(card);
    else
      System.out.println("\n\n\n");
    System.out.println("Strikes: "+game.getStrikes()+" / "+game.getStrikeLimit());
    System.out.flush();
  }
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

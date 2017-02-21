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
    game=new Game(0);
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
          promptCardPut();
      }
      else if (game.isCardPlaced()) {
        Card card=game.getPlacedCard();
        if (card.isPathCorner() || card.isPathTee())
          promptCardRotate();
        game.finishPlayCard();
      }
    }
    if (game.isLost()) System.out.println("LOSE");
    if (game.isWon()) System.out.println("WIN");
    if (game.isGiveUp()) System.out.println("Gave up");
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
      if (index==-1) {
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
      String res=prompt("Enter R to rotate, or nothing to continue: ", error).toLowerCase();
      if (res.length()==0)
        done=true;
      else
      if (res.toLowerCase().startsWith("r")){
        game.rotateCard();
        drawGame();
      }
      else
        error = "Error: Invalid entry";
    }
  }

  private String prompt(String p, String error) throws Exception {
    drawGame();
    if (error!=null) System.out.println(error);
    System.out.print(p);
    return reader.readLine();
  }
  private void drawGame() throws Exception {
    AsciiBoard.draw(game.getBoard(), System.out);
    Card card=game.getUpCard();
    if (card!=null)
      drawUpCard(card);
    System.out.println("Strikes: "+game.getStrikes());
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

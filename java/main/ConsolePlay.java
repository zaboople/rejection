package main;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsolePlay {
  public static void main(String[] args) throws Exception {
    new ConsolePlay().play();
  }

  private BufferedReader reader;
  private ConsolePlay() throws Exception {
    reader=new BufferedReader(new InputStreamReader(System.in));
  }

  private void play() throws Exception {
    Game game=new Game(0);
    while (!game.isOver()){
      if (game.isWaiting()){
        game.nextCard();
        draw(game);
      }
      else if (game.isWaitingStriked()) {
        readLine();
      }
      else if (game.isCardUp()) {
        String place=readLine();
      }
    }
  }
  private void draw(Game game) throws Exception {
    AsciiBoard.draw(game.getBoard(), System.out);
    System.out.println("STRIKES: "+game.getStrikes());
    System.out.flush();
  }
  private String readLine() throws Exception {
    return reader.readLine();
  }
}
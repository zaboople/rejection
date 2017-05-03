package main.swang;
import main.GameConfigSetup;
import main.GameConfig;
import main.Gamble;
import main.Game;

public class ScreenPlay implements ScreenPlayInterface {
  public static void main(String[] args) throws Exception {
    GameConfigSetup setup=new GameConfigSetup();
    if (!setup.go(args)) System.exit(1);
    else play(setup.config, setup.gamble);
  }

  private static void play(GameConfig config, Gamble gamble) throws Exception {
    Screen.startup(new ScreenPlay(config, gamble));
  }

  private final GameConfig config;
  private final Gamble gamble;
  private Screen screen;
  private Game game;

  public ScreenPlay(GameConfig config, Gamble gamble) {
    this.config=config;
    this.gamble=gamble;
  }

  /////////////
  // EVENTS: //
  /////////////

  public @Override void init(Screen screen) {
    this.screen=screen;
    startGame();
  }

  public @Override void betEntered(String bet) {
    int amount=-1;
    try {
      amount=Integer.parseInt(bet);
    } catch (Exception e) {
      return;
    }
    if (amount > gamble.getTotal())
      return;
    gamble.setBet(amount);
    screen.setStatePlay(gamble, game.getBoard());
  }

  public @Override void moveEntered(String move) {
    move=move.toLowerCase();
    if (move.equals("s")){
    }
    else
    if (move.equals("r")) {
    }
    screen.setStateNextMove();
  }

  //////////////////////
  // PRIVATE METHODS: //
  //////////////////////

  private void startGame() {
    game=new Game(config);
    if (gamble!=null)
      screen.setStateBet(gamble.getTotal());
    else
      screen.setStatePlay(gamble, game.getBoard());
  }

}
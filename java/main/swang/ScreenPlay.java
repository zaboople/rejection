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
    if (amount <= gamble.getTotal()) {
      gamble.setBet(amount);
      nextCard();
      screen.setGameState(game.getState(), gamble);
    }
    else
      screen.setStateBet(gamble.getTotal());
  }

  public @Override void moveEntered(String move) {
    move=move.toLowerCase().trim();
    System.out.println("MOVE "+move);
    if (game.isWaiting()) {
      nextCard();
      screen.setGameState(game.getState(), gamble);
    }
    else
    if (game.isWaitingStriked()) {
      game.ackStrike();
      nextCard();
      screen.setGameState(game.getState(), gamble);
    }
    if (game.firstCardUp()) {
      boolean doubled="d".equals(move);
      boolean valid=doubled || "".equals(move) || gamble==null;
      if (gamble!=null && gamble.canDoubleDown() && doubled)
        gamble.doubleDown();
      if (valid)
        game.playFirstCard();
      screen.setGameState(game.getState(), gamble);
    }
    else
    if (game.isCardUp()) {
      game.playCardWherever();
      screen.setGameState(game.getState(), gamble);
    }
    else
    if (game.isCardPlaced()) {
      if (move.equals("s"))
        game.switchPlayCard();
      else
      if (move.equals("r"))
        game.rotateCard();
      else
      if (move.equals("g"))
        game.giveUp();
      else
      if (move.equals("")) {
        game.finishPlayCard();
        nextCard();
      }
      screen.setGameState(game.getState(), gamble);
    }
  }


  public @Override void playAgainEntered(String choice) {
    choice=choice.toLowerCase().trim();
    if (choice.equals(""))
      startGame();
    else
      System.exit(0);
  }

  //////////////////////
  // PRIVATE METHODS: //
  //////////////////////

  private void startGame() {
    game=new Game(config);
    screen.setBoard(game.getBoard());
    if (gamble!=null)
      screen.setStateBet(gamble.getTotal());
    else {
      nextCard();
      screen.setGameState(game.getState(), gamble);
    }
  }

  private void nextCard() {
    if (game.isWaiting()) {
      game.nextCard();
      if (game.isCardUp() && !game.firstCardUp())
        game.playCardWherever();
    }
  }

}
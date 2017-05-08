package main.swang;
import main.GameConfigSetup;
import main.GameConfig;
import main.Gamble;
import main.Game;
import main.GameState;

/**
 * Screen play attempts to control as much of the interaction as possible
 * although Screen really ends up being the mirror to it.
 */
public class ScreenPlay implements ScreenPlayInterface {

  /** Entry point for the whole shebang. */
  public static void main(String[] args) throws Exception {
    GameConfigSetup setup=new GameConfigSetup();
    if (!setup.go(args))
      System.exit(1);
    else
      Screen.startup(new ScreenPlay(setup.config, setup.gamble), false);
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

  /** The initial callback from Screen after it's bootstrapped itself in. */
  public @Override void init(Screen screen) {
    this.screen=screen;
    startBet();
  }

  /**
   * This is the verymost beginning of a game. If we are gambling, the corresponding callback
   * is betEntered(), otherwise we hop straight to startGame().
   */
  private void startBet() {
    screen.setBoard(null);
    if (gamble!=null)
      screen.setStateBet(gamble.getTotal());
    else
      startGame();
  }

  /** If this callback succeeds, we advance to startGame(). */
  public @Override void betEntered(String bet) {
    int amount=-1;
    try {
      amount=Integer.parseInt(bet.trim().replaceAll("\\D", ""));
    } catch (Exception e) {
      return;
    }
    if (amount <= gamble.getTotal()) {
      gamble.setBet(amount);
      startGame();
    }
    else
      screen.setStateBet(gamble.getTotal());
  }

  /**
   * Creates a new game after betting is done, and sets up the first callback to
   * moveEntered.
   */
  private void startGame() {
    game=new Game(config);
    screen.setBoard(game.getBoard());
    updateDisplay();
  }

  /**
   * After bet/game start, this acts as launch point to set up the Screen class
   * with the latest state and wait for the callback to moveEntered().
   */
  private void updateDisplay() {
    screen.nextState(game.getState(), gamble, game.getBoard().onFinish());
  }

  /**
   * This is called _after_ user input, so the goal is to advance
   * to the next state or ask them to try again because input was invalid.
   */
  public @Override void moveEntered(String move) {
    move=move.toLowerCase().trim();
    GameState state=game.getState();
    if (state.isGameStart()) {
      boolean doubled="d".equals(move);
      boolean valid=doubled || "".equals(move);
      if (gamble!=null && gamble.canDoubleDown() && doubled) {
        gamble.doubleDown();
        screen.setBet(gamble);
      }
      if (valid)
        advanceState();
      updateDisplay();
    }
    else
    if (game.isWaiting()) {
      advanceState();
      updateDisplay();
    }
    else
    if (game.isWaitingStriked()) {
      game.ackStrike();
      advanceState();
      updateDisplay();
    }
    else
    if (game.isCardUp()) {
      game.playCardWherever();
      updateDisplay();
    }
    else
    if (game.isCardPlaced()) {
      if (move.equals("s"))
        game.switchPlayCard();
      else
      if (move.equals("r"))
        game.rotateCard();
      else
      if (move.equals("g")) {
        game.giveUp();
        advanceState();
      }
      else
      if (move.equals("a") || move.equals("")) {
        game.finishPlayCard();
        advanceState();
      }
      updateDisplay();
    }
    else
    if (game.isOver()) {
      if (move.equals("q"))
        System.exit(0);
      else
      if (move.equals(""))
        startBet();
      else
        // Bad input:
        updateDisplay();
    }
  }

  /**
   * Advances us through state changes until we are ready to get more input.
   * 1. Advances to the next card if the game isn't over. Plays the card
   *    to the "best-looking" spot if it's not the first move.
   * 2. If the previous caused us to lose the game (on strikes), or if the game was
   *    already over, updates the gamble status.
   */
  private void advanceState() {
    if (game.isWaiting() || game.isGameStart()) {
      game.nextCard();
      if (game.isCardUp())
        game.playCardWherever();
    }
    if (game.isOver() && gamble!=null)
      gamble.winOrLose(game.isWon(), game.allCovered());
  }

}

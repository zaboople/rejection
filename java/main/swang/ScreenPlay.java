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
    startPlay();
  }

  public @Override void moveEntered(String move) {
    if (game.isWaitingStriked()) {
      game.ackStrike();
      playNext();
    }
    else
    if (game.firstCardUp()) {
      game.playFirstCard();
      playNext();
    }
    else {
      move=move.toLowerCase();
      if (move.equals("s")){
      }
      else
      if (move.equals("r")) {
      }
      screen.setStateNextMove();
    }
  }

  //////////////////////
  // PRIVATE METHODS: //
  //////////////////////

  private void startGame() {
    game=new Game(config);
    if (gamble!=null)
      screen.setStateBet(gamble.getTotal());
    else
      startPlay();
  }
  private void startPlay() {
    screen.setStatePlay(gamble, game.getBoard());
    playNext();
  }
  private void playNext() {
    if (game.isOver()){
      System.out.println("OVER!");
    }
    else if (game.isWaitingStriked()) {
      screen.setStrikes(game.getStrikes(), game.getStrikeLimit());
      screen.setStateStrike();
    }
    else if (game.isWaiting()){
      game.nextCard();
      playNext();
    }
    /*
    else if (game.isCardUp()) {
      if (game.atVeryBeginning())
        screen.promptFirstPlay();
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


    game.nextCard();
    while (game.isWaitingStriked()) {
      game.ackStrike();
      game.nextCard();
    }
    game.playFirstCard();
    */

  }

}
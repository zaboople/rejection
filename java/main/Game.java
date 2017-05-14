package main;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.security.SecureRandom;
import static main.GameState.GAME_START;
import static main.GameState.WAITING;
import static main.GameState.WAITING_STRIKED;
import static main.GameState.CARD_UP;
import static main.GameState.CARD_PLACED;
import static main.GameState.LOST;
import static main.GameState.WON;
import static main.GameState.GIVE_UP;


/**
 * Represents game state. Tries as much as possible to hide
 * board state from game clients, although the board is still
 * exposed for the sake of rendering.
 */
public class Game {

  // Final variables:
  private final SecureRandom randomizer=new SecureRandom();
  private final Board board;
  private final Deck deck;
  private final GameConfig config;

  // Stateful variables:
  private GameState state;
  private byte prevDirection;
  private Card upCard=null;
  private int moves=0; //Not really used

  public Game(GameConfig c) {
    this.config=c;
    this.state=new GameState(config.STRIKE_LIMIT, config.KEYS);
    board=new Board(randomizer, c.BOARD_WIDTH, c.BOARD_HEIGHT).reset(c.KEYS, c.BONUSES);
    deck=new Deck(
      randomizer,
      c.STRIKE_CARDS, c.CARD_CORNERS, c.CARD_BARS, c.CARD_TEES, c.CARD_CROSSES
    );
  }

  /**
   * Exposes a read-only BoardView for rendering.
   */
  public BoardView getBoard() {
    return board;
  }

  public void nextCard() {
    state.require(WAITING | GAME_START);
    upCard=deck.next();
    if (upCard.isStrike()){
      state.addStrike();
      state.set(
        state.getStrikeCount()==state.getStrikeLimit()
          ?LOST :WAITING_STRIKED
      );
    }
    else
      state.set(CARD_UP);
  }

  public void playCardWherever() {
    state.require(CARD_UP);
    if (board.hasCurrent()) {
      byte options=board.whereCanIPlayTo();
      if (prevDirection > 0 && (options & prevDirection)!=0) play(prevDirection);
      else
      if ((options & Dir.RIGHT)!=0) play(Dir.RIGHT);
      else
      if ((options & Dir.DOWN)!=0) play(Dir.DOWN);
      else
      if ((options & Dir.UP)!=0) play(Dir.UP);
      else
      if ((options & Dir.LEFT)!=0) play(Dir.LEFT);
      else
        throw new IllegalStateException("Invalid play options: "+options);
    }
    else {
      board.playFirstCard(upCard);
      setPlaced();
    }
  }

  public void playUp() {play(Dir.UP);}
  public void playDown() {play(Dir.DOWN);}
  public void playLeft() {play(Dir.LEFT);}
  public void playRight() {play(Dir.RIGHT);}

  public void rotateCard() {
    state.require(CARD_PLACED);
    board.rotateCard();
  }
  public void switchPlayCard() {
    byte temp=board.switchPlay();
    if (temp!=0) prevDirection=temp;
  }
  public void finishPlayCard() {
    state.require(CARD_PLACED);
    moves++;

    if (board.onKey())
      state.addKeysCrossed();
    else
    if (board.onBonus())
      state.removeStrike();

    if (board.onFinish())
      state.set(
        state.getKeysCrossed()==state.getKeys()
          ?WON :LOST
      );
    else
    if (board.whereCanIPlayTo()==0)
      state.set(LOST);
    else
      state.set(WAITING);
  }
  public void ackStrike() {
    state.require(WAITING_STRIKED);
    state.set(WAITING);
  }
  public void ackStrikeNextCard() {
    ackStrike();
    nextCard();
  }
  public void giveUp() {
    state.set(GIVE_UP);
  }

  public boolean allCovered() {
    for (int i=0; i<board.getCellCount(); i++)
      if (board.getCard(i)==null)
        return false;
    return true;
  }


  public boolean canPlayUp() {return canPlay(Dir.UP);}
  public boolean canPlayDown() {return canPlay(Dir.DOWN);}
  public boolean canPlayLeft() {return canPlay(Dir.LEFT);}
  public boolean canPlayRight() {return canPlay(Dir.RIGHT);}

  public GameState getState(){return state;}

  // These are "convenience" methods or really just leftovers where
  // I didn't feel like going all through ConsolePlay and changing the calls:
  public int getStrikes() {return state.getStrikeCount();}
  public int getKeysCrossed() {return state.getKeysCrossed();}
  public int getStrikeLimit() {return state.getStrikeLimit();}
  public int getKeyLimit() {return state.getKeys();}

  public boolean isGameStart(){return state.isGameStart();}
  public boolean isWaiting(){return state.isWaiting();}
  public boolean isWaitingStriked(){return state.isWaitingStriked();}
  public boolean isCardUp(){return state.isCardUp();}
  public boolean isCardPlaced(){return state.isCardPlaced();}
  public boolean isOver() {return state.isOver();}
  public boolean isGiveUp(){return state.isGiveUp();}
  public boolean isWon(){return state.isWon();}
  public boolean isLost(){return state.isLost();}

  //////////////
  // PRIVATE: //
  //////////////

  private void play(byte direction) {
    state.require(CARD_UP);
    board.play(upCard, direction);
    prevDirection=direction;
    setPlaced();
  }
  private void setPlaced() {
    state.set(CARD_PLACED);
    upCard=null;
  }
  private boolean canPlay(byte direction) {
    if (!board.hasCurrent()) return false;
    state.require(CARD_UP);
    return board.canPlay(direction);
  }

}

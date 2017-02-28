package main;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.security.SecureRandom;

/**
 * Represents game state. Tries as much as possible to hide
 * board state from game clients, although the board is still
 * exposed for the sake of rendering.
 */
public class Game {

  private static final int
    WAITING=0,
    WAITING_STRIKED=1,
    CARD_UP=2,
    CARD_PLACED=3,
    LOST=4,
    WON=5,
    GIVE_UP=6;

  // Final variables:
  private final SecureRandom randomizer=new SecureRandom();
  private final Board board;
  private final Deck deck;
  private final GameConfig config;

  // Stateful variables:
  private Card upCard=null;
  private int state=WAITING;
  private boolean onFirstCard=true;
  private int strikes=0;
  private int keys=0;
  private int moved=0;

  public Game() {
    this(new GameConfig());
  }
  public Game(GameConfig c) {
    this.config=c;
    board=new Board(
      randomizer, c.BOARD_WIDTH, c.BOARD_HEIGHT, c.KEY_COUNT, c.BONUS_COUNT
    );
    deck=new Deck(
      randomizer, c.STRIKE_CARDS,
      c.CARD_CORNER_COUNT, c.CARD_BAR_COUNT, c.CARD_TEE_COUNT, c.CARD_CROSS_COUNT
    );
  }

  /** Ideally this would be a read-only interface. */
  public Board getBoard() {
    return board;
  }

  public Card getUpCard() {
    if (upCard==null && state==CARD_UP)
      throw new IllegalStateException("Up card should not be null");
    return upCard;
  }

  public void nextCard() {
    requireState(WAITING);
    upCard=deck.next();
    if (upCard.isStrike()) {
      strikes++;
      state=strikes==config.STRIKE_LIMIT ?LOST :WAITING_STRIKED;
    } else {
      state=CARD_UP;
    }
  }

  public boolean tryPlayCard() {
    requireState(CARD_UP);
    byte options=board.whereCanIPlayTo();
    boolean played=true;
    if ((options ^ Dir.RIGHT)==0) play(Dir.RIGHT);
    else
    if ((options ^ Dir.DOWN)==0) play(Dir.DOWN);
    else
    if ((options ^ Dir.UP)==0) play(Dir.UP);
    else
    if ((options ^ Dir.LEFT)==0) play(Dir.LEFT);
    else
      played=false;
    return played;
  }
  public void playCardWherever() {
    requireState(CARD_UP);
    byte options=board.whereCanIPlayTo();
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

  public void playUp() {play(Dir.UP);}
  public void playDown() {play(Dir.DOWN);}
  public void playLeft() {play(Dir.LEFT);}
  public void playRight() {play(Dir.RIGHT);}

  public void rotateCard() {
    requireState(CARD_PLACED);
    moved++;
    board.rotateCard();
  }
  public boolean switchPlayCard() {
    return board.switchPlay();
  }
  public void finishPlayCard() {
    requireState(CARD_PLACED);
    if (board.onKey())
      keys++;
    else if (board.onBonus())
      strikes=strikes==0 ?0 :strikes-1;
    if (board.onFinish())
      state=keys==config.KEY_COUNT ?WON :LOST;
    else
    if (board.whereCanIPlayTo()==0)
      state=LOST;
    else
      state=WAITING;
  }
  public Game ackStrike() {
    requireState(WAITING_STRIKED);
    state=WAITING;
    return this;
  }
  public Game giveUp() {
    state=GIVE_UP;
    return this;
  }

  public void playFirstCard() {
    requireState(CARD_UP);
    if (!onFirstCard) throw new IllegalStateException("Not on very first");
    board.playFirstCard(upCard);
    moved++;
    onFirstCard=false;
    state=CARD_PLACED;
    upCard=null;
  }


  public boolean atVeryBeginning() {return onFirstCard;}
  public boolean canPlayUp() {return canPlay(Dir.UP);}
  public boolean canPlayDown() {return canPlay(Dir.DOWN);}
  public boolean canPlayLeft() {return canPlay(Dir.LEFT);}
  public boolean canPlayRight() {return canPlay(Dir.RIGHT);}
  public boolean isOver() {
    return state==LOST || state==WON || state==GIVE_UP;
  }
  public boolean isCardUp(){return state==CARD_UP;}
  public boolean isCardPlaced(){return state==CARD_PLACED;}
  public boolean isWaiting(){return state==WAITING;}
  public boolean isWaitingStriked(){return state==WAITING_STRIKED;}
  public boolean isGiveUp(){return state==GIVE_UP;}
  public boolean isWon(){return state==WON;}
  public boolean isLost(){return state==LOST || state==GIVE_UP;}
  public int getStrikes() {return strikes;}
  public Card getPlacedCard() {
    requireState(CARD_PLACED);
    return board.getCurrentCard();
  }
  public int getMoved() {
    return moved;
  }
  public int getStrikeLimit() {
    return config.STRIKE_LIMIT;
  }

  //////////////
  // PRIVATE: //
  //////////////

  private void play(byte direction) {
    requireState(CARD_UP);
    moved++;
    board.play(upCard, direction);
    state=board.onFinish()
      ?(keys==config.KEY_COUNT ?WON :LOST)
      :CARD_PLACED;
    upCard=null;
  }
  private boolean canPlay(byte direction) {
    if (onFirstCard) return false;
    requireState(CARD_UP);
    return board.canPlay(direction);
  }
  private void requireState(int shouldBe) {
    if (state!=shouldBe)
      throw new IllegalStateException("State should be: "+shouldBe+"; is: "+state);
  }

}
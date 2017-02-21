package main;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents game state. Tries as much as possible to hide
 * board state from game clients, although the board is still
 * exposed for the sake of rendering.
 */
public class Game {
  private static final int STRIKE_LIMIT=3;
  private static final int KEY_COUNT=2;
  private static final int
    WAITING=0,
    WAITING_STRIKED=1,
    CARD_UP=2,
    CARD_PLACED=3,
    LOST=4,
    WON=5,
    GIVE_UP=6;

  private final Board board=new Board();
  private final Deck deck=new Deck(3, 21, 21, 21, 21);
  private final int bet;
  private Card upCard=null;
  private int state=WAITING;
  private boolean onFirstCard=true;
  private int strikes=0;
  private int keys=0;
  public Game(int bet) {
    this.bet=bet;
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
      state=strikes==STRIKE_LIMIT ?LOST :WAITING_STRIKED;
    } else {
      state=CARD_UP;
    }
  }

  public boolean canPlayUp() {return canPlay(board::canPlayUp);}
  public boolean canPlayDown() {return canPlay(board::canPlayDown);}
  public boolean canPlayLeft() {return canPlay(board::canPlayLeft);}
  public boolean canPlayRight() {return canPlay(board::canPlayRight);}

  public void playUp() {play(board::playUp);}
  public void playDown() {play(board::playDown);}
  public void playLeft() {play(board::playLeft);}
  public void playRight() {play(board::playRight);}

  public void rotateCard() {
    requireState(CARD_PLACED);
    board.rotateCard();
  }
  public void finishPlayCard() {
    requireState(CARD_PLACED);
    if (board.onKey())
      keys++;
    else if (board.onBonus())
      strikes=strikes==0 ?0 :strikes-1;

    if (board.onFinish())
      state=keys==0 ?WON :LOST;
    else
    if (!(board.canPlayUp() || board.canPlayDown() || board.canPlayLeft() || board.canPlayRight()))
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
    onFirstCard=false;
    state=CARD_PLACED;
    upCard=null;
  }

  private void play(Consumer<Card> f) {
    requireState(CARD_UP);
    f.accept(upCard);
    state=CARD_PLACED;
    upCard=null;
  }
  private boolean canPlay(Supplier<Boolean> f) {
    if (onFirstCard) return false;
    requireState(CARD_UP);
    return f.get();
  }


  public boolean atVeryBeginning() {return onFirstCard;}
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

  private void requireState(int shouldBe) {
    if (state!=shouldBe)
      throw new IllegalStateException("State should be: "+shouldBe+"; is: "+state);
  }

}
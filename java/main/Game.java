package main;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// Bug: Rotate hung on left-right bar
// Bug: Rotate exploded on prev = 1 and current = 1
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

  public boolean canPutUp() {return canPut(board::canPutUp);}
  public boolean canPutDown() {return canPut(board::canPutDown);}
  public boolean canPutLeft() {return canPut(board::canPutLeft);}
  public boolean canPutRight() {return canPut(board::canPutRight);}

  public void putUp() {put(board::putUp);}
  public void putDown() {put(board::putDown);}
  public void putLeft() {put(board::putLeft);}
  public void putRight() {put(board::putRight);}

  public void rotateCard() {
    requireState(CARD_PLACED);
    board.rotateCard();
  }
  public void finishPut() {
    requireState(CARD_PLACED);
    if (board.onKey())
      keys++;
    else if (board.onBonus())
      strikes=strikes==0 ?0 :strikes-1;

    if (board.onFinish())
      state=keys==0 ?WON :LOST;
    else
    if (!(board.canPutUp() || board.canPutDown() || board.canPutLeft() || board.canPutRight()))
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

  public void putFirstCard() {
    requireState(CARD_UP);
    if (!onFirstCard) throw new IllegalStateException("Not on very first");
    board.setCard(0, upCard);
    onFirstCard=false;
    state=CARD_PLACED;
    upCard=null;
  }

  private void put(Consumer<Card> f) {
    requireState(CARD_UP);
    f.accept(upCard);
    state=CARD_PLACED;
    upCard=null;
  }
  private boolean canPut(Supplier<Boolean> f) {
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

  private void requireState(int shouldBe) {
    if (state!=shouldBe)
      throw new IllegalStateException("State should be: "+shouldBe+"; is: "+state);
  }

}
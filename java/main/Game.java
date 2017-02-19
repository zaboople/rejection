package main;
import java.util.function.Function;

public class Game {
  private static final int STRIKE_LIMIT=3;
  private static final int KEY_COUNT=2;
  private static final int WAITING=0, CARD_UP=1, CARD_PLACED=2, LOST=3, WON=4, GIVE_UP=5;

  private final Board board=new Board();
  private final Deck deck=new Deck(3, 24, 24, 24, 24);
  private final int bet;
  private Card upCard=null;
  private int state=WAITING;
  private int strikes=0;
  private int keys=0;
  public Game(int bet) {
    this.bet=bet;
  }
  public Game nextCard() {
    if (state!=WAITING) throw new IllegalStateException("Not in wait state; in state "+state);
    upCard=deck.next();
    if (upCard.isStrike()) {
      strikes++;
      state=strikes==STRIKE_LIMIT ?LOST :WAITING;
    } else {
      state=CARD_UP;
    }
    return this;
  }

  public boolean canPutUp() {return board.canPutUp();}
  public boolean canPutDown() {return board.canPutDown();}
  public boolean canPutLeft() {return board.canPutLeft();}
  public boolean canPutRight() {return board.canPutRight();}

  public boolean putUp() {return put(board::putUp);}
  public boolean putDown() {return put(board::putDown);}
  public boolean putLeft() {return put(board::putLeft);}
  public boolean putRight() {return put(board::putRight);}

  public void finishPut() {
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
  public Game giveUp() {
    state=GIVE_UP;
    return this;
  }

  private boolean put(Function<Card, Boolean> f) {
    if (state!=CARD_UP) throw new IllegalStateException("State should be CARD_UP, was "+state);
    boolean res=f.apply(upCard);
    if (res) state=CARD_PLACED;
    return res;
  }

  public boolean isGameOver() {
    return state==LOST || state==WON || state==GIVE_UP;
  }
  public boolean isWaiting(){return state==WAITING;}
  public boolean isGiveUp(){return state==GIVE_UP;}
  public boolean isWon(){return state==WON;}
  public boolean isLost(){return state==LOST || state==GIVE_UP;}

}
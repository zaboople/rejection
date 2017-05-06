package main;

/**
 * Exposes parts of the internal state of the Game mainly to allow read-only consumption
 * by UI rendering while limiting write access. Acts like it's immutable, but it actually isn't (we
 * can change that easily enough if we ever want to).
 */
public class GameState {

  // Package private constants exposed for Game class:
  static final int
    WAITING=0,
    WAITING_STRIKED=1,
    CARD_UP=2,
    CARD_PLACED=3,
    LOST=4,
    WON=5,
    GIVE_UP=6;

  // Main state:
  private int state=WAITING;

  // Rest of state; arguably firstCardUp should be part of our "main" state variable, but whatever:
  private boolean firstCardUp=true;
  private final int strikeLimit, keys;
  private int strikes=0;
  private int keysCrossed=0;

  public @Override String toString() {
    return state+" "+firstCardUp+" "+strikes+"/"+strikeLimit+" "+keysCrossed+"/"+keys;
  }

  // PACKAGE-PRIVATE METHODS FOR GAME CLASS ONLY:

  GameState(int strikeLimit, int keys) {
    this.strikeLimit=strikeLimit;
    this.keys=keys;
  }
  GameState set(int state) {
    this.state=state;
    return this;
  }
  GameState setFirstCardPlayed() {
    firstCardUp=false;
    return this;
  }
  GameState addStrike() {
    strikes++;
    return this;
  }
  GameState removeStrike() {
    if (strikes>0) strikes--;
    return this;
  }
  GameState addKeysCrossed() {
    keysCrossed++;
    return this;
  }
  void require(int shouldBe) {
    if (state!=shouldBe)
      throw new IllegalStateException("State should be: "+shouldBe+"; is: "+state);
  }

  // PUBLIC METHODS

  /** Means we are waiting to play first card onto the board. */
  public boolean firstCardUp() {return state==CARD_UP && firstCardUp;}
  public int getStrikeCount() {return strikes;}
  public int getStrikeLimit() {return strikeLimit;}
  public int getKeysCrossed() {return keysCrossed;}
  public int getKeys() {return keys;}
  public boolean isCardUp(){return state==CARD_UP;}
  public boolean isCardPlaced(){return state==CARD_PLACED;}
  public boolean isWaiting(){return state==WAITING;}
  public boolean isWaitingStriked(){return state==WAITING_STRIKED;}
  public boolean isOver()  {return state==WON || state==LOST || state==GIVE_UP;}
  public boolean isWon()   {return state==WON;}
  public boolean isLost()  {return state==LOST || state==GIVE_UP;}
  public boolean isGiveUp(){return state==GIVE_UP;}
}

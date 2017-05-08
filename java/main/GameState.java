package main;

/**
 * Exposes parts of the internal state of the Game mainly to allow read-only consumption
 * by UI rendering while limiting write access. Acts like it's immutable, but it actually isn't (we
 * can change that easily enough if we ever want to). There are some things that technically *should*
 * be in here but... aren't, mainly the Gamble class. This is close enough for jazz, however, and the
 * loose ends are not hard to tie up.
 */
public class GameState {

  // Package private constants exposed for Game class:
  static final int
    GAME_START=1,
    WAITING=2,
    WAITING_STRIKED=4,
    CARD_UP=8,
    CARD_PLACED=16,
    LOST=32,
    WON=64,
    GIVE_UP=128;

  // Main state:
  private int state=GAME_START;

  // Rest of state:
  private final int strikeLimit, keys;
  private int strikes=0;
  private int keysCrossed=0;

  public @Override String toString() {
    return state+" "+strikes+"/"+strikeLimit+" "+keysCrossed+"/"+keys;
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
  void require(int shouldBeOneOf) {
    if ((state & shouldBeOneOf)==0)
      throw new IllegalStateException("State should be one of: "+shouldBeOneOf+"; is: "+state);
  }

  // PUBLIC METHODS

  /** Means we are waiting to play first card onto the board. */
  public int getStrikeCount() {return strikes;}
  public int getStrikeLimit() {return strikeLimit;}
  public int getKeysCrossed() {return keysCrossed;}
  public int getKeys() {return keys;}
  public boolean isGameStart(){return state==GAME_START;}
  public boolean isCardUp(){return state==CARD_UP;}
  public boolean isCardPlaced(){return state==CARD_PLACED;}
  public boolean isWaiting(){return state==WAITING;}
  public boolean isWaitingStriked(){return state==WAITING_STRIKED;}
  public boolean isOver()  {return state==WON || state==LOST || state==GIVE_UP;}
  public boolean isWon()   {return state==WON;}
  public boolean isLost()  {return state==LOST || state==GIVE_UP;}
  public boolean isGiveUp(){return state==GIVE_UP;}
}

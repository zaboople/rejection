package main;

/**
 * Exposes parts of the internal state of the Game mainly to allow read-only consumption
 * by UI rendering while limiting write access. Acts like it's immutable, but it actually isn't (we
 * can change that easily enough if we ever want to).
 */
public class GameState {
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

  // PACKAGE-PRIVATE METHODS FOR GAME CLASS ONLY:
  // The modification methods currently act like we're immutable; we aren't, of
  // course, because we don't really need to be, but it's easy to change now if we want to.

  GameState(int strikeLimit, int keys) {
    this.strikeLimit=strikeLimit;
    this.keys=keys;
  }
  GameState setState(int state) {
    this.state=state;
    return this;
  }
  GameState setFirstCardPlayed() {
    firstCardUp=false;
    return this;
  }

  void require(int shouldBe) {
    if (state!=shouldBe)
      throw new IllegalStateException("State should be: "+shouldBe+"; is: "+state);
  }

  // PUBLIC METHODS

  /** Means we are waiting to play first card onto the board. */
  public boolean firstCardUp() {return firstCardUp;}
  public int getStrikes() {return strikes;}
  public int getKeysCrossed() {return keysCrossed;}
  public int getStrikeLimit() {return strikeLimit;}
  public int getKeys() {return keys;}
}

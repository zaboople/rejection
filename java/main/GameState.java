/**
 * Exposes parts of the internal state of the Game for consumption
 * by UI rendering.
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

}

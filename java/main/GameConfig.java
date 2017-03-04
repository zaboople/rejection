package main;

/**
 * Configures the game according to a variety of tuneable parameters,
 * for testing/verification. Defaults should be reasonable.
 */
public class GameConfig {
  public int
    BOARD_WIDTH=8,
    BOARD_HEIGHT=8,
    STRIKE_CARDS=4,
    STRIKE_LIMIT=3,
    KEYS=4,
    BONUSES=6,
    CARD_CORNERS=24,
    CARD_BARS=8,
    CARD_TEES=24,
    CARD_CROSSES=8;

  public void ensureEnoughCards() {
    int leftOver=CARD_CORNERS + CARD_BARS + CARD_TEES + CARD_CROSSES - (BOARD_WIDTH * BOARD_HEIGHT);
    while (leftOver < 0) {
      if (leftOver < 0) {leftOver++;CARD_CORNERS++;}
      if (leftOver < 0) {leftOver++;CARD_BARS++;}
      if (leftOver < 0) {leftOver++;CARD_TEES++;}
      if (leftOver < 0) {leftOver++;CARD_CROSSES++;}
    }
  }
}


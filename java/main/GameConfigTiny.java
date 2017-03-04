package main;

/**
 * Tiny board
 */
public class GameConfigTiny extends GameConfig {
  public GameConfigTiny(){
    BOARD_WIDTH=4;
    BOARD_HEIGHT=4;
    STRIKE_CARDS=4;
    STRIKE_LIMIT=3;
    KEYS=2;
    BONUSES=3;
    CARD_CORNERS=10;
    CARD_BARS=4;
    CARD_TEES=4;
    CARD_CROSSES=7;
  }
}


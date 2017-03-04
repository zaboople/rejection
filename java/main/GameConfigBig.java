package main;

/**
 * Big board
 */
public class GameConfigBig extends GameConfig {
  public GameConfigBig(){
    BOARD_WIDTH=30;
    BOARD_HEIGHT=12;
    int cellCount=BOARD_WIDTH * BOARD_HEIGHT;
    STRIKE_CARDS=cellCount / 8;
    STRIKE_LIMIT=STRIKE_CARDS / 3;
    KEYS=cellCount / 15;
    BONUSES=cellCount / 12;
    CARD_CORNERS=cellCount / 3;
    CARD_BARS=cellCount / 6;
    CARD_TEES=cellCount / 3;
    CARD_CROSSES=cellCount / 5;
  }
}


package main;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 */
public interface BoardView {

  public int getWidth();
  public int getHeight();
  public Cell getCell(int row, int col);
  public Card getCard(int row, int col);
  public boolean isStart(int row, int col);
  public boolean isFinish(int row, int col);
}
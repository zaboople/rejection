package main;

/**
 * Provides a minimalist, read-only view of the board for rendering purposes.
 */
public interface BoardView {
  public int getWidth();
  public int getHeight();
  public Cell getCell(int row, int col);
  public Card getCard(int row, int col);
  public boolean isStart(int row, int col);
  public boolean isFinish(int row, int col);
  public boolean onFinish();
}
package main.swang;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.font.LineMetrics;
import javax.swing.JPanel;
import main.Card;
import main.Board;
import main.Cell;

public class NewCardPanel extends JPanel {

  private static Dimension[] fontOffsets={null, null, null, null};
  private static short fontIndexKey=0, fontIndexBonus=1, fontIndexStart=2, fontIndexFinish=3;
  private static final int dashCount=4;
  private static final int dashFactor=1;

  private Board board;
  private Font font;
  private int rows=2, cols=2;
  private int currHeight=-1, currWidth=-1;
  private int
    dashWide, wDashLen, vDashLen,
    border, cardWide, cardHigh,
    actualWide, actualHigh, gapLen;
  private int[]
    wDashFixups=new int[dashCount],
    vDashFixups=new int[dashCount];

  public void setFont(Font font) {
    this.font=font;
  }
  public void setBoard(Board b) {
    this.board=b;
    this.rows=b.getHeight();
    this.cols=b.getWidth();
    this.repaint();
  }


  @Override public void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    if (board==null) return;

    final Dimension dim=getSize();
    if (dim.width!=currWidth || dim.height!=currHeight)
      recomputeLayoutOnResize(dim);
    if (!graphics.getFont().equals(font))
      recomputeFontOnResize(graphics);

    ((Graphics2D)graphics).setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
    );
    graphics.setColor(Color.BLACK);
    graphics.fillRect(0, 0, dim.width, dim.height);


    /*** DRAW BORDER DECORATION: ***/
    graphics.setColor(Color.GRAY);

    // Column lines:
    for (int x=border; x<actualWide; x+=(dashWide+cardWide)){
      int yStart=border+dashWide;
      for (int __=0; __<rows; __++) {
        int yOff=yStart+gapLen;
        for (int d=0; d<dashCount; d++){
          int realDashLen=vDashLen - vDashFixups[d];
          graphics.fillRect(x, yOff, dashWide, realDashLen);
          yOff+=realDashLen+gapLen;
        }
        yStart+=dashWide+cardHigh;
      }
    }

    // Row lines:
    for (int y=border; y<actualHigh; y+=(dashWide+cardHigh)){
      int pos=border+dashWide;
      for (int __=0; __<cols; __++) {
        int xOff=pos+gapLen;
        for (int d=0; d<dashCount; d++){
          int realDashLen=wDashLen - wDashFixups[d];
          graphics.fillRect(xOff, y, realDashLen, dashWide);
          xOff+=realDashLen+gapLen;
        }
        pos+=dashWide+cardWide;
      }
    }

    // DRAW CARDS & BACKGROUND:
    FontMetrics metrics=graphics.getFontMetrics();
    Rectangle2D
      ktangle=metrics.getStringBounds("K", graphics),
      btangle=metrics.getStringBounds("B", graphics);
    LineMetrics lmk=metrics.getLineMetrics("K", graphics);
    Rectangle2D realTextSize=font.createGlyphVector(metrics.getFontRenderContext(), "K").getVisualBounds();
    int
      kxOff=(int)Math.round(
        ( ((double)cardWide)-realTextSize.getWidth() )
        / 2.0f
      ),
      kyOff=(int)Math.round(
        (cardHigh+realTextSize.getHeight()) / 2.0
      );


    graphics.setColor(Color.GREEN);
    int top=border+dashWide;
    for (int r=0; r<rows; r++){
      int left=border+dashWide;
      for (int c=0; c<cols; c++) {
        Cell cell=board.getCell(r, c);
        if (cell.isKey())
          drawCenter(graphics, Color.GREEN, left, top, fontOffsets[fontIndexKey], "K");
        else
        if (cell.isBonus())
          drawCenter(graphics, Color.BLUE, left, top, fontOffsets[fontIndexBonus], "B");
        else
        if (board.isStart(r, c))
          drawCenter(graphics, Color.MAGENTA, left, top, fontOffsets[fontIndexStart], "S");
        else
        if (board.isFinish(r, c))
          drawCenter(graphics, Color.MAGENTA, left, top, fontOffsets[fontIndexFinish], "F");
        left+=cardWide+dashWide;
      }
      top+=cardHigh+dashWide;
    }
  }

  private void drawCenter(Graphics graphics, Color color, int left, int top, Dimension offsets, String achar) {
    graphics.setColor(color);
    graphics.drawString(achar, left+offsets.width, top +offsets.height);
  }


  //////////////////////////
  //                      //
  // RECOMPUTION + CACHE: //
  //                      //
  //////////////////////////

  /**
   * Recomputes all our measurements for layout. These are "cached" in
   * instance variables, thus taking some load off our paintComponent() magick
   * when game state changes without window size changing.
   */
  private void recomputeLayoutOnResize(Dimension dim) {
    currHeight=dim.height;
    currWidth=dim.width;

    // Decoration space and leftovers:
    dashWide=((dim.width+dim.height) / 256) * dashFactor;
    if (dashWide==0) dashWide=1;
    border=dashWide;
    int highExtra=(border*2)+((rows+1)*dashWide);
    int wideExtra=(border*2)+((cols+1)*dashWide);

    // Space we can actually use for 3 x 4 card size:
    int playHigh=dim.height-highExtra;
    cardHigh=playHigh / rows;
    cardWide=(cardHigh*3) / 4;
    int playWide=cols * cardWide;
    if (playWide + wideExtra > dim.width) {
      playWide=dim.width - wideExtra;
      cardWide=playWide / cols;
      cardHigh=(cardWide * 4) / 3;
      playHigh=cardHigh * rows;
    }
    actualWide=wideExtra+playWide;
    actualHigh=highExtra+playHigh;

    // Dashes:
    gapLen=Math.round(cardHigh / 24f);
    if (gapLen<2) gapLen=2;
    float fdashCount=dashCount;
    wDashLen=(int)Math.round(
      (cardWide - (gapLen * (dashCount+1))) / fdashCount
    );
    vDashLen=(int)Math.round(
      (cardHigh - (gapLen * (dashCount+1))) / fdashCount
    );

    // Dash-drawing rounding errors: The rounding fixes go in an array of size dashCount,
    // with -1, 1 or 0 as values to add to the corresponding dash.
    //
    // BTW, as the math goes, we'll never actually be off by more than 1/2 of dashCount,
    // but we can afford to create the full array for simplicity's sake. The last
    // half (or so) will just get filled with zeroes.
    int
      wDeficit=cardWide - (gapLen + ((wDashLen + gapLen) * dashCount)),
      vDeficit=cardHigh - (gapLen + ((vDashLen + gapLen) * dashCount));
    int
      ww=wDeficit > 0 ?-1 :1,
      vv=vDeficit > 0 ?-1 :1;
    for (int i=0; i<dashCount; i++){
      if (wDeficit==0) ww=0;
      wDashFixups[i]=ww;
      wDeficit+=ww;

      if (vDeficit==0) vv=0;
      vDashFixups[i]=vv;
      vDeficit+=vv;
    }
  }

  /**
   * Another variation, just setting arrangements for the center
   * text where it exists (K, B, S, F)
   */
  private void recomputeFontOnResize(Graphics graphics) {
    graphics.setFont(font);
    FontMetrics metrics=graphics.getFontMetrics();
    fontOffsets[fontIndexKey]=recomputeFontOnResize(metrics, "K");
    fontOffsets[fontIndexBonus]=recomputeFontOnResize(metrics, "B");
    fontOffsets[fontIndexStart]=recomputeFontOnResize(metrics, "S");
    fontOffsets[fontIndexFinish]=recomputeFontOnResize(metrics, "F");
  }
  private Dimension recomputeFontOnResize(FontMetrics metrics, String center) {
    Rectangle2D realTextSize=font.createGlyphVector(metrics.getFontRenderContext(), center).getVisualBounds();
    int
      kxOff=(int)Math.round(
        ( ((double)cardWide)-realTextSize.getWidth() )
        / 2.0f
      ),
      kyOff=(int)Math.round(
        (cardHigh+realTextSize.getHeight()) / 2.0
      );
    return new Dimension(kxOff, kyOff);
  }

}
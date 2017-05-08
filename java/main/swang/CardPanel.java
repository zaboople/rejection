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
import main.BoardView;
import main.Cell;

public class CardPanel extends JPanel {

  private static final int dashCount=2;
  private static final int dashFactor=1;

  /////////////////////////
  //                     //
  // INSTANCE VARIABLES: //
  //                     //
  /////////////////////////

  private BoardView board;
  private Font font;
  private int rows=2, cols=2;
  private int currHeight=-1, currWidth=-1;
  private int
    dashWide, wDashLen, vDashLen,
    border, cardWide, cardHigh,
    actualWide, actualHigh, gapLen,
    vPathLeftOff, vPathHigh, vPathDownTopOff,
    hPathTopOff, hPathWide, hPathRightOff;

  private final short fontIndexKey=0, fontIndexBonus=1, fontIndexStart=2, fontIndexFinish=3;
  private final Dimension[] fontOffsets={null, null, null, null};

  private final int[]
    wDashFixups=new int[dashCount],
    vDashFixups=new int[dashCount];

  /////////////////////
  //                 //
  // INITIALIZATION: //
  //                 //
  /////////////////////

  public void setFont(Font font) {
    this.font=font;
  }

  public void setBoard(BoardView b) {
    this.board=b;
    if (b!=null) {
      this.rows=b.getHeight();
      this.cols=b.getWidth();
    }
    this.repaint();
  }

  public int getActualWidth() {
    return actualWide;
  }

  public int getActualHeight() {
    return actualHigh;
  }

  ////////////////////////
  //                    //
  // EVENTS (ONLY ONE): //
  //                    //
  ////////////////////////

  @Override public void paintComponent(Graphics graphics) {
    // Rudimentary initialization:
    super.paintComponent(graphics);
    final Dimension dim=getSize();
    ((Graphics2D)graphics).setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
    );
    graphics.setColor(Color.BLACK);
    graphics.fillRect(0, 0, dim.width, dim.height);
    if (board==null || font==null) return;

    // Recompute stuff:
    boolean layoutChanged=dim.width!=currWidth || dim.height!=currHeight,
            fontChanged=!graphics.getFont().equals(font);
    if (layoutChanged)
      recomputeLayoutOnResize(dim);
    if (fontChanged)
      recomputeTextOffset(graphics);
    if (fontChanged || layoutChanged)
      recomputePathOffsets();

    // Draw stuff:
    drawBorders(graphics);
    drawCardsAndSymbols(graphics);
  }

  //////////////
  //          //
  // DRAWING: //
  //          //
  //////////////

  /** Draws the playing grid. */
  private void drawBorders(Graphics graphics) {
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
  }

  /** Draws the cards played so far and background symbols for keys/bonuses/start/finish. */
  private void drawCardsAndSymbols(Graphics graphics) {
    int top=border+dashWide;
    for (int r=0; r<rows; r++){
      int left=border+dashWide;
      for (int c=0; c<cols; c++) {
        Cell cell=board.getCell(r, c);
        if (cell.isKey())
          drawCenterSymbol(graphics, Color.GREEN, left, top, fontOffsets[fontIndexKey], "K");
        else
        if (cell.isBonus())
          drawCenterSymbol(graphics, Color.BLUE, left, top, fontOffsets[fontIndexBonus], "B");
        else
        if (board.isStart(r, c))
          drawCenterSymbol(graphics, Color.MAGENTA, left, top, fontOffsets[fontIndexStart], "S");
        else
        if (board.isFinish(r, c))
          drawCenterSymbol(graphics, Color.MAGENTA, left, top, fontOffsets[fontIndexFinish], "F");
        Card card=board.getCard(r, c);
        if (card!=null) {
          graphics.setColor(Color.WHITE);
          if (card.hasPathUp())
            graphics.fillRect(left+vPathLeftOff, top+dashWide, dashWide, vPathHigh);
          if (card.hasPathDown())
            graphics.fillRect(left+vPathLeftOff, top+vPathDownTopOff, dashWide, vPathHigh);
          if (card.hasPathLeft())
            graphics.fillRect(left+dashWide, top+hPathTopOff, hPathWide, dashWide);
          if (card.hasPathRight())
            graphics.fillRect(left+hPathRightOff, top+hPathTopOff, hPathWide, dashWide);
        }
        left+=cardWide+dashWide;
      }
      top+=cardHigh+dashWide;
    }
  }

  private void drawCenterSymbol(
      Graphics graphics, Color color, int left, int top, Dimension offsets, String achar
    ) {
    graphics.setColor(color);
    graphics.drawString(achar, left+offsets.width, top+offsets.height);
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
    dashWide=((dim.width+dim.height) / 384) * dashFactor;
    if (dashWide==0) dashWide=1;
    else if (dashWide>3) dashWide=3;
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
    gapLen=Math.round(cardHigh / 20f);
    if (gapLen<2) gapLen=2;
    else
    if (gapLen>4) gapLen=4;
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
   * Another variation: When font changes, configure the center
   * text background where it exists (K, B, S, F).
   */
  private void recomputeTextOffset(Graphics graphics) {
    graphics.setFont(font);
    FontMetrics metrics=graphics.getFontMetrics();
    fontOffsets[fontIndexKey]=recomputeTextOffset(metrics, "K");
    fontOffsets[fontIndexBonus]=recomputeTextOffset(metrics, "B");
    fontOffsets[fontIndexStart]=recomputeTextOffset(metrics, "S");
    fontOffsets[fontIndexFinish]=recomputeTextOffset(metrics, "F");
  }
  private Dimension recomputeTextOffset(FontMetrics metrics, String center) {
    //Subtract? Add? Note: For offLeft, we get the proper left; but for offTop, we have this problem
    //of rendering from the "baseline", which is between the dangling down-line of a "p" and the
    //circley bit abovewards.
    Rectangle2D realTextSize=font.createGlyphVector(metrics.getFontRenderContext(), center).getVisualBounds();
    float fcardWide=cardWide, fcardHigh=cardHigh;
    int
      offLeft=(int)Math.round(
        (fcardWide-realTextSize.getWidth())
        / 2.0f
      ),
      offTop=(int)Math.round(
        (fcardHigh+realTextSize.getHeight())
        / 2.0f
      );
    return new Dimension(offLeft, offTop);
  }

  /**
   * Finally, if font _or_ playing area size changes, we need to recompute the
   * card path drawing.
   */
  private void recomputePathOffsets() {
    int avgFontTopOff=fontOffsets[fontIndexKey].height,
        avgFontLeftOff=fontOffsets[fontIndexKey].width; //DERP

    vPathLeftOff=Math.round((cardWide-dashWide)/2.0f);
    vPathHigh=cardHigh - (avgFontTopOff + (dashWide * 2));
    vPathDownTopOff=avgFontTopOff + dashWide;

    hPathTopOff=Math.round((cardHigh-dashWide)/2.0f);
    hPathWide=avgFontLeftOff-(dashWide*2);
    hPathRightOff=cardWide - (dashWide+hPathWide);
    //System.out.println("FUCK "+cardWide+" "+avgFontWide+" "+dashWide);
  }

}

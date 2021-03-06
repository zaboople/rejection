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
import main.BoardView;
import main.Card;
import main.Cell;
import main.Dir;

public class CardPanel extends JPanel {

  private static final int dashCount=2;
  private static final int dashFactor=1;

  /////////////////////////
  //                     //
  // INSTANCE VARIABLES: //
  //                     //
  /////////////////////////

  private final Color
    grayColor=new Color(50,50,50),
    keyColor=Color.GREEN,
    bonusColor=Color.BLUE,
    startColor=Color.MAGENTA,
    finishColor=Color.MAGENTA;
  private final Runnable resizeCallback;

  private BoardView board;
  private Font font;
  private int rows=2, cols=2;
  private int currHeight=-1, currWidth=-1;
  private int

    // Really "dash-thick", plus precalculated double/triple/quadruple:
    dashWide, dashWide2, dashWide3, dashWide4,

    // Length of dash vertically/horizontally, which are different, yes:
    wDashLen, vDashLen,

    // Meh:
    border, cardWide, cardHigh,
    actualWide, actualHigh, gapLen,

    // Path variables:
    vPathLeftOff, vPathHighWithSymbol, vPathHighWithoutSymbol, vPathDownTopOffWithSymbol,
    hPathTopOff, hPathWideWithSymbol, hPathWideWithoutSymbol, hPathRightOffWithSymbol
    ;

  private final short fontIndexKey=0, fontIndexBonus=1, fontIndexStart=2, fontIndexFinish=3;
  private final Dimension[] fontOffsets={null, null, null, null};

  private final int[]
    wDashFixups=new int[dashCount],
    vDashFixups=new int[dashCount];

  private boolean fontChanged=false;

  /////////////////////
  //                 //
  // INITIALIZATION: //
  //                 //
  /////////////////////


  /**
   * @param resizeCallback This is so Screen can find out the results
   *     of our layout rearrangements. Refer to paintComponent() for
   *     more details.
   */
  public CardPanel(Runnable resizeCallback) {
    super();
    this.resizeCallback=resizeCallback;
  }

  public void setFont(Font font) {
    this.font=font;
    fontChanged=true;
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
    boolean layoutChanged=dim.width!=currWidth || dim.height!=currHeight;
    if (layoutChanged)
      recomputeLayoutOnResize(dim);
    if (fontChanged || layoutChanged) {
      recomputeTextOffset(graphics);
      recomputePathOffsets();
      fontChanged=false;
      // Warning: This is really a callback to Screen, which may invoke repaint() back at
      // us, which eventually/asynchronously gets turned into *another* paintComponent()
      // call by Swing internals. However, this cycle should stabilize quickly since we
      // maintain steady size while they adjust fonts to accommodate:
      resizeCallback.run();
    }

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
    graphics.setColor(grayColor);
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
    graphics.setFont(font);
    int top=border+dashWide;
    for (int r=0; r<rows; r++){
      int left=border+dashWide;
      for (int c=0; c<cols; c++) {
        final int cellIndex=board.getCellIndex(r, c);
        final Cell cell=board.getCell(cellIndex);
        final int cellIndexFrom=cell.getPrevious();


        // Draw symbol:
        boolean hasSymbol=true;
        if (cell.isKey())
          drawCenterSymbol(graphics, keyColor, left, top, fontOffsets[fontIndexKey], "K");
        else
        if (cell.isBonus())
          drawCenterSymbol(graphics, bonusColor, left, top, fontOffsets[fontIndexBonus], "B");
        else
        if (board.isStart(r, c))
          drawCenterSymbol(graphics, startColor, left, top, fontOffsets[fontIndexStart], "S");
        else
        if (board.isFinish(r, c))
          drawCenterSymbol(graphics, finishColor, left, top, fontOffsets[fontIndexFinish], "F");
        else
          hasSymbol=false;


        // Draw paths & connections:
        Card card=cell.getCard();
        if (card!=null) {

          graphics.setColor(Color.WHITE);

          // Draw basic paths:
          int
            vPathHigh=hasSymbol ?vPathHighWithSymbol :vPathHighWithoutSymbol,
            hPathWide=hasSymbol ?hPathWideWithSymbol :hPathWideWithoutSymbol;
          if (card.hasPathUp())
            graphics.fillRect(left+vPathLeftOff, top+dashWide, dashWide, vPathHigh);
          if (card.hasPathDown()) {
            int pathTop=hasSymbol ?top+vPathDownTopOffWithSymbol :top+vPathHighWithoutSymbol;
            graphics.fillRect(left+vPathLeftOff, pathTop, dashWide, vPathHigh);
          }
          if (card.hasPathLeft())
            graphics.fillRect(left+dashWide, top+hPathTopOff, hPathWide, dashWide);
          if (card.hasPathRight()) {
            int pathLeft=hasSymbol ?left+hPathRightOffWithSymbol :left+hPathWideWithoutSymbol;
            graphics.fillRect(pathLeft, top+hPathTopOff, hPathWide, dashWide);
          }

          // Connecting points:
          byte dirFrom=0;
          if (cellIndexFrom>0) {
            if (cellIndexFrom==cellIndex+1) dirFrom=Dir.RIGHT;
            else
            if (cellIndexFrom==cellIndex-1) dirFrom=Dir.LEFT;
            else
            if (cellIndexFrom==cellIndex-cols) dirFrom=Dir.UP;
            else
            if (cellIndexFrom==cellIndex+cols) dirFrom=Dir.DOWN;
            else
            if (dirFrom==0) throw new RuntimeException("What?");
          }
          if (dirFrom!=0) {
            if (dirFrom==Dir.LEFT)
              graphics.fillRect(left-dashWide2, top+hPathTopOff, dashWide3, dashWide);
            else
            if (dirFrom==Dir.RIGHT)
              graphics.fillRect(left+cardWide-dashWide2, top+hPathTopOff, dashWide4, dashWide);
            else
            if (dirFrom==Dir.UP)
              graphics.fillRect(left+vPathLeftOff, top-dashWide2, dashWide, dashWide3);
            else
            if (dirFrom==Dir.DOWN)
              graphics.fillRect(left+vPathLeftOff, top+cardHigh-dashWide2, dashWide, dashWide4);
            else
              throw new RuntimeException("Illegal direction: "+dirFrom);
          }
        }
        left+=cardWide+dashWide;
      }
      top+=cardHigh+dashWide;
    }
  } // drawCardsAndSymbols()

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
    dashWide2=2*dashWide;
    dashWide3=3*dashWide;
    dashWide4=4*dashWide;
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
    recomputePathOffsets();
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
    int minFontTopOff=fontOffsets[0].height,
        minFontLeftOff=fontOffsets[0].width;
    for (int i=1; i<fontOffsets.length; i++) {
      Dimension d=fontOffsets[i];
      if (d.height < minFontTopOff) minFontTopOff=d.height;
      if (d.width < minFontLeftOff) minFontLeftOff=d.width;
    }

    vPathLeftOff=Math.round((cardWide-dashWide)/2.0f);
    vPathHighWithSymbol=cardHigh - (minFontTopOff + (dashWide * 2));
    vPathDownTopOffWithSymbol=minFontTopOff + dashWide;

    hPathTopOff=Math.round((cardHigh-dashWide)/2.0f);
    hPathWideWithSymbol=minFontLeftOff-(dashWide*2);
    hPathRightOffWithSymbol=cardWide-(dashWide+hPathWideWithSymbol);

    //These are a little bizarre in their definition but they work:
    vPathHighWithoutSymbol=hPathTopOff;
    hPathWideWithoutSymbol=vPathLeftOff;
  }

}

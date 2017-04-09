package main.swang;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import main.Card;

public class NewCardPanel extends JPanel {
  private Card card;
  private final int rows, cols;
  private static final int dashCount=4;
  private static final int dashFactor=1;

  public NewCardPanel(int rows, int cols) {
    super();
    this.rows=rows;
    this.cols=cols;
  }
  public void setCard(Card c) {
    this.card=c;
    this.repaint();
  }

  @Override public void paintComponent(Graphics g) {
    super.paintComponent(g);

    final Dimension dim=getSize();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, dim.width, dim.height);


    /** CALCULATE DIMENSIONS; FIXME ONLY CALC ON RESIZE **/

    // Calculate decoration space and leftovers:
    int dashWide=((dim.width+dim.height) / 256) * dashFactor;
    if (dashWide==0) dashWide=1;
    int border=dashWide;
    int highExtra=(border*2)+((rows+1)*dashWide);
    int wideExtra=(border*2)+((cols+1)*dashWide);

    // Figure out the space we can actually use for 3 x 4 card size:
    int playHigh=dim.height-highExtra;
    int cardHigh=playHigh / rows;
    int cardWide=(cardHigh*3) / 4;
    int playWide=cols * cardWide;
    if (playWide + wideExtra > dim.width) {
      playWide=dim.width - wideExtra;
      cardWide=playWide / cols;
      cardHigh=(cardWide * 4) / 3;
      playHigh=cardHigh * rows;
    }
    final int actualWide=wideExtra+playWide,
              actualHigh=highExtra+playHigh;

    // Dashes:
    int gapLen=cardHigh / 32;
    if (gapLen<2) gapLen=2;
    int
      wDashLen=(cardWide - (gapLen * (dashCount+1))) / dashCount,
      vDashLen=(cardHigh - (gapLen * (dashCount+1))) / dashCount;


    /*** DRAW BORDER DECORATION: ***/
    g.setColor(Color.GRAY);

    // Column lines:
    for (int x=border; x<actualWide; x+=(dashWide+cardWide)){
      int pos=border+dashWide;
      for (int __=0; __<rows; __++) {
        int yOff=pos+gapLen;
        for (int ___=0; ___<dashCount; ___++){
          g.fillRect(x, yOff, dashWide, vDashLen);
          yOff+=vDashLen+gapLen;
        }
        pos+=dashWide+cardHigh;
      }
    }

    // Row lines:
    for (int y=border; y<actualHigh; y+=(dashWide+cardHigh)){
      int pos=border+dashWide;
      for (int __=0; __<cols; __++) {
        int xOff=pos+gapLen;
        for (int ___=0; ___<dashCount; ___++){
          g.fillRect(xOff, y, wDashLen, dashWide);
          xOff+=wDashLen+gapLen;
        }
        pos+=dashWide+cardWide;
      }
    }

    //System.out.println("BORDER: "+border+" DIM: "+dim.width+" "+dim.height+"  WIDE/HIGH: "+playWide+" "+playHigh+" CARD "+cardWide+" "+cardHigh);

    if (card != null) {

    }
    //g.setColor(Color.WHITE);
    //g.drawString("test", 0, 0);
  }

}
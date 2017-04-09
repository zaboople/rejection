package main.swang;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import main.Card;

public class CardPanel extends JPanel {
  private final Dimension cardSize;
  private Card card;
  private boolean hasRight, hasBottom;

  public CardPanel(Dimension cardSize, boolean hasBottom, boolean hasRight) {
    super();
    this.cardSize=cardSize;
    this.hasBottom=hasBottom;
    this.hasRight=hasRight;
    setPreferredSize(cardSize);
    //setBorder(javax.swing.BorderFactory.createDashedBorder(Color.GRAY, 4, 8, 2, false));
  }
  public void setCard(Card c) {
    this.card=c;
    this.repaint();
  }
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    final Dimension d=getSize();
    final int width=d.width;
    final int height=d.height;

    g.setColor(Color.BLACK);
    g.fillRect(0, 0, width, height);

    final int dashLen=height / 8;
    final int dashWide=4 * dashLen / 8;
    final int vDashCount=height / dashLen;
    final int wDashCount=width / dashLen;
    final int offset=dashLen / 2;

    g.setColor(Color.GRAY);
    for (int i=0; i<vDashCount; i++) {
      if (i % 2 == 0) {
        int ypos=offset + (i*dashLen);
        g.fillRect(0, ypos, dashWide, dashLen);
        if (hasRight)
          g.fillRect(width-dashWide, ypos, dashWide, dashLen);
      }
    }
    for (int i=0; i<wDashCount; i++) {
      if (i % 2 != 0) {
        int xpos=(i*dashLen) - (offset/2);
        g.fillRect(xpos, 0, dashLen, dashWide);
        if (hasBottom)
          g.fillRect(xpos, height-dashWide, dashLen, dashWide);
      }
    }

    if (card != null) {

    }
    //g.setColor(Color.WHITE);
    //g.drawString("test", 0, 0);
  }

}
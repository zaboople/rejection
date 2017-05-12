package main.swang;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import main.Card;
import main.BoardView;
import main.Gamble;
import main.GameState;
import org.tmotte.common.swang.GridBug;
import org.tmotte.common.swang.CurrentOS;
import org.tmotte.common.swang.KeyMapper;

/** Does all the swing-y stuff. Uses CardPanel to show the cards. */
public class Screen {

  /** Initializes a new instance of Screen and calls back spi.init() when finished. */
  public static void startup(ScreenPlayInterface spi, boolean fullScreen) {
    javax.swing.SwingUtilities.invokeLater(()-> {
      try {
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        Screen screen=new Screen(spi, fullScreen);
        screen.show();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  private ScreenPlayInterface watcher;
  private GameState gameState;
  private boolean fullScreen=false;
  private boolean initialized=false;

  private int blackLabelFontSize=20, cardFontSize=10;
  private Font blackLabelFont, cardFont;

  private JFrame win;
  private CardPanel cardPanel;
  private JLabel
    lblAlert,
    lblForKeys,
    lblKeys,
    lblForStrikes,
    lblStrikes,
    lblBetPrefix,
    lblBet,
    lblForMove,

    lblEnterBet1
  ;
  private JComponent[] allNothings, allTextComps, allJTFs, textPanels;
  private int lblNothingIndex=-1;
  private JTextField jtfBet, jtfMove;
  private JPanel pnlPlay, pnlBet, pnlAlert;
  private CurrentOS currentOS;

  private Screen(ScreenPlayInterface watcher, boolean fullScreen) {
    this.watcher=watcher;
    this.fullScreen=fullScreen;
  }

  private void show() {
    init();
    pnlPlay.setVisible(false);
    pnlBet.setVisible(false);
    watcher.init(this);
    win.setVisible(true);
    win.toFront();
  }

  /////////////////////////////////////////////////////
  // EXPOSED TO ScreenPlay TO RECEIVE STATE CHANGES: //
  /////////////////////////////////////////////////////

  void setBoard(BoardView board)  {
    cardPanel.setBoard(board);
  }
  void setStateBet(int available) {
    lblEnterBet1.setText(String.format("Enter bet for this round, limit $%d:", available));
    setVisiblePanel(pnlBet);
    jtfBet.requestFocusInWindow();
  }
  void setBet(Gamble gamble) {
    lblBetPrefix.setText(gamble==null ?" " :"Bet:");
    lblBet.setText(gamble==null ?" " :String.format(" $%d of %d", gamble.getBet(), gamble.getTotal()));
  }

  /** The onFinishCell param lets us know enough to determine why we lost (strikes, out of moves, missed key cells) */
  void nextState(GameState state, Gamble gamble, boolean onFinishCell) {
    this.gameState=state;

    // Key & Strike count/limit:
    {
      int
        count=state.getStrikeCount(),
        limit=state.getStrikeLimit();
      String text=String.format(" %d / %d %s", count, limit, limit-count<=1 ?"*****" :"");
      if (!text.equals(lblStrikes.getText()))
        lblStrikes.setText(text);
    }
    {
      int
        count=state.getKeysCrossed(),
        limit=state.getKeys();
      String text=String.format(" %d / %d %s", count, limit, limit-count<=1 ?"*****" :"");
      if (!text.equals(lblKeys.getText()))
        lblKeys.setText(text);
    }

    if (state.isGameStart()) {
      setBet(gamble);
      lblAlert.setText(" ");
      setVisiblePanel(pnlPlay);
      centerAlertPanel();
      lblForMove.setText(
        gamble!=null && gamble.canDoubleDown()
          ?"Enter [D]ouble down or [ ] to play first card:"
          :"Press enter [ ] to play first card:"
      );
      jtfMove.setText("");
      lblAlert.setText(" ");
      jtfMove.requestFocusInWindow();
    }
    else
    if (state.isWaitingStriked()) {
      setAlert(Color.RED, "!!!!!! STRIKE !!!!!!");
      lblForMove.setText("Strike hit. Press enter:");
      setVisiblePanel(pnlPlay);
      jtfMove.setText("");;
      jtfMove.requestFocusInWindow();
    }
    else
    if (state.isCardPlaced()) {
      if (!" ".equals(lblAlert.getText()))
        lblAlert.setText(" ");
      String lblText="[R]otate, [S]witch, [G]ive up or [ ]Accept:";
      if (!lblText.equals(lblForMove.getText()))
        lblForMove.setText(lblText);
      setVisiblePanel(pnlPlay);
      jtfMove.setText("");;
      jtfMove.requestFocusInWindow();
      cardPanel.repaint();
    }
    else
    if (state.isOver()) {
      setAlert(
        state.isWon()? Color.GREEN :Color.RED,
        state.isWon()
          ?(
              gamble==null
                ?"******* WIN *******"
                :String.format("$$$$$$$ WIN You have $%d $$$$$$$", gamble.getTotal())
          )
          :(
              getLoseMessage(state, onFinishCell)+(
                gamble==null ?"" :String.format(" You have $%d", gamble.getTotal())
              )
          )
      );
      lblForMove.setText("Play again? Enter [Q]uit or [ ] to continue:");
      jtfMove.setText("");
      setVisiblePanel(pnlPlay);
      jtfMove.requestFocusInWindow();
      cardPanel.repaint();
    }

  }

  private void setAlert(Color color, String text) {
    centerAlertPanel();
    lblAlert.setForeground(color);
    lblAlert.setText(text);
  }


  /////////////
  // CREATE: //
  /////////////

  private void init() {
    if (!initialized) {
      create();
      layout();
      listen();
      initialized=true;
    }
  }

  private void create(){
    currentOS=new CurrentOS();

    win=new JFrame();
    win.setTitle("Rejection");

    if (fullScreen) {
      Rectangle screen=win.getGraphicsConfiguration().getBounds();
      win.setBounds(screen);
      win.setUndecorated(true);
    }
    else
      win.setPreferredSize(new Dimension(15*50, 12*50));


    blackLabelFont=createLabelFont(blackLabelFontSize);
    cardFont=createCardFont(cardFontSize);

    cardPanel=new CardPanel();
    cardPanel.setFont(cardFont);

    pnlPlay=newBlackPanel();
    pnlBet=newBlackPanel();
    pnlAlert=newBlackPanel();

    lblAlert=new BlackLabel("STRIKE");
    lblAlert.setForeground(Color.RED);
    lblKeys=new BlackLabel(" 0 / 0 ******");
    lblForKeys=new BlackLabel("Keys:");
    lblForStrikes=new BlackLabel("Strikes:");
    lblStrikes=new BlackLabel(" 0 / 0 ******");
    lblBetPrefix=new BlackLabel("Bet:");
    lblBet=new BlackLabel("$0 of $0");
    lblForMove=new BlackLabel("[R]otate, [S]witch, [G]ive up or [ ]Accept:");
    jtfMove=new BlackJTF();
    jtfMove.setColumns(2);

    lblEnterBet1=new BlackLabel("Bet for this game,");
    jtfBet=new BlackJTF();
    jtfBet.setColumns(10);

    allTextComps=new JComponent[]{
      lblEnterBet1, jtfBet,
      lblAlert, lblForKeys, lblKeys, lblForStrikes, lblStrikes, lblBetPrefix, lblBet, lblForMove, jtfMove
    };
    allNothings=new JComponent[6];
    for (int i=0; i<allNothings.length; i++) allNothings[i]=new BlackLabel(" ");
    allJTFs=new JComponent[]{jtfBet, jtfMove};
    textPanels=new JComponent[]{pnlPlay, pnlBet};
  }

  /////////////
  // LAYOUT: //
  /////////////

  private void layout() {
    win.getContentPane().setBackground(Color.BLACK);
    GridBug gb=new GridBug(win);
    gb
      .gridXY(0)
      .setInsets(5, 5, 0, 5)
      .fill(gb.BOTH)
      .anchor(gb.NORTHWEST)
      .weightXY(1)
      .addY(layoutTableau())
      .fill(gb.HORIZONTAL)
      .weightXY(1,0)
      .setInsets(0, 5, 5, 10)
      .addY(layoutPlay())
      .addY(layoutPnlBet())
      ;
    if (!fullScreen)
      win.pack();
  }


  private Container layoutTableau() {
    return new GridBug(newBlackPanel())
      .insets(9)
      .fill(GridBug.BOTH)
      .weightXY(1)
      .anchor(GridBug.NORTHWEST)
      .add(cardPanel)
      .getContainer();
  }

  private Container layoutPlay() {
    return new GridBug(pnlPlay)
      .insets(0, 0, 0, 0)
      .weightXY(0, 0)
      .anchor(GridBug.WEST)
      .setX(0).gridWidth(2).weightX(1).fill(GridBug.NONE)
      .addX(
        new GridBug(pnlAlert)
          .insets(0)
          .weightX(0).add(lblAlert)
          .getContainer()
      )
      .fill(GridBug.NONE).weightX(0).gridWidth(1)
      .addY().setX(0).addX(lblForKeys).add(lblKeys)
      .addY().setX(0).addX(lblForStrikes).add(lblStrikes)
      .addY().setX(0).addX(lblBetPrefix).add(lblBet)
      .addY().setX(0).gridWidth(2).addX(
        new GridBug(newBlackPanel())
          .insets(0)
          .addX(lblForMove).weightX(1).insetLeft(3).add(jtfMove)
          .getContainer()
      )
      .getContainer();
  }


  private Container layoutPnlBet() {
    return new GridBug(pnlBet)
      .insets(0, 0, 0, 0)
      .anchor(GridBug.WEST)
      .setX(0).insetLeft(0).insetRight(0).gridWidth(2).weightX(0)
      .addY(allNothings[++lblNothingIndex])
      .addY(allNothings[++lblNothingIndex])
      .addY(allNothings[++lblNothingIndex])
      .addY(allNothings[++lblNothingIndex])
      .gridWidth(1).weightX(0).addX(lblEnterBet1) //FIXME move to bottom
        .weightX(1).insetLeft(3).fill(GridBug.HORIZONTAL)
        .insetRight(10).addY(jtfBet)
      .getContainer();
  }


  /////////////
  // LISTEN: //
  /////////////

  private void click(boolean action) {
    win.setVisible(false);
  }

  private void listen() {
    KeyAdapter allListener=new KeyAdapter(){
      @Override public void keyReleased(KeyEvent e){
        Component comp=e.getComponent();
        int keyCode=e.getKeyCode();
        if (keyCode==KeyEvent.VK_W && KeyMapper.modifierPressed(e, currentOS))
          System.exit(0);
        else
        if (comp==jtfBet && keyCode==KeyEvent.VK_ENTER)
          watcher.betEntered(jtfBet.getText());
        else
        if (comp==jtfMove && textActuallyEntered(keyCode, jtfMove))
          watcher.moveEntered(jtfMove.getText());
      }
    };
    win.addKeyListener(allListener);
    for (JComponent j: allJTFs)
      j.addKeyListener(allListener);

    win.addComponentListener(new ComponentAdapter() {
    	@Override public void componentResized(ComponentEvent e) {
        handleResizeWindow();
      }
    });
    win.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e){
        System.exit(0);
      }
    });
  }

  ////////////////////////
  // PRIVATE UTILITIES: //
  ////////////////////////

  private boolean textActuallyEntered(int keyCode, JTextField entryBox) {
    return keyCode==KeyEvent.VK_ENTER || !"".equals(entryBox.getText().trim());
  }

  /** This seems to get invoked before rendering, so we don't have to issue a repaint() */
  private void handleResizeWindow() {

    // 1. Label font:
    {
      int fontSize=win.getHeight() / 32;
      if (fontSize==0) fontSize=1;
      else
      if (fontSize>18) fontSize=18;
      Font font=createLabelFont(fontSize);
      if (!font.equals(blackLabelFont)) {
        blackLabelFont=font;
        for (JComponent comp: allTextComps)
          comp.setFont(font);
        for (JComponent comp: allNothings)
          comp.setFont(font);
        cardPanel.setFont(font);
      }
    }

    // 2. Card panel font:
    {
      int fontSize=win.getHeight() / 48;
      if (fontSize==0) fontSize=1;
      else
      if (fontSize>18) fontSize=18;
      Font font=createCardFont(fontSize);
      if (!font.equals(cardFont)){
        cardFont=font;
        cardPanel.setFont(font);
      }
    }

    // 3. And some loose-endedness:
    centerAlertPanel();
  }

  private void centerAlertPanel() {
    Dimension pnlAlertSize=pnlAlert.getSize();
    int cardPanelWide=cardPanel.getActualWidth();
    if (pnlAlertSize.height > 0 && cardPanelWide!=pnlAlertSize.width && cardPanelWide>lblForMove.getSize().width){
      pnlAlert.setPreferredSize(new Dimension(cardPanel.getActualWidth(), pnlAlertSize.height));
      pnlAlert.revalidate();
    }
  }

  private static String getLoseMessage(GameState state, boolean onFinishCell) {
    // There is some process-of-elimination reasoning here that limits our need
    // to have access to the full game state situation.
    if (!state.isLost())
      return "LOSE - INTERNAL ERROR";
    else
    if (state.isGiveUp())
      return "LOSE - You gave up.";
    else
    if (state.getStrikeCount()==state.getStrikeLimit())
      return "LOSE - Too many strikes.";
    else
    if (onFinishCell)
      return "LOSE - Missed keys.";
    else
      return "LOSE - Nowhere to go.";
  }

  private void setVisiblePanel(JPanel p) {
    for (JComponent jc: textPanels)
      if (p!=jc)
        jc.setVisible(false);
    p.setVisible(true);
  }

  private static JPanel newColorPanel(Color c) {
    JPanel jp=new JPanel();
    jp.setBackground(c);
    return jp;
  }

  private static JPanel newBlackPanel() {
    return newColorPanel(Color.BLACK);
  }

  private Font createCardFont(int size) {
    return new Font(Font.SANS_SERIF, Font.PLAIN, size);
  }

  private Font createLabelFont(int size) {
    return new Font(Font.MONOSPACED, Font.PLAIN, size);
  }

  /////////////////////
  // UTILITY CLASSES //
  /////////////////////

  private class BlackLabel extends JLabel {
    public BlackLabel(String text) {
      super(text);
      setForeground(Color.WHITE);
      setFont(blackLabelFont);
    }
  }
  private class BlackJTF extends JTextField {
    public BlackJTF() {
      super();
      setForeground(Color.WHITE);
      setFont(blackLabelFont);
      setBackground(Color.BLACK);
      setHorizontalAlignment(LEFT);
      setCaretColor(Color.GRAY);// Leave alone and get no cursor
    }
  }
}

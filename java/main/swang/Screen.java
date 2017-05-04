package main.swang;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import main.Card;
import main.Board;
import main.Gamble;
import org.tmotte.common.swang.GridBug;
import org.tmotte.common.swang.CurrentOS;
import org.tmotte.common.swang.KeyMapper;

public class Screen {

  public static void startup(ScreenPlayInterface spi) {
    javax.swing.SwingUtilities.invokeLater(()-> {
      try {
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        Screen screen=new Screen(spi);
        screen.show();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  private ScreenPlayInterface watcher;
  private boolean initialized=false;

  private int blackLabelFontSize=20;
  private Font blackLabelFont;

  private JFrame win;
  private NewCardPanel cardPanel;
  private JLabel
    lblStrikeAlert,

    lblForKeys,
    lblKeys,
    lblForStrikes,
    lblStrikes,
    lblBetPrefix,
    lblBet,
    lblForMove,

    lblWinLose,
    lblYouHave,
    lblPlayAgain,

    lblEnterBet1,
    lblEnterBet2
  ;
  private JComponent[] allNothings, allTextComps, allJTFs, textPanels;
  private int lblNothingIndex=-1;
  private JTextField jtfBet, jtfMove, jtfPlayAgain;
  private JPanel pnlPlay, pnlWinLose, pnlPlayAgain, pnlBet;
  private CurrentOS currentOS;

  Screen(ScreenPlayInterface watcher) {
    this.watcher=watcher;
  }

  public void show() {
    init();
    pnlPlay.setVisible(false);
    pnlWinLose.setVisible(false);
    pnlPlayAgain.setVisible(false);
    pnlBet.setVisible(false);
    watcher.init(this);
    win.setVisible(true);
    win.toFront();
  }

  public void setStrikes(int strikes, int limit) {
    lblStrikes.setText(String.format(" %d / %d %s", strikes, limit, limit-strikes<=1 ?"*****" :""));
  }

  public void setStateBet(int available) {
    lblEnterBet1.setText(String.format("Enter bet for this round, limit $%d:", available));
    setVisiblePanel(pnlBet);
    jtfBet.requestFocusInWindow();
  }
  public void setStatePlay(Gamble gamble, Board board) {
    lblStrikes.setText(" ");
    lblBetPrefix.setText(gamble==null ?" " :"Bet:");
    lblBet.setText(gamble==null ?" " :String.format("%d of %d", gamble.getBet(), gamble.getTotal()));
    setVisiblePanel(pnlPlay);
    cardPanel.setBoard(board);
    jtfMove.requestFocusInWindow();
  }
  public void setStateStrike() {
    lblStrikeAlert.setText("   !!! STRIKE !!!");
    lblForMove.setText("Strike hit. Press enter:");
  }
  public void setStateNextMove() {
    lblStrikeAlert.setText(" ");
    String s=jtfMove.getText();
    if (s==null || s.length()==0)
      return;
    jtfMove.select(0, s.length());
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
    win.setPreferredSize(new Dimension(15*50, 12*50)); //FIXME
    blackLabelFont=createFont(blackLabelFontSize);

    cardPanel=new NewCardPanel();
    cardPanel.setFont(blackLabelFont);

    pnlPlay=newBlackPanel();
    pnlWinLose=newBlackPanel();
    pnlPlayAgain=newBlackPanel();
    pnlBet=newBlackPanel();

    lblStrikeAlert=new BlackLabel("STRIKE");
    lblKeys=new BlackLabel(" 0 / 0 ******");
    lblForKeys=new BlackLabel("Keys:");
    lblForStrikes=new BlackLabel("Strikes:");
    lblStrikes=new BlackLabel(" 0 / 0 ******");
    lblBetPrefix=new BlackLabel("Bet:");
    lblBet=new BlackLabel("$0 of $0");
    lblForMove=new BlackLabel("[R]otate, [S]witch, [G]ive up or [ ]Accept:");
    jtfMove=new BlackJTF();
    jtfMove.setColumns(2);

    lblWinLose=new BlackLabel("$$$$$$$ WIN $$$$$$$");
    lblYouHave=new BlackLabel("You have $1000010000");
    lblPlayAgain=new BlackLabel("Play again? Enter [Q]uit or [ ] to continue:");
    jtfPlayAgain=new BlackJTF();
    jtfPlayAgain.setColumns(2);

    //FIXME how about "Your bank: $##" "Your bet: $jtf"
    lblEnterBet1=new BlackLabel("Bet for this game,");
    lblEnterBet2=new BlackLabel("limit $XXXXXX:");
    jtfBet=new BlackJTF();
    jtfBet.setColumns(10);

    allTextComps=new JComponent[]{
      lblEnterBet1, lblEnterBet2, jtfBet,
      lblStrikeAlert, lblForKeys, lblKeys, lblForStrikes, lblStrikes, lblBetPrefix, lblBet, lblForMove, jtfMove,
      lblWinLose, lblYouHave, lblPlayAgain, jtfPlayAgain
    };
    allNothings=new JComponent[6];
    for (int i=0; i<allNothings.length; i++) allNothings[i]=new BlackLabel(" ");
    allJTFs=new JComponent[]{jtfBet, jtfMove, jtfPlayAgain};
    textPanels=new JComponent[]{pnlPlay, pnlWinLose, pnlPlayAgain, pnlBet};
  }

  private Font createFont(int size) {
    return new Font(Font.MONOSPACED, Font.PLAIN, size);
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
      .addY(layoutWinLose())
      .addY(layoutPnlBet())
      ;
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
      .gridWidth(2).weightX(1).add(lblStrikeAlert).weightX(0).gridWidth(1)
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


  private Container layoutWinLose() {
    return new GridBug(pnlWinLose)
      .anchor(GridBug.WEST)
      .gridWidth(1).weightX(1)
      .addY(lblWinLose)
      .addY(allNothings[++lblNothingIndex])
      .addY(lblYouHave)
      .addY(allNothings[++lblNothingIndex])
      .addY(
        new GridBug(newBlackPanel())
          .insets(0)
          .addX(lblPlayAgain).insetLeft(3).add(jtfPlayAgain)
          .getContainer()
      )
      .getContainer();
  }

  private Container layoutPnlBet() {
    return new GridBug(pnlBet)
      .insets(0, 0, 0, 0)
      .anchor(GridBug.WEST)
      .gridWidth(1).weightX(0).addX(lblEnterBet1)
        .weightX(1).insetLeft(3).fill(GridBug.HORIZONTAL).insetRight(10).addY(jtfBet)
      .setX(0).insetLeft(0).insetRight(0).gridWidth(2).weightX(0)
      .addY(allNothings[++lblNothingIndex])
      .addY(allNothings[++lblNothingIndex])
      .addY(allNothings[++lblNothingIndex])
      .addY(allNothings[++lblNothingIndex])
      .getContainer();
  }


  /////////////
  // LISTEN: //
  /////////////

  private void click(boolean action) {
    win.setVisible(false);
  }

  private void listen() {
    //win.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    KeyAdapter allListener=new KeyAdapter(){
      @Override public void keyReleased(KeyEvent e){
        Component comp=e.getComponent();
        int keyCode=e.getKeyCode();
        if (keyCode==KeyEvent.VK_W && KeyMapper.modifierPressed(e, currentOS))
          System.exit(0);
        else
        if (keyCode==KeyEvent.VK_ENTER){
          if (comp==jtfBet)
            watcher.betEntered(jtfBet.getText());
        }
        else
        if (comp==jtfMove)
          // This _only_ works if we watch keyReleased & not keyPressed;
          // when the watcher calls us back we need to select-all on the
          // jftMove textbox.
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

  private void handleResizeWindow() {
    int fontSize=win.getHeight() / 32;
    if (fontSize==0) fontSize=1;
    Font font=createFont(fontSize);
    if (!font.equals(blackLabelFont)) {
      blackLabelFont=font;
      for (JComponent comp: allTextComps)
        comp.setFont(font);
      for (JComponent comp: allNothings)
        comp.setFont(font);
      cardPanel.setFont(font);
    }
  }

  private void setVisiblePanel(JPanel p) {
    for (JComponent jc: textPanels)
      if (p!=jc)
        jc.setVisible(false);
    p.setVisible(true);
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
  private static JPanel newColorPanel(Color c) {
    JPanel jp=new JPanel();
    jp.setBackground(c);
    return jp;
  }
  private static JPanel newBlackPanel() {
    return newColorPanel(Color.BLACK);
  }

  /////////////
  /// TEST: ///
  /////////////


  public static void main(final String[] args) throws Exception {
    startup(new ScreenPlayTest());
  }
}

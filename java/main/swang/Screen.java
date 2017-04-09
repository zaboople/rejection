package main.swang;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class Screen {

  private boolean initialized=false;

  private int blackLabelFontSize=14;
  private Font blackLabelFont;

  private JFrame win;
  private NewCardPanel cardPanel;
  private JLabel
    lblStrikeAlert,
    lblKeys,
    lblStrikes,
    lblBet;
  private JTextField jtfBetEntry;

  private class BlackLabel extends JLabel {
    public BlackLabel(String text) {
      super(text);
      setForeground(Color.WHITE);
      setFont(blackLabelFont);
    }
  }


  public void show() {
    init();
    win.setVisible(true);
    win.toFront();
  }

  private void click(boolean action) {
    win.setVisible(false);
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
    win=new JFrame();
    win.setTitle("Rejection");
    blackLabelFont=new Font(Font.MONOSPACED, Font.PLAIN, blackLabelFontSize);
    cardPanel=new NewCardPanel(6, 10);
    cardPanel.setPreferredSize(new Dimension(218, 196));
    lblStrikeAlert=new BlackLabel("STRIKE");
    lblKeys=new BlackLabel(" 0 / 0 ******");
    lblStrikes=new BlackLabel(" 0 / 0 ******");
    lblBet=new BlackLabel("$0 of $0");
  }

  /////////////
  // LAYOUT: //
  /////////////

  private void layout() {
    GridBug gb=new GridBug(win);
    gb
      .gridXY(0)
      .setInsets(0)
      .fill(gb.BOTH)
      .anchor(gb.NORTHWEST)
      .weightXY(1)
      .addY(layoutMiddle())
      .fill(gb.HORIZONTAL)
      .weightXY(1,0)
      .addY(layoutBottom1());
    win.pack();
  }


  private Container layoutTop() {
    JPanel jp=new JPanel();
    GridBug gb=new GridBug(jp);
    gb.gridXY(0);
    gb.weightXY(0);
    gb.fill=gb.BOTH;
    gb.anchor=gb.NORTHWEST;
    gb.setInsets(5);
    gb.insets.top=0;

    return jp;
  }

  private Container layoutMiddle() {
    JPanel jp=new JPanel();
    jp.setBackground(Color.BLACK);
    return new GridBug(jp)
      .insets(9)
      .fill(GridBug.BOTH)
      .weightXY(1)
      .anchor(GridBug.NORTHWEST)
      .add(cardPanel)
      .getContainer();
  }

  private Container layoutBottom1() {
    JPanel jp=new JPanel();
    jp.setBackground(Color.BLACK);
    return new GridBug(jp)
      .insets(0, 0, 0, 5)
      .weightXY(0, 0)
      .anchor(GridBug.NORTHWEST)
      .gridWidth(2)
      .add(lblStrikeAlert)
      .gridWidth(1)
      .addY().setX(0).addX(new BlackLabel("Keys:")).weightX(1).add(lblKeys).weightX(0)
      .addY().setX(0).addX(new BlackLabel("Strikes:")).add(lblStrikes)
      .addY().setX(0).addX(new BlackLabel("Bet:")).add(lblBet)
      .getContainer();
  }
  private Container getButtonPanel() {
    JPanel panel=new JPanel();
    GridBug gb=new GridBug(panel);
    Insets insets=gb.insets;
    insets.top=5;
    insets.bottom=5;
    insets.left=5;
    insets.right=5;

    gb.gridx=0;
    //gb.add(new JButton("FUCKING HELL"));
    return panel;
  }

  /////////////
  // LISTEN: //
  /////////////

  private void listen() {
    //win.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    win.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e){
        if (e.getKeyCode()==KeyEvent.VK_ESCAPE){
          System.exit(0);
        }
      }
    });
    win.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e){
        System.exit(0);
      }
    });
  }

  /////////////
  /// TEST: ///
  /////////////

  public static void main(final String[] args) throws Exception {
    javax.swing.SwingUtilities.invokeLater(()-> {
      try {
        initLookFeel();
        Screen screen=new Screen();
        screen.show();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
  public static void initLookFeel() {
    try {
      javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

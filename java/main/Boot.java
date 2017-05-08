package main;
import main.swang.ScreenPlay;
import main.cmdline.ConsolePlay;

/**
 * The big kahuna entry point. Receives command-line options and selects
 * GUI or command-line play on inputs.
 */
public class Boot {

  public static void main(String[] args) throws Exception {
    GameConfigSetup setup=new GameConfigSetup();
    if (!setup.go(args))
      System.exit(1);
    if (setup.gui)
      ScreenPlay.boot(setup.config, setup.gamble);
    else
      new ConsolePlay(setup.config, setup.gamble).play();

  }

}

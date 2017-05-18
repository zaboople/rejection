package main;
import java.io.File;
import java.io.FileInputStream;

/** Acts as the command-line setup system. */
class GameConfigSetup {
  public GameConfig config;
  public Gamble gamble;
  public boolean gui=true;

  private static boolean help(String error) {
    if (error!=null) System.out.println("Error: "+error);
    System.out.println(
      "Usage: [--config <file>] [--wager <amount>] [--gui|--ascii]\n"+
      "  --config Use a configuration file. Look in sample-configs.\n"+
      "  --wager  Enable betting. The <amount> is your total wager.\n"+
      "  --gui    \n"+
      "  --ascii  GUI mode (default) is better, even though it doesn't \n"+
      "           try that hard to be \"graphical\". \n"+
      "           Hidden feature: Command/Ctrl-W will exit anytime. \n"
    );
    return error==null;
  }

  public boolean go(String[] args) throws Exception{
    for (int i=0; i<args.length; i++)
      if (args[i].equals("-h") || args[i].equals("--help")){
        help(null);
        return false;
      }
      else
      if (args[i].equals("-g") || args[i].equals("--gui"))
        gui=true;
      else
      if (args[i].equals("-a") || args[i].equals("--ascii"))
        gui=false;
      else
      if (args[i].equals("-w") || args[i].equals("--wager") || args[i].equals("--gamble")){
        i++;
        if (i==args.length)
          return help("Expected a number");
        Integer amount;
        try {amount=Integer.parseInt(args[i]);}
        catch (Exception e) {return help("Not a parseable number: "+args[i]);}
        gamble=new Gamble(amount);
      }
      else
      if (args[i].equals("-c") || args[i].equals("--config")){
        i++;
        if (i==args.length)
          return help("Error: Expected a filename");
        String a=args[i];
        File file=new File(a);
        if (!file.exists())
          return help("Not a file: "+a);
        try (FileInputStream instr=new FileInputStream(file)) {
          config=new GameConfig(instr);
        } catch (Exception e) {
          return help(e.getMessage());
        }
      }
      else
        return help("Unexpected: "+args[i]);

    if (config==null)
      config=new GameConfig();

    return true;
  }

}


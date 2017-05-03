package main;
import java.io.File;
import java.io.FileInputStream;

public class GameConfigSetup {
  public GameConfig config;
  public Gamble gamble;

  private static boolean help(String error) {
    if (error!=null) System.out.println("Error: "+error);
    System.out.println("Usage: [--config <file>] [--wager <amount>]");
    return error==null;
  }

  public boolean go(String[] args) throws Exception{
    for (int i=0; i<args.length; i++)
      if (args[i].equals("-h") || args[i].equals("--help")){
        help(null);
        return false;
      }
      else
      if (args[i].equals("-w") || args[i].equals("--wager")){
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
        try {
          config=new GameConfig().load(new FileInputStream(file));
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


package test;
import main.*;

public class RandomNoRepeatTest {
  public static void main(String[] args) {
    for (String arg: args) {
      int limit=Integer.parseInt(arg);
      RandomNoRepeat r=new RandomNoRepeat(limit);
      for (int i=0; i<limit; i++)
        System.out.println(r.next());
    }
  }
}
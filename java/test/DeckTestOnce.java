package test;

import main.Card;
import main.Deck;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.security.SecureRandom;

public class DeckTestOnce {
  public static void main(String[] args) {
    test(args);
  }

  private static void test(String[] args) {
    if (args.length!=Deck.possible.length)
      throw new IllegalArgumentException("Need "+Deck.possible.length+" got "+args.length);
    int[] distributions=Arrays.stream(args).mapToInt(Integer::parseInt).toArray();
    for (int i=0; i<Deck.possible.length; i++)
      System.out.print(Deck.possible[i].toStringShort()+"="+distributions[i]+" ");
    System.out.println();

    SecureRandom randomizer=new SecureRandom();
    Deck deck=new Deck(randomizer, distributions);
    while (deck.hasNext()) {
      System.out.print(deck.next().toStringShort());
    }
    System.out.println();
  }
}
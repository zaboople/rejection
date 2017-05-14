package test;

import main.Card;
import main.Deck;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.security.SecureRandom;

/**
 * Given inputs for the number of each type of card (strike, bar, corner, cross, tee),
 * counts the number of times each card type appears in each possible deck index
 * over 10000 random decks.
 */
public class DeckTest {
  public static void main(String[] args) {
    test(args);
  }

  private static void test(String[] args) {
    if (args.length!=Deck.possible.length)
      throw new IllegalArgumentException("Need "+Deck.possible.length+" got "+args.length);
    int[] distributions=Arrays.stream(args).mapToInt(Integer::parseInt).toArray();
    Map<String, Map<Integer,Integer>> cardMap=new HashMap<>();
    for (Card c: Deck.possible)
      cardMap.put(c.toString(), new HashMap<>());
    SecureRandom randomizer=new SecureRandom();
    IntStream.range(0, 10000).forEach(__ -> {
      Deck deck=new Deck(randomizer, distributions);
      for (int d=0; d<deck.size(); d++) {
        Map<Integer, Integer> counts=cardMap.get(deck.next().toString());
        Integer c=counts.get(d);
        if (c==null) c=0;
        counts.put(d, c+1);
      }
    });
    for (Card c: Deck.possible)
      System.out.println("\n" + c + cardMap.get(c.toString()));
  }
}
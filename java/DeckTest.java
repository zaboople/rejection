import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.IntStream;

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
    IntStream.range(0, 10000).forEach(blahblah-> {
      Deck deck=new Deck(distributions);
      for (int d=0; d<deck.size(); d++) {
        String card=deck.next().toString();
        Map<Integer, Integer> counts=cardMap.get(card);
        Integer c=counts.get(d);
        if (c==null) c=0;
        counts.put(d, c+1);
      }
    });
    for (Card c: Deck.possible)
      System.out.println("\n" + c + cardMap.get(c.toString()));
  }
}
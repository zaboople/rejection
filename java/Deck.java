import java.security.SecureRandom;
import java.util.Arrays;

public class Deck {

  private static final boolean TT=true, __=false;

  final static Card[] possible={
    Card.strike(),
    // 2's:
    Card.path(TT, TT, __, __),
    Card.path(__, TT, TT, __),
    Card.path(__, __, TT, TT),

    Card.path(TT, __, __, TT),
    Card.path(TT, __, TT, __),
    Card.path(__, TT, __, TT),
    // 3's:
    Card.path(__, TT, TT, TT),
    Card.path(TT, __, TT, TT),
    Card.path(TT, TT, __, TT),
    Card.path(TT, TT, TT, __),
    // 4:
    Card.path(TT, TT, TT, TT)
  };

  private final Card[] cards;
  private int cardIndex=0;

  /**
   * Accepts an array of distributions per card type. There are exactly 7 kinds of card,
   * so the array should be that size. There will be that many cards per type, randomly
   * shuffled into this deck.
   * <br>
   * Refer to the possible[] array for the mapping from distributions to card type.
   */
  public Deck(SecureRandom randomizer, int... distributions) {
    if (distributions.length!=possible.length)
      throw new RuntimeException("Mismatch "+distributions.length+" is not "+possible.length);
    cards=new Card[
      Arrays.stream(distributions).reduce(0, (x, y) -> x+y)
    ];

    // Build an unshuffled (raw) list of cards:
    final Card[] rawCards=new Card[cards.length];
    {
      int allocIndex=0;
      for (int d=0; d<distributions.length; d++)
        for (int a=0; a<distributions[d]; a++)
          rawCards[allocIndex++]=possible[d];
    }

    // Allocate those cards randomly to our actual array. This
    // is not hugely performant (n log n ?), but it seems secure:
    for (int waiting=cards.length; waiting>0; waiting--) {
      int select=-1;
      int countdown=randomizer.nextInt(waiting);
      for (int rc=0; select==-1; rc++)
        if (rawCards[rc]!=null && --countdown == -1)
          select=rc;
      cards[waiting-1]=rawCards[select];
      rawCards[select]=null;
    }

    // Verification:
    for (Card card: rawCards)
      if (card!=null)
        throw new IllegalStateException("Unused: "+card);
    for (Card card: cards)
      if (card==null)
        throw new IllegalStateException("Missing card");
  }
  public Deck(int... distributions) {
    this(new SecureRandom(), distributions);
  }

  public Card next() {
    if (cardIndex<cards.length)
      return cards[cardIndex++];
    throw new IllegalStateException("Not enough cards");
  }
  public int size() {
    return cards.length;
  }


}
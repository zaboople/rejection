package main;

import java.security.SecureRandom;
import java.util.Arrays;

public class Deck {

  private static final boolean TT=true, __=false;

  public final static Card[] possible={
    Card.strike(),
    Card.pathCorner(),
    Card.pathBar(),
    Card.pathTee(),
    Card.pathCross()
  };

  private final Card[] cards;
  private int cardIndex=0;

  /**
   * Accepts an array of distributions per card type. There are exactly 5 kinds of card,
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

    // Allocate those cards randomly:
    RandomNoRepeat
      fromRandom=new RandomNoRepeat(randomizer, rawCards.length),
      toRandom=new RandomNoRepeat(randomizer, cards.length);
    for (int i=0; i<cards.length; i++){
      int from=fromRandom.next();
      cards[toRandom.next()]=rawCards[from];
      rawCards[from]=null;
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
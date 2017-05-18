package main;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Represents the deck of cards. Maintains an internal
 * array of Card objects for the duration of use.
 */
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
  public boolean hasNext() {
    return cardIndex<cards.length;
  }


  /** Leftover stuff. I thought maybe the sequential setup of raw cards
      was causing bunching-up of card types in the shuffled deck. */
  private void alternateRawSetup(Card[] rawCards, int[] distributions) {
    int[][] distAllocs=new int[distributions.length][];
    for (int d=0; d<distributions.length; d++){
      int allocPer=distributions[d];
      distAllocs[d]=new int[allocPer];
    }
    for (int d=0; d<distributions.length; d++){
      int alloc=distributions[d];
      int spread=cards.length / alloc;
      int index=spread+d;
      while (alloc>0) {
        while (rawCards[index]!=null) {
          //System.out.print("Z"+d+"_"+alloc+" ");
          index++;
          if (index>=cards.length)
            index=0;
        }
        alloc--;
        rawCards[index]=possible[d];
        index+=spread;
        if (index>=cards.length)
          index=0;
      }
    }
    //System.out.println();
    for (Card c: rawCards)
      System.out.print(c.toString().substring(0,1));
    System.out.println();
  }

}
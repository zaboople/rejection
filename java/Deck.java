public class Deck {
  private static Card[] possible={
    Card.strike(),
    // 2's:
    Card.path(true, true, false, false),
    Card.path(true, false, true, false),
    Card.path(true, false, false, true),
    // 3's:
    Card.path(true, true, true, false),
    Card.path(false, true, true, true),
    // 4:
    Card.path(true, true, true, true)
  };

  private final Partitioner partitioner;
  public Deck(int... distributions) {
    if (distributions.length!=possible.length)
      throw new RuntimeException("Mismatch "+distributions.length+" is not "+possible.length);
    partitioner=new Partitioner(distributions);
  }
  public Card next() {
    return possible[partitioner.nextIndex()];
  }
}
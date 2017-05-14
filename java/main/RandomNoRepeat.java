package main;
import java.security.SecureRandom;

/**
 * Generates non-repeating random numbers across a range until we run
 * out of numbers. Maintains an internal array of used values to match
 * that range.
 * <br>
 * This is not hugely performant (n log n ?), but it seems secure
 * enough. Don't use it for very large ranges.
 */
public class RandomNoRepeat {

  private final SecureRandom rand;
  private final boolean[] used;

  public RandomNoRepeat(int size) {
    this(new SecureRandom(), size);
  }
  /** Creates an instance allowing range between 0 and size, exclusive. */
  public RandomNoRepeat(SecureRandom rand, int size) {
    this(rand, new boolean[size]);
  }
  public RandomNoRepeat(SecureRandom rand, boolean[] used) {
    this.rand=rand;
    this.used=used;
  }
  /**
   * @throws IllegalStateException If all possible numbers are used up.
   */
  public int next() {
    int orig=rand.nextInt(used.length);
    int n=orig;
    while (used[n]){
      n++;
      if (n==used.length)
        n=0;
      if (n==orig)
        throw new IllegalStateException("No possible values left in "+used.length);
    }
    used[n]=true;
    return n;
  }
  /**
   * Creates an array of size count, and fills it with numbers randomly selected
   * from our designated range. Note that numbers previously selected by either fill() or next()
   * will not be used.
   * @throws IllegalStateException If we run out of values to use.
   */
  public int[] fill(int count) {
    final int[] array=new int[count];
    for (int i=0; i<array.length; i++)
      array[i]=next();
    return array;
  }

}

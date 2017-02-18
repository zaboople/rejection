package main;

import java.security.SecureRandom;
public class RandomNoRepeat {

  private final SecureRandom rand;
  private final boolean[] used;

  public RandomNoRepeat(int size) {
    this(new SecureRandom(), size);
  }
  public RandomNoRepeat(SecureRandom rand, int size) {
    this(rand, new boolean[size]);
  }
  public RandomNoRepeat(SecureRandom rand, boolean[] used) {
    this.rand=rand;
    this.used=used;
  }
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
}
package test;
import main.*;
import java.security.SecureRandom;

/**
 * Tests a RandomNoRepeat of range X by filling arrays increasing in size
 * from 1 thru X with random numbers. Each array is constructed using a new
 * RandomNoRepeat. Multiple arrays of the same size are tested, so we can count
 * the number of times a given number appears.
 */
public class RandomNoRepeatTest {
  public static void main(String[] args) throws Exception {
    if (args.length<1) {
      System.out.println("Need a range input");
    }
    SecureRandom randomizer=new SecureRandom();
    int range=Integer.parseInt(args[0]);
    int repeat=range * range;
    int[] verify=new int[range];

    for (int scope=1; scope<range; scope++) {
      System.out.print("Array size "); System.out.print(scope); System.out.print(":");
      for (int i=0; i<verify.length; i++)
        verify[i]=0;
      for (int repeatIndex=0; repeatIndex<repeat; repeatIndex++) {
        RandomNoRepeat generator=new RandomNoRepeat(randomizer, range);
        int[] result=generator.fill(scope);
        for (int i=0; i<result.length; i++)
          verify[result[i]]++;
      }
      for (int v: verify) {
        System.out.print(" ");
        System.out.print(v);
      }
      System.out.println();
    }
  }
}
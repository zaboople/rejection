package main;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** FIXME document */
public class Partitioner {
  private final SecureRandom randomizer;
  private final int partitions[];
  public Partitioner(SecureRandom randomizer, int... distribution) {
    this.randomizer=randomizer;
    int total=Arrays.stream(distribution).reduce(0, (x, y) -> x+y);
    int size=Integer.MAX_VALUE / total;
    this.partitions=Arrays.stream(distribution).map(x -> x * size).toArray();
    for (int i=1; i<partitions.length-1; i++)
      partitions[i] += partitions[i-1];
    partitions[partitions.length-1]=Integer.MAX_VALUE;
  }
  public Partitioner(int... distribution) {
    this(new SecureRandom(), distribution);
  }
  public String toString() {
    return Arrays.stream(partitions)
      .mapToObj(String::valueOf)
      .collect(Collectors.joining(", "));
  }
  public int nextIndex() {
    int mark=Math.abs(randomizer.nextInt());
    for (int i=0; i<partitions.length; i++)
      if (mark < partitions[i])
        return i;
    return partitions.length-1;
  }
  public Stream<Integer> stream() {
    return Stream.generate(this::nextIndex);
  }
  public int partitionCount() {
    return partitions.length;
  }


  public static void main(String[] args) {
    test(args);
  }
  private static void test(String[] args) {
    Partitioner p=new Partitioner(Arrays.stream(args).mapToInt(Integer::parseInt).toArray());
    System.out.println(p);
    int[] counts=new int[p.partitionCount()];
    for (int i=0; i<100; i++)
      counts[p.nextIndex()]++;
    System.out.println(
      Arrays.stream(counts)
        .mapToObj(String::valueOf)
        .collect(Collectors.joining(","))
    );
    System.out.flush();
  }
}

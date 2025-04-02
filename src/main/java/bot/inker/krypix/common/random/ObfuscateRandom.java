package bot.inker.krypix.common.random;

import java.util.UUID;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public interface ObfuscateRandom {
  static ObfuscateRandom fromJava(java.util.Random random) {
    return new JavaUtilRandomWrapper(random);
  }

  void nextBytes(byte[] bytes);

  default byte[] nextBytes(int length) {
    byte[] bytes = new byte[length];
    nextBytes(bytes);
    return bytes;
  }

  int nextInt();

  int nextInt(int bound);

  long nextLong();

  boolean nextBoolean();

  float nextFloat();

  double nextDouble();

  double nextGaussian();

  IntStream ints(long streamSize);

  IntStream ints();

  IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound);

  IntStream ints(int randomNumberOrigin, int randomNumberBound);

  LongStream longs(long streamSize);

  LongStream longs();

  LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound);

  LongStream longs(long randomNumberOrigin, long randomNumberBound);

  DoubleStream doubles(long streamSize);

  DoubleStream doubles();

  DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound);

  DoubleStream doubles(double randomNumberOrigin, double randomNumberBound);

  default UUID nextUUID() {
    var randomBytes = nextBytes(16);
    randomBytes[6] &= 0x0f;  /* clear version        */
    randomBytes[6] |= 0x40;  /* set to version 4     */
    randomBytes[8] &= 0x3f;  /* clear variant        */
    randomBytes[8] |= (byte) 0x80;  /* set to IETF variant  */
    var msb = 0L;
    var lsb = 0L;
    for (int i = 0; i < 8; i++)
      msb = (msb << 8) | (randomBytes[i] & 0xff);
    for (int i = 8; i < 16; i++)
      lsb = (lsb << 8) | (randomBytes[i] & 0xff);
    return new UUID(msb, lsb);
  }
}

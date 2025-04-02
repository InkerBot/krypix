package bot.inker.krypix.common.random;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public final class PRNGObfuscateRandom extends Random implements ObfuscateRandom {
  private final PseudoRandomNumberGenerator prng;
  private byte[] buffer;
  private int pos;

  public PRNGObfuscateRandom(PseudoRandomNumberGenerator prng) {
    this.prng = prng;
    this.buffer = new byte[0];
    this.pos = 0;
  }

  private byte nextByte() {
    if (pos >= buffer.length) {
      buffer = prng.nextBlock();
      pos = 0;
    }
    return buffer[pos++];
  }

  @Override
  protected int next(int bits) {
    int result = 0;
    for (int i = 0; i < bits; i += 8) {
      result = (result << 8) | (nextByte() & 0xFF);
    }
    return result >>> (8 - bits);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    PRNGObfuscateRandom that = (PRNGObfuscateRandom) o;
    return pos == that.pos && Objects.equals(prng, that.prng) && Objects.deepEquals(buffer, that.buffer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(prng, Arrays.hashCode(buffer), pos);
  }
}

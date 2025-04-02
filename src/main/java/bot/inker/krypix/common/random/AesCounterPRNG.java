package bot.inker.krypix.common.random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class AesCounterPRNG implements PseudoRandomNumberGenerator {
  public static final int BLOCK_SIZE = 32;
  private final Cipher cipher;
  private long counter;

  public AesCounterPRNG(byte[] seed) {
    // Use the first 16 bytes of the seed; pad with zeros if needed.
    byte[] keyBytes = new byte[BLOCK_SIZE];
    System.arraycopy(seed, 0, keyBytes, 0, Math.min(seed.length, BLOCK_SIZE));
    SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
    try {
      cipher = Cipher.getInstance("AES/ECB/NoPadding");
      cipher.init(Cipher.ENCRYPT_MODE, keySpec);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
      throw new IllegalStateException("Failed to init cryptographic pseudorandom number generator", e);
    }
    counter = 0;
  }

  @Override
  public int blockSize() {
    return BLOCK_SIZE;
  }

  @Override
  public byte[] nextBlock() {
    ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
    // The first 8 bytes are zeros; the last 8 bytes store the counter.
    buffer.putLong(8, counter);
    buffer.putLong(16, counter++);
    buffer.putLong(24, counter + 1);
    try {
      return cipher.doFinal(buffer.array());
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      throw new IllegalStateException("Failed to init cryptographic pseudorandom number generator", e);
    }
  }
}
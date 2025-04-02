package bot.inker.krypix.common.random;

public interface PseudoRandomNumberGenerator {
  int blockSize();

  byte[] nextBlock();
}

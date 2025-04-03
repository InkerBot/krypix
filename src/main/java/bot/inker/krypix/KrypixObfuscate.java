package bot.inker.krypix;

import bot.inker.krypix.common.random.AesCounterPRNG;
import bot.inker.krypix.common.random.ObfuscateRandom;
import bot.inker.krypix.common.random.PRNGObfuscateRandom;
import bot.inker.krypix.util.uncheck.UncheckUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class KrypixObfuscate {
  private static final Logger logger = LoggerFactory.getLogger(KrypixObfuscate.class);
  private static final boolean DEBUG = logger.isDebugEnabled() && Boolean.getBoolean("krypix.debug");

  private final ObfuscateRandom random;
  private final File tempDir;
  private final AppView appView;

  private KrypixObfuscate(ObfuscateRandom random, File tempDir, boolean withStandardScope) {
    ensureTempDir(tempDir);
    this.random = random;
    this.tempDir = tempDir;
    this.appView = new AppView(new File(tempDir, "appview"), withStandardScope);
  }

  private static void ensureTempDir(File tempDir) {
    if (tempDir.exists()) {
      try {
        FileUtils.deleteDirectory(tempDir);
      } catch (IOException e) {
        throw UncheckUtil.uncheck(e);
      }
    }
    try {
      FileUtils.forceMkdir(tempDir);
    } catch (IOException e) {
      throw UncheckUtil.uncheck(e);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public ObfuscateRandom random() {
    return random;
  }

  public File tempDir() {
    return tempDir;
  }

  public AppView appView() {
    return appView;
  }

  public static final class Builder {
    // 0 = not set, 1 = specified, 2 = seed
    private int randomStatus = 0;
    private ObfuscateRandom random;
    private File tempDir;
    private final boolean withStandardScope = true;

    public Builder random(ObfuscateRandom random) {
      if (randomStatus == 2) {
        logger.warn("Random has already been set by seed, overriding by random");
      }
      randomStatus = 1;
      this.random = random;
      return this;
    }

    public Builder seed(byte[] seed) {
      if (randomStatus == 1) {
        logger.warn("Random has already been set by random, overriding by seed");
      }
      randomStatus = 2;
      if (seed.length != AesCounterPRNG.BLOCK_SIZE) {
        try {
          var digest = MessageDigest.getInstance("SHA-256");
          seed = digest.digest(seed);
        } catch (NoSuchAlgorithmException e) {
          throw UncheckUtil.uncheck(e);
        }
      }
      if (DEBUG) {
        logger.debug("Seed: {}", Base64.getEncoder().encodeToString(seed));
      }
      this.random = new PRNGObfuscateRandom(new AesCounterPRNG(seed));
      return this;
    }

    public Builder tempDir(File tempDir) {
      this.tempDir = tempDir;
      return this;
    }

    public KrypixObfuscate build() {
      return new KrypixObfuscate(random, tempDir, withStandardScope);
    }
  }
}

package bot.inker.krypix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws IOException {
    logger.info("Running Krypix with {}", (Object) args);

    KrypixObfuscate obfuscate = KrypixObfuscate.builder()
      .seed(KrypixStandards.TEST_SEED)
      .tempDir(new File("tmp"))
      .build();

    obfuscate.appView()
      .loader(KrypixStandards.SCOPE_PROGRAM)
      .load(new File("input"));

    obfuscate.appView()
      .loader(KrypixStandards.SCOPE_LIBRARY)
      .load(new File("/Users/inkerbot/Library/Java/JavaVirtualMachines/azul-23.0.2/Contents/Home/jmods"));

    obfuscate.appView().build();

    System.out.println();
  }
}

package bot.inker.krypix;

import bot.inker.krypix.asm.KrypixControlStringer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    obfuscate.appView().allClasses().forEach(clazz -> {
      if (!clazz.scope().mutable()) {
        return;
      }
      Path path = Paths.get("dump", clazz.name());
      for (int i = 0; i < clazz.methods().size(); i++) {
        KrypixMethod method = clazz.methods().get(i);
        if (!method.hasCode()) {
          continue;
        }

        String methodString = new KrypixControlStringer(obfuscate.appView(), method).stringer();
        try {
          Path outputPath = path.resolve(i + "_" + method.name() + ".txt");
          Files.createDirectories(outputPath.getParent());
          try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write(methodString);
          }
        } catch (IOException e) {
          logger.error("Failed to write method string to file at {} {}", clazz, method, e);
        }
      }
    });
    System.out.println();
  }
}

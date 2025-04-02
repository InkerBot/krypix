package bot.inker.krypix;

import java.nio.charset.StandardCharsets;

public final class KrypixStandards {
  public static final byte[] TEST_SEED = "飞雪连天射白鹿，笑书神侠倚碧鸳".getBytes(StandardCharsets.UTF_8);

  public static final String SCOPE_PROGRAM = "program";
  public static final String SCOPE_LIBRARY = "library";
  public static final String SCOPE_EXTERNAL = "external";
}

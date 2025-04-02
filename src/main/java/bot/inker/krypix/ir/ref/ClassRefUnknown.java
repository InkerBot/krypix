package bot.inker.krypix.ir.ref;

import bot.inker.krypix.KrypixClass;

public final class ClassRefUnknown implements ClassRef {
  private final String name;

  public ClassRefUnknown(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }
}

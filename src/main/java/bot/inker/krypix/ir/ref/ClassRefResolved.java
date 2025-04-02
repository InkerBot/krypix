package bot.inker.krypix.ir.ref;

import bot.inker.krypix.KrypixClass;

public final class ClassRefResolved implements ClassRef {
  private final KrypixClass clazz;

  public ClassRefResolved(KrypixClass clazz) {
    this.clazz = clazz;
  }

  public KrypixClass get() {
    return clazz;
  }

  @Override
  public String name() {
    return clazz.name();
  }
}

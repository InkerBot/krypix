package bot.inker.krypix.ir.ref;

import bot.inker.krypix.KrypixMethod;

public class MethodRefResolved implements MethodRef {
  private final KrypixMethod method;

  public MethodRefResolved(KrypixMethod method) {
    this.method = method;
  }

  public KrypixMethod get() {
    return method;
  }

  @Override
  public ClassRefResolved owner() {
    return new ClassRefResolved(method.owner());
  }

  @Override
  public String name() {
    return method.name();
  }

  @Override
  public MethodType desc() {
    return method.desc();
  }
}

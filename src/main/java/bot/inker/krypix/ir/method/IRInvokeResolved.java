package bot.inker.krypix.ir.method;

import bot.inker.krypix.KrypixMethod;

public final class IRInvokeResolved implements IRInvoke {
  private final Type type;
  private final KrypixMethod method;

  public IRInvokeResolved(Type type, KrypixMethod method) {
    this.type = type;
    this.method = method;
  }

  @Override
  public Type type() {
    return type;
  }

  public KrypixMethod method() {
    return method;
  }

  @Override
  public String owner() {
    return method.owner().name();
  }

  @Override
  public String name() {
    return method.name();
  }

  @Override
  public String desc() {
    return method.desc();
  }
}

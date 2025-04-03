package bot.inker.krypix.ir.method;

import bot.inker.krypix.KrypixMethod;
import bot.inker.krypix.ir.ref.MethodType;

public final class IRInvokeMethodResolved implements IRInvokeMethod {
  private final Type type;
  private final KrypixMethod method;

  public IRInvokeMethodResolved(Type type, KrypixMethod method) {
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
  public MethodType desc() {
    return method.type();
  }
}

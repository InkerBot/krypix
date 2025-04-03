package bot.inker.krypix.ir.method;

import bot.inker.krypix.ir.ref.MethodType;

public final class IRInvokeMethodUnknown implements IRInvokeMethod {
  private final Type type;
  private final String owner;
  private final String name;
  private final MethodType methodType;

  public IRInvokeMethodUnknown(Type type, String owner, String name, MethodType methodType) {
    this.type = type;
    this.owner = owner;
    this.name = name;
    this.methodType = methodType;
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public String owner() {
    return owner;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public MethodType desc() {
    return methodType;
  }
}

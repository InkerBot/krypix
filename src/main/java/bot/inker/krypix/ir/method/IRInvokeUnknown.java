package bot.inker.krypix.ir.method;

public final class IRInvokeUnknown implements IRInvoke {
  private final Type type;
  private final String owner;
  private final String name;
  private final String desc;

  public IRInvokeUnknown(Type type, String owner, String name, String desc) {
    this.type = type;
    this.owner = owner;
    this.name = name;
    this.desc = desc;
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
  public String desc() {
    return desc;
  }
}

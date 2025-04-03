package bot.inker.krypix.ir.field;

public final class IRFieldLoadUnknown implements IRFieldLoad {
  private final boolean isStatic;
  private final String owner;
  private final String name;
  private final String desc;

  public IRFieldLoadUnknown(boolean isStatic, String owner, String name, String desc) {
    this.isStatic = isStatic;
    this.owner = owner;
    this.name = name;
    this.desc = desc;
  }

  @Override
  public boolean isStatic() {
    return isStatic;
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

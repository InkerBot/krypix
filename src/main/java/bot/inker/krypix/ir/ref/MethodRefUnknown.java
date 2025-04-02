package bot.inker.krypix.ir.ref;

public final class MethodRefUnknown implements MethodRef {
  private final ClassRef owner;
  private final String name;
  private final MethodType desc;

  public MethodRefUnknown(ClassRef owner, String name, MethodType desc) {
    this.owner = owner;
    this.name = name;
    this.desc = desc;
  }

  @Override
  public ClassRef owner() {
    return owner;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public MethodType desc() {
    return desc;
  }
}

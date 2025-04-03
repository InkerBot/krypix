package bot.inker.krypix.ir.ref;

public final class FieldRefUnknown implements FieldRef {
  private final ClassRef owner;
  private final String name;
  private final TypeRef desc;
  private final boolean isStatic;

  public FieldRefUnknown(ClassRef owner, String name, TypeRef desc, boolean isStatic) {
    this.owner = owner;
    this.name = name;
    this.desc = desc;
    this.isStatic = isStatic;
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
  public TypeRef desc() {
    return desc;
  }

  @Override
  public boolean isStatic() {
    return isStatic;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(owner.name()).append(" ");
    if (isStatic()) {
      sb.append("static ");
    } else {
      sb.append("field ");
    }
    sb.append(name()).append(" ").append(desc);
    return sb.toString();
  }
}

package bot.inker.krypix.ir.ref;

import bot.inker.krypix.KrypixField;

public final class FieldRefResolved implements FieldRef {
  private final KrypixField field;

  public FieldRefResolved(KrypixField field) {
    this.field = field;
  }

  public KrypixField field() {
    return field;
  }

  @Override
  public ClassRef owner() {
    return new ClassRefResolved(field.owner());
  }

  @Override
  public String name() {
    return field.name();
  }

  @Override
  public TypeRef desc() {
    return field.type();
  }

  @Override
  public boolean isStatic() {
    return field.isStatic();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(field.owner().name()).append(" ");
    if (field.isStatic()) {
      sb.append("static ");
    } else {
      sb.append("field ");
    }
    sb.append(field.name()).append(" ").append(field.type());
    return sb.toString();
  }
}

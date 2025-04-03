package bot.inker.krypix.ir.field;

import bot.inker.krypix.ir.ref.ClassRef;
import bot.inker.krypix.ir.ref.FieldRef;
import bot.inker.krypix.ir.ref.TypeRef;

public final class IRFieldStore implements IRField {
  private final FieldRef field;

  public IRFieldStore(FieldRef field) {
    this.field = field;
  }

  @Override
  public FieldRef field() {
    return field;
  }

  @Override
  public boolean isStatic() {
    return field.isStatic();
  }

  @Override
  public ClassRef owner() {
    return field.owner();
  }

  @Override
  public String name() {
    return field.name();
  }

  @Override
  public TypeRef desc() {
    return field.desc();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("put");
    if (field.isStatic()) {
      sb.append("static ");
    } else {
      sb.append("field ");
    }
    sb.append(field.owner().name()).append(" ");
    sb.append(field.name()).append(" ").append(field.desc());
    return sb.toString();
  }
}

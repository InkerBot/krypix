package bot.inker.krypix.ir.field;

import bot.inker.krypix.KrypixField;
import bot.inker.krypix.ir.ref.ClassRef;
import bot.inker.krypix.ir.ref.ClassRefResolved;

public final class IRFieldLoadResolved implements IRFieldLoad {
  private final KrypixField field;

  public IRFieldLoadResolved(KrypixField field) {
    this.field = field;
  }

  public KrypixField field() {
    return field;
  }

  @Override
  public boolean isStatic() {
    return field.isStatic();
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
  public String desc() {
    return field.desc();
  }
}

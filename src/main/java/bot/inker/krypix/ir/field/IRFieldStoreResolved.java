package bot.inker.krypix.ir.field;

import bot.inker.krypix.KrypixField;

public final class IRFieldStoreResolved implements IRFieldStore {
  private final KrypixField field;

  public IRFieldStoreResolved(KrypixField field) {
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
  public String owner() {
    return field.owner().name();
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

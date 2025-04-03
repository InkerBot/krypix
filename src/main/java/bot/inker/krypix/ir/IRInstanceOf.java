package bot.inker.krypix.ir;

import bot.inker.krypix.ir.ref.TypeRef;

public final class IRInstanceOf implements IRAbstract {
  private final TypeRef type;

  public IRInstanceOf(TypeRef type) {
    this.type = type;
  }

  public TypeRef type() {
    return type;
  }

  @Override
  public String toString() {
    return "instanceof " + type;
  }
}

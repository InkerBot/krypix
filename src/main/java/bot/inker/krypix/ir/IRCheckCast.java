package bot.inker.krypix.ir;

import bot.inker.krypix.ir.ref.TypeRef;

public final class IRCheckCast implements IRAbstract {
  private final TypeRef type;

  public IRCheckCast(TypeRef type) {
    this.type = type;
  }

  public TypeRef type() {
    return type;
  }
}

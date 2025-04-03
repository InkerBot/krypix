package bot.inker.krypix.ir.array;

import bot.inker.krypix.ir.BaseValueType;

public final class IRArrayLoad implements IRArrayOperator {
  private final BaseValueType type;

  public IRArrayLoad(BaseValueType type) {
    this.type = (type == BaseValueType.BOOLEAN)
      ? BaseValueType.BYTE
      : type;
  }

  @Override
  public BaseValueType type() {
    return type;
  }

  @Override
  public String toString() {
    return "arrayload " + type.name().toLowerCase();
  }
}

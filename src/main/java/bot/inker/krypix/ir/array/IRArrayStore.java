package bot.inker.krypix.ir.array;

import bot.inker.krypix.ir.BaseValueType;

public final class IRArrayStore implements IRArrayOperator {
  private final BaseValueType type;

  public IRArrayStore(BaseValueType type) {
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
    return "arraystore " + type.name().toLowerCase();
  }
}

package bot.inker.krypix.ir.num;

import bot.inker.krypix.ir.BaseFrameType;
import bot.inker.krypix.ir.BaseValueType;

public final class IRMathCast implements IRMath {
  private final BaseFrameType sourceType;
  private final BaseValueType targetType;

  public IRMathCast(BaseFrameType sourceType, BaseValueType targetType) {
    this.sourceType = sourceType;
    this.targetType = targetType;
  }

  @Override
  public BaseFrameType type() {
    return sourceType;
  }

  public BaseValueType targetType() {
    return targetType;
  }
}

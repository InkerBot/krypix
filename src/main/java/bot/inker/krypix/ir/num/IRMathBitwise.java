package bot.inker.krypix.ir.num;

import bot.inker.krypix.ir.BaseFrameType;

public final class IRMathBitwise implements IRMath {
  private final BaseFrameType type;
  private final Operation operation;

  public IRMathBitwise(BaseFrameType type, Operation operation) {
    this.type = type;
    this.operation = operation;
  }

  @Override
  public BaseFrameType type() {
    return type;
  }

  public Operation operation() {
    return operation;
  }

  @Override
  public String toString() {
    return operation.name().toLowerCase() + " " + type.name().toLowerCase();
  }

  public enum Operation {
    SHL, SHR, USHR, AND, OR, XOR
  }
}

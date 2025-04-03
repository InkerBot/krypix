package bot.inker.krypix.ir.num;

import bot.inker.krypix.ir.BaseFrameType;

public final class IRMathBinary implements IRMath {
  private final BaseFrameType type;
  private final Operation operation;

  public IRMathBinary(BaseFrameType type, Operation operation) {
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

  public enum Operation {
    ADD, SUB, MUL, DIV, REM, SHL, SHR, USHR, AND, OR, XOR
  }
}

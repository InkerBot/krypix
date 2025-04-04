package bot.inker.krypix.ir;

import org.objectweb.asm.Opcodes;

public enum BaseValueType {
  INT(BaseFrameType.INT), LONG(BaseFrameType.LONG), FLOAT(BaseFrameType.FLOAT), DOUBLE(BaseFrameType.DOUBLE), OBJECT(BaseFrameType.OBJECT),
  BOOLEAN(BaseFrameType.INT), BYTE(BaseFrameType.INT), CHAR(BaseFrameType.INT), SHORT(BaseFrameType.INT);

  private final BaseFrameType frameType;

  BaseValueType(BaseFrameType frameType) {
    this.frameType = frameType;
  }

  public BaseFrameType frameType() {
    return frameType;
  }

  public int forOpcode(int opcode) {
    if (opcode == Opcodes.IALOAD || opcode == Opcodes.IASTORE) {
      return switch (this) {
        case BOOLEAN, BYTE -> opcode + (Opcodes.BALOAD - Opcodes.IALOAD);
        case CHAR -> opcode + (Opcodes.CALOAD - Opcodes.IALOAD);
        case SHORT -> opcode + (Opcodes.SALOAD - Opcodes.IALOAD);
        case INT -> opcode;
        case FLOAT -> opcode + (Opcodes.FALOAD - Opcodes.IALOAD);
        case LONG -> opcode + (Opcodes.LALOAD - Opcodes.IALOAD);
        case DOUBLE -> opcode + (Opcodes.DALOAD - Opcodes.IALOAD);
        case OBJECT -> opcode + (Opcodes.AALOAD - Opcodes.IALOAD);
        default -> throw new AssertionError();
      };
    } else {
      return frameType().forOpcode(opcode);
    }
  }
}

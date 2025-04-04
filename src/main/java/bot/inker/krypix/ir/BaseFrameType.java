package bot.inker.krypix.ir;

import org.objectweb.asm.Opcodes;

public enum BaseFrameType {
  INT, LONG, FLOAT, DOUBLE, OBJECT;

  public int forOpcode(int opcode) {
    if (opcode == Opcodes.IALOAD || opcode == Opcodes.IASTORE) {
      throw new UnsupportedOperationException();
    }
    return switch (this) {
      case INT -> opcode;
      case LONG -> opcode + (Opcodes.LRETURN - Opcodes.IRETURN);
      case FLOAT -> opcode + (Opcodes.FRETURN - Opcodes.IRETURN);
      case DOUBLE -> opcode + (Opcodes.DRETURN - Opcodes.IRETURN);
      case OBJECT -> {
        if (opcode != Opcodes.ILOAD && opcode != Opcodes.ISTORE && opcode != Opcodes.IRETURN) {
          throw new UnsupportedOperationException();
        }
        yield opcode + (Opcodes.ARETURN - Opcodes.IRETURN);
      }
      default -> throw new AssertionError();
    };
  }
}

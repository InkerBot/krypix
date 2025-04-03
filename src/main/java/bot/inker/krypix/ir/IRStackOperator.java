package bot.inker.krypix.ir;

import com.google.common.base.Preconditions;
import org.objectweb.asm.Opcodes;

public final class IRStackOperator implements IRAbstract {
  private final Type type;

  public IRStackOperator(Type type) {
    this.type = type;
  }

  public static IRStackOperator fromOpcode(int opcode) {
    Preconditions.checkArgument(opcode >= Opcodes.POP && opcode <= Opcodes.SWAP, "Invalid opcode: %d", opcode);
    return new IRStackOperator(Type.values()[opcode - Opcodes.POP]);
  }

  public Type type() {
    return type;
  }

  public enum Type {
    POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP;

    private final int opcode = ordinal() + Opcodes.POP;

    public int opcode() {
      return opcode;
    }
  }
}

package bot.inker.krypix.ir.handle;

import bot.inker.krypix.ir.ref.FieldRef;
import bot.inker.krypix.ir.ref.MethodRef;
import org.objectweb.asm.Opcodes;

public final class IRHandle {
  private final Type type;
  private final Object target;
  private final boolean isInterface;

  private IRHandle(Type type, Object target, boolean isInterface) {
    this.type = type;
    this.target = target;
    this.isInterface = isInterface;
  }

  public static IRHandle ofGetField(FieldRef target) {
    return new IRHandle(Type.GETFIELD, target, false);
  }

  public static IRHandle ofGetStatic(FieldRef target) {
    return new IRHandle(Type.GETSTATIC, target, false);
  }

  public static IRHandle ofPutField(FieldRef target) {
    return new IRHandle(Type.PUTFIELD, target, false);
  }

  public static IRHandle ofPutStatic(FieldRef target) {
    return new IRHandle(Type.PUTSTATIC, target, false);
  }

  public static IRHandle ofInvokeVirtual(MethodRef target, boolean isInterface) {
    return new IRHandle(Type.INVOKEVIRTUAL, target, isInterface);
  }

  public static IRHandle ofInvokeStatic(MethodRef target) {
    return new IRHandle(Type.INVOKESTATIC, target, false);
  }

  public static IRHandle ofInvokeSpecial(MethodRef target) {
    return new IRHandle(Type.INVOKESPECIAL, target, false);
  }

  public static IRHandle ofNewInvokeSpecial(MethodRef target) {
    return new IRHandle(Type.NEWINVOKESPECIAL, target, false);
  }

  public static IRHandle ofInvokeInterface(MethodRef target) {
    return new IRHandle(Type.INVOKEINTERFACE, target, true);
  }

  public Type type() {
    return type;
  }

  public int opcode() {
    return type.opcode();
  }

  public boolean isField() {
    return type.isField();
  }

  public boolean isMethod() {
    return !type.isField();
  }

  public boolean isStatic() {
    return type.isStatic;
  }

  public boolean isInterface() {
    return isInterface;
  }

  public Object get() {
    return target;
  }

  public FieldRef field() {
    if (target instanceof FieldRef) {
      return (FieldRef) target;
    }
    throw new IllegalStateException("Target is not a field reference");
  }

  public MethodRef method() {
    if (target instanceof MethodRef) {
      return (MethodRef) target;
    }
    throw new IllegalStateException("Target is not a method reference");
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    sb.append(type.name());
    if (target instanceof FieldRef) {
      sb.append(" ").append(((FieldRef) target).owner()).append(" ").append(((FieldRef) target).name());
    } else if (target instanceof MethodRef) {
      sb.append(" ").append(((MethodRef) target).owner()).append(" ").append(((MethodRef) target).name());
    }
    return sb.append("}").toString();
  }

  public enum Type {
    GETFIELD(Opcodes.H_GETFIELD, true, false),
    GETSTATIC(Opcodes.H_GETSTATIC, true, true),
    PUTFIELD(Opcodes.H_PUTFIELD, true, false),
    PUTSTATIC(Opcodes.H_PUTSTATIC, true, true),
    INVOKEVIRTUAL(Opcodes.H_INVOKEVIRTUAL, false, false),
    INVOKESTATIC(Opcodes.H_INVOKESTATIC, false, true),
    INVOKESPECIAL(Opcodes.H_INVOKESPECIAL, false, false),
    NEWINVOKESPECIAL(Opcodes.H_NEWINVOKESPECIAL, false, false),
    INVOKEINTERFACE(Opcodes.H_INVOKEINTERFACE, true, true);

    private final int opcode;
    private final boolean isField;
    private final boolean isStatic;

    Type(int opcode, boolean isField, boolean isStatic) {
      this.opcode = opcode;
      this.isField = isField;
      this.isStatic = isStatic;
    }

    public static Type fromOpcode(int opcode) {
      return switch (opcode) {
        case Opcodes.H_GETFIELD -> GETFIELD;
        case Opcodes.H_GETSTATIC -> GETSTATIC;
        case Opcodes.H_PUTFIELD -> PUTFIELD;
        case Opcodes.H_PUTSTATIC -> PUTSTATIC;
        case Opcodes.H_INVOKEVIRTUAL -> INVOKEVIRTUAL;
        case Opcodes.H_INVOKESTATIC -> INVOKESTATIC;
        case Opcodes.H_INVOKESPECIAL -> INVOKESPECIAL;
        case Opcodes.H_NEWINVOKESPECIAL -> NEWINVOKESPECIAL;
        case Opcodes.H_INVOKEINTERFACE -> INVOKEINTERFACE;
        default -> throw new IllegalArgumentException("Unknown opcode: " + opcode);
      };
    }

    public int opcode() {
      return opcode;
    }

    public boolean isField() {
      return isField;
    }

    public boolean isStatic() {
      return isStatic;
    }
  }
}

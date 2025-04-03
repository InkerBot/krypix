package bot.inker.krypix.ir.ref;

import bot.inker.krypix.ir.BaseFrameType;
import bot.inker.krypix.ir.BaseValueType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class TypeRef {
  private final Object elementType;
  private final int dimensions;

  private TypeRef(Object elementType, int dimensions) {
    this.elementType = elementType;
    this.dimensions = dimensions;
  }

  public static TypeRef ofClass(ClassRef classRef) {
    return new TypeRef(classRef, 0);
  }

  public static TypeRef ofInteger() {
    return new TypeRef(Type.INT, 0);
  }

  public static TypeRef ofLong() {
    return new TypeRef(Type.LONG, 0);
  }

  public static TypeRef ofDouble() {
    return new TypeRef(Type.DOUBLE, 0);
  }

  public static TypeRef ofVoid() {
    return new TypeRef(Type.VOID, 0);
  }

  public static TypeRef ofBoolean() {
    return new TypeRef(Type.BOOLEAN, 0);
  }

  public static TypeRef ofChar() {
    return new TypeRef(Type.CHAR, 0);
  }

  public static TypeRef ofByte() {
    return new TypeRef(Type.BYTE, 0);
  }

  public static TypeRef ofShort() {
    return new TypeRef(Type.SHORT, 0);
  }

  public static TypeRef ofFloat() {
    return new TypeRef(Type.FLOAT, 0);
  }

  public static TypeRef ofArray(TypeRef elementType, int dimensions) {
    return new TypeRef(elementType.elementType, dimensions);
  }

  public static TypeRef fromAsmSort(int sort) {
    if (0 <= sort && sort <= 8) {
      return new TypeRef(sort, 0);
    } else {
      throw new IllegalArgumentException("Invalid ASM sort: " + sort);
    }
  }

  public static TypeRef fromAsmOpcode(int opcode) {
    return switch (opcode) {
      case Opcodes.T_BOOLEAN -> ofBoolean();
      case Opcodes.T_CHAR -> ofChar();
      case Opcodes.T_BYTE -> ofByte();
      case Opcodes.T_SHORT -> ofShort();
      case Opcodes.T_INT -> ofInteger();
      case Opcodes.T_FLOAT -> ofFloat();
      case Opcodes.T_LONG -> ofLong();
      case Opcodes.T_DOUBLE -> ofDouble();
      default -> throw new IllegalArgumentException("Invalid ASM opcode: " + opcode);
    };
  }

  public String desc() {
    if (elementType instanceof Integer) {
      return "[".repeat(dimensions) + switch ((Integer) elementType) {
        case Type.VOID -> "V";
        case Type.BOOLEAN -> "Z";
        case Type.CHAR -> "C";
        case Type.BYTE -> "B";
        case Type.SHORT -> "S";
        case Type.INT -> "I";
        case Type.FLOAT -> "F";
        case Type.LONG -> "J";
        case Type.DOUBLE -> "D";
        default -> throw new IllegalStateException("Unexpected value: " + elementType);
      };
    } else {
      return "[".repeat(dimensions) + "L" + ((ClassRef) elementType).name() + ";";
    }
  }

  public int sort() {
    if (dimensions != 0) {
      return Type.ARRAY;
    } else if (elementType instanceof Integer) {
      return (Integer) elementType;
    } else if (elementType instanceof ClassRef) {
      return Type.OBJECT;
    } else {
      throw new IllegalStateException("Unexpected elementType: " + elementType);
    }
  }

  public TypeRef elementType() {
    if (dimensions == 0) {
      return this;
    }

    return new TypeRef(elementType, 0);
  }

  public int dimensions() {
    return dimensions;
  }

  public int size() {
    return switch (sort()) {
      case Type.VOID -> 0;
      case Type.BOOLEAN, Type.CHAR, Type.BYTE, Type.SHORT, Type.INT,
           Type.FLOAT, Type.ARRAY, Type.OBJECT -> 1;
      case Type.LONG, Type.DOUBLE -> 2;
      default -> throw new IllegalStateException("Unknown type sort");
    };
  }

  public ClassRef elementClass() {
    if (elementType instanceof ClassRef) {
      return (ClassRef) elementType;
    } else {
      throw new IllegalStateException("Element type is not a class reference");
    }
  }

  public TypeRef withDimensions(int dimensions) {
    return new TypeRef(elementType, dimensions);
  }

  public BaseFrameType asFrameType() {
    return asValueType().frameType();
  }

  public BaseValueType asValueType() {
    return switch (sort()) {
      case Type.BOOLEAN -> BaseValueType.BOOLEAN;
      case Type.CHAR -> BaseValueType.CHAR;
      case Type.BYTE -> BaseValueType.BYTE;
      case Type.SHORT -> BaseValueType.SHORT;
      case Type.INT -> BaseValueType.INT;
      case Type.FLOAT -> BaseValueType.FLOAT;
      case Type.LONG -> BaseValueType.LONG;
      case Type.DOUBLE -> BaseValueType.DOUBLE;
      case Type.OBJECT, Type.ARRAY -> BaseValueType.OBJECT;
      default -> throw new IllegalStateException("Unknown type sort");
    };
  }

  @Override
  public String toString() {
    return desc();
  }
}

package bot.inker.krypix.ir.ref;

import org.objectweb.asm.Type;

public final class TypeRefPrimitive implements TypeRef {
  private final int elementType;
  private final int dimensions;

  public TypeRefPrimitive(int elementType, int dimensions) {
    this.elementType = elementType;
    this.dimensions = dimensions;

    if (elementType == Type.VOID && dimensions != 0) {
      throw new IllegalArgumentException("Void type cannot have dimensions");
    }
  }

  @Override
  public String desc() {
    return "[".repeat(dimensions) + switch (elementType) {
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
  }

  @Override
  public int sort() {
    if (dimensions == 0) {
      return elementType;
    } else {
      return Type.ARRAY;
    }
  }

  @Override
  public TypeRefPrimitive elementType() {
    if (dimensions == 0) {
      return this;
    }

    return new TypeRefPrimitive(elementType, 0);
  }

  public int dimensions() {
    return dimensions;
  }
}

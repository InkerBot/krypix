package bot.inker.krypix.ir.ref;

import bot.inker.krypix.KrypixClass;
import bot.inker.krypix.ir.BaseFrameType;
import bot.inker.krypix.ir.BaseValueType;
import org.objectweb.asm.Type;

public interface TypeRef {
  String desc();

  int sort();

  default boolean isArray() {
    return dimensions() != 0;
  }

  TypeRef elementType();

  default BaseValueType asValueType() {
    return switch (sort()) {
      case Type.BOOLEAN -> BaseValueType.BOOLEAN;
      case Type.CHAR -> BaseValueType.CHAR;
      case Type.BYTE -> BaseValueType.BYTE;
      case Type.SHORT -> BaseValueType.SHORT;
      case Type.INT -> BaseValueType.INT;
      case Type.LONG -> BaseValueType.LONG;
      case Type.DOUBLE -> BaseValueType.DOUBLE;
      case Type.FLOAT -> BaseValueType.FLOAT;
      case Type.ARRAY, Type.OBJECT -> BaseValueType.OBJECT;
      default -> throw new IllegalStateException("Unknown type sort");
    };
  }

  default BaseFrameType asFrameType() {
    return switch (sort()) {
      case Type.BOOLEAN, Type.CHAR, Type.BYTE, Type.SHORT, Type.INT -> BaseFrameType.INT;
      case Type.LONG -> BaseFrameType.LONG;
      case Type.DOUBLE -> BaseFrameType.DOUBLE;
      case Type.FLOAT -> BaseFrameType.FLOAT;
      case Type.ARRAY, Type.OBJECT -> BaseFrameType.OBJECT;
      default -> throw new IllegalStateException("Unknown type sort");
    };
  }

  default int dimensions() {
    int numDimensions = 1;
    while (desc().charAt(numDimensions) == '[') {
      numDimensions++;
    }
    return numDimensions;
  }

  default int size() {
    return switch (sort()) {
      case Type.VOID -> 0;
      case Type.BOOLEAN, Type.CHAR, Type.BYTE, Type.SHORT, Type.INT,
           Type.FLOAT, Type.ARRAY, Type.OBJECT -> 1;
      case Type.LONG, Type.DOUBLE -> 2;
      default -> throw new IllegalStateException("Unknown type sort");
    };
  }

  static TypeRefResolved ofClass(KrypixClass elementType) {
    return new TypeRefResolved(elementType, 0);
  }

  static TypeRefPrimitive ofInt() {
    return new TypeRefPrimitive(Type.INT, 0);
  }

  static TypeRefPrimitive ofLong() {
    return new TypeRefPrimitive(Type.LONG, 0);
  }

  static TypeRefPrimitive ofFloat() {
    return new TypeRefPrimitive(Type.FLOAT, 0);
  }

  static TypeRefPrimitive ofDouble() {
    return new TypeRefPrimitive(Type.DOUBLE, 0);
  }

  static TypeRefPrimitive ofVoid() {
    return new TypeRefPrimitive(Type.VOID, 0);
  }

  static TypeRefPrimitive ofBoolean() {
    return new TypeRefPrimitive(Type.BOOLEAN, 0);
  }

  static TypeRefPrimitive ofByte() {
    return new TypeRefPrimitive(Type.BYTE, 0);
  }

  static TypeRefPrimitive ofChar() {
    return new TypeRefPrimitive(Type.CHAR, 0);
  }

  static TypeRefPrimitive ofShort() {
    return new TypeRefPrimitive(Type.SHORT, 0);
  }

  static TypeRef ofArray(TypeRef elementType, int dimensions) {
    if (dimensions == 0) {
      return elementType;
    }

    if (elementType instanceof TypeRefPrimitive primitive) {
      return new TypeRefPrimitive(primitive.sort(), dimensions);
    } else if (elementType instanceof TypeRefResolved resolved) {
      return new TypeRefResolved(resolved.krypixClass(), dimensions);
    } else {
      return new TypeRefUnknown("[".repeat(dimensions) + elementType.elementType().desc());
    }
  }

  static TypeRef ofUnknown(String desc) {
    return new TypeRefUnknown(desc);
  }
}

package bot.inker.krypix.ir.ref;

import org.objectweb.asm.Type;

public interface TypeRef {
  String desc();

  int sort();

  default boolean isArray() {
    return dimensions() != 0;
  }

  TypeRef elementType();

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
}

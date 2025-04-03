package bot.inker.krypix.ir.ref;

import bot.inker.krypix.KrypixClass;
import org.objectweb.asm.Type;

public final class TypeRefResolved implements TypeRef {
  private final KrypixClass elementType;
  private final int dimensions;

  public TypeRefResolved(KrypixClass elementType, int dimensions) {
    this.elementType = elementType;
    this.dimensions = dimensions;
  }

  public KrypixClass krypixClass() {
    return elementType;
  }

  @Override
  public String desc() {
    return "[".repeat(dimensions) + "L" + elementType + ";";
  }

  @Override
  public int sort() {
    return (dimensions == 0)
      ? Type.OBJECT
      : Type.ARRAY;
  }

  @Override
  public TypeRefResolved elementType() {
    return new TypeRefResolved(elementType, 0);
  }
}

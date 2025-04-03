package bot.inker.krypix.ir.ref;

import org.objectweb.asm.Type;

public final class TypeRefUnknown implements TypeRef {
  private final String desc;

  public TypeRefUnknown(String desc) {
    this.desc = desc;
  }

  @Override
  public String desc() {
    return desc;
  }

  @Override
  public int sort() {
    return (desc.charAt(0) == '[') ? Type.ARRAY : Type.OBJECT;
  }

  @Override
  public TypeRef elementType() {
    if (desc.charAt(0) != '[') {
      return this;
    }

    int dimensions = 0;
    while (desc.charAt(dimensions) == '[') {
      dimensions++;
    }

    return new TypeRefUnknown(desc.substring(dimensions));
  }
}

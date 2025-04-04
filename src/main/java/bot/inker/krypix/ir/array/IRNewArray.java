package bot.inker.krypix.ir.array;

import bot.inker.krypix.ir.IRAbstract;
import bot.inker.krypix.ir.ref.TypeRef;

public final class IRNewArray implements IRAbstract {
  private final TypeRef type;
  private final int dimension;

  public IRNewArray(TypeRef type, int dimension) {
    this.type = type;
    this.dimension = dimension;
  }

  public TypeRef type() {
    return type;
  }

  public int dimension() {
    return dimension;
  }

  @Override
  public String toString() {
    String sb = "newarray " +
      "[".repeat(Math.max(0, dimension)) +
      type;
    return sb;
  }
}

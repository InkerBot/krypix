package bot.inker.krypix.ir.array;

import bot.inker.krypix.ir.IRAbstract;
import bot.inker.krypix.ir.ref.TypeRef;

public class IRNewArray implements IRAbstract {
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
    StringBuilder sb = new StringBuilder();
    sb.append("newarray ");
    for (int i = 0; i < dimension; i++) {
      sb.append("[");
    }
    sb.append(type);
    return sb.toString();
  }
}

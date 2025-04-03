package bot.inker.krypix.ir.handle;

import bot.inker.krypix.ir.IRConst;
import bot.inker.krypix.ir.ref.TypeRef;

public final class IRConstantDynamic {
  private final String name;
  private final TypeRef desc;
  private final IRHandle bsm;
  private final IRConst[] bsmArgs;

  public IRConstantDynamic(String name, TypeRef desc, IRHandle bsm, IRConst[] bsmArgs) {
    this.name = name;
    this.desc = desc;
    this.bsm = bsm;
    this.bsmArgs = bsmArgs;
  }

  public String name() {
    return name;
  }

  public TypeRef desc() {
    return desc;
  }

  public IRHandle bsm() {
    return bsm;
  }

  public IRConst[] bsmArgs() {
    return bsmArgs;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    sb.append("ConstantDynamic ").append(name).append(" ").append(desc);
    if (bsm != null) {
      sb.append(" bsm=").append(bsm);
    }
    if (bsmArgs != null && bsmArgs.length > 0) {
      sb.append(" bsmArgs=[");
      for (int i = 0; i < bsmArgs.length; i++) {
        sb.append(bsmArgs[i]);
        if (i < bsmArgs.length - 1) {
          sb.append(", ");
        }
      }
      sb.append("]");
    }
    return sb.append("}").toString();
  }
}

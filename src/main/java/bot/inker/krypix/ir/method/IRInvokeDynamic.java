package bot.inker.krypix.ir.method;

import bot.inker.krypix.ir.IRConst;
import bot.inker.krypix.ir.handle.IRHandle;
import bot.inker.krypix.ir.ref.MethodType;

public final class IRInvokeDynamic implements IRInvoke {
  public final IRHandle bsm;
  public final IRConst[] bsmArgs;
  private final String name;
  private final MethodType desc;

  public IRInvokeDynamic(String name, MethodType methodType, IRHandle bsm, IRConst[] bsmArgs) {
    this.name = name;
    this.desc = methodType;
    this.bsm = bsm;
    this.bsmArgs = bsmArgs;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public MethodType desc() {
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
    StringBuilder sb = new StringBuilder();
    sb.append("invokedynamic ").append(name).append(" ").append(desc);
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
    return sb.toString();
  }
}

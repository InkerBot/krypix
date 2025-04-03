package bot.inker.krypix.ir.method;

import bot.inker.krypix.ir.IRConst;
import bot.inker.krypix.ir.ref.MethodType;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;

public final class IRInvokeDynamic implements IRInvoke {
  private final String name;
  private final MethodType desc;
  public final Handle bsm;
  public final IRConst[] bsmArgs;

  public IRInvokeDynamic(String name, MethodType methodType, Handle bsm, IRConst[] bsmArgs) {
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

  public Handle bsm() {
    return bsm;
  }

  public IRConst[] bsmArgs() {
    return bsmArgs;
  }
}

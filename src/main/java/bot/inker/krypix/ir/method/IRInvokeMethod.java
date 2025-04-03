package bot.inker.krypix.ir.method;

import bot.inker.krypix.ir.ref.ClassRef;
import bot.inker.krypix.ir.ref.MethodRef;
import bot.inker.krypix.ir.ref.MethodType;

public final class IRInvokeMethod implements IRInvoke {
  private final Type type;
  private final MethodRef method;
  private final boolean isInterface;

  public IRInvokeMethod(Type type, MethodRef method, boolean isInterface) {
    this.type = type;
    this.method = method;
    this.isInterface = isInterface;
  }

  public Type type() {
    return type;
  }

  public MethodRef method() {
    return method;
  }

  public ClassRef owner() {
    return method.owner();
  }

  @Override
  public String name() {
    return method.name();
  }

  @Override
  public MethodType desc() {
    return method.desc();
  }

  public boolean isInterface() {
    return isInterface;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("invoke").append(type.name().toLowerCase()).append(" ");
    sb.append(owner().name()).append(" ");
    sb.append(method.name()).append(" ").append(method.desc());
    if (isInterface) {
      sb.append(" interface");
    }
    return sb.toString();
  }

  public enum Type {
    VIRTUAL,
    SPECIAL,
    STATIC,
    INTERFACE
  }
}

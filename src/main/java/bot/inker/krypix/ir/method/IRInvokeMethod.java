package bot.inker.krypix.ir.method;

import bot.inker.krypix.ir.IRAbstract;
import bot.inker.krypix.ir.ref.MethodType;

public interface IRInvokeMethod extends IRInvoke {
  Type type();

  String owner();

  enum Type {
    VIRTUAL,
    SPECIAL,
    STATIC,
    INTERFACE
  }
}

package bot.inker.krypix.ir.method;

import bot.inker.krypix.ir.IRAbstract;

public interface IRInvoke extends IRAbstract {
  Type type();

  String owner();

  String name();

  String desc();

  enum Type {
    VIRTUAL,
    SPECIAL,
    STATIC,
    INTERFACE
  }
}

package bot.inker.krypix.ir.field;

import bot.inker.krypix.ir.IRAbstract;

public interface IRField extends IRAbstract {
  boolean isStatic();
  String owner();
  String name();
  String desc();
}

package bot.inker.krypix.ir.field;

import bot.inker.krypix.ir.IRAbstract;
import bot.inker.krypix.ir.ref.ClassRef;

public interface IRField extends IRAbstract {
  boolean isStatic();

  ClassRef owner();

  String name();

  String desc();
}

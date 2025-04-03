package bot.inker.krypix.ir.field;

import bot.inker.krypix.ir.IRAbstract;
import bot.inker.krypix.ir.ref.ClassRef;
import bot.inker.krypix.ir.ref.FieldRef;
import bot.inker.krypix.ir.ref.TypeRef;

public interface IRField extends IRAbstract {
  boolean isStatic();

  FieldRef field();

  ClassRef owner();

  String name();

  TypeRef desc();
}

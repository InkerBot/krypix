package bot.inker.krypix.ir.ref;

public interface FieldRef {
  ClassRef owner();

  String name();

  TypeRef desc();

  boolean isStatic();
}

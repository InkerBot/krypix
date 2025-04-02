package bot.inker.krypix.ir.ref;

public interface MethodRef {
  ClassRef owner();
  String name();
  MethodType desc();
}

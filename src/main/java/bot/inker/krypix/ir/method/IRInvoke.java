package bot.inker.krypix.ir.method;

import bot.inker.krypix.ir.IRAbstract;
import bot.inker.krypix.ir.ref.MethodType;

public interface IRInvoke extends IRAbstract {
  String name();

  MethodType desc();
}

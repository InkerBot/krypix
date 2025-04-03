package bot.inker.krypix.ir.local;

import bot.inker.krypix.ir.BaseFrameType;
import bot.inker.krypix.ir.IRAbstract;

public interface IRLocal extends IRAbstract {
  BaseFrameType type();

  int index();
}

package bot.inker.krypix.ir.local;

import bot.inker.krypix.ir.BaseFrameType;

public final class IRLocalStore implements IRLocal {
  private final BaseFrameType type;
  private final int index;

  public IRLocalStore(BaseFrameType type, int index) {
    this.type = type;
    this.index = index;
  }

  @Override
  public BaseFrameType type() {
    return type;
  }

  @Override
  public int index() {
    return index;
  }
}

package bot.inker.krypix.ir.branch;

import bot.inker.krypix.ir.BaseFrameType;
import org.jetbrains.annotations.Nullable;

public final class IRReturn implements IRTerminatal {
  private final @Nullable BaseFrameType type;

  public IRReturn(@Nullable BaseFrameType type) {
    this.type = type;
  }

  public @Nullable BaseFrameType type() {
    return type;
  }

  @Override
  public String toString() {
    return "return" + (type == null ? "" : " " + type.name().toLowerCase());
  }
}

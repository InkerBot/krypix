package bot.inker.krypix.ir;

import bot.inker.krypix.ir.ref.ClassRef;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

public final class ExceptionHandler {
  private final @Nullable ClassRef catchType;
  private final CodeBlock handler;

  public ExceptionHandler(@Nullable ClassRef catchType, CodeBlock handler) {
    Preconditions.checkArgument(handler != null, "Handler cannot be null");
    this.catchType = catchType;
    this.handler = handler;
  }

  public @Nullable ClassRef catchType() {
    return catchType;
  }

  public CodeBlock handler() {
    return handler;
  }
}

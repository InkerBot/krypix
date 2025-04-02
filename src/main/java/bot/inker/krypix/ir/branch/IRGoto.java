package bot.inker.krypix.ir.branch;

import bot.inker.krypix.ir.CodeBlock;

public final class IRGoto implements IRBranch {
  private final CodeBlock target;

  public IRGoto(CodeBlock target) {
    this.target = target;
  }

  public CodeBlock target() {
    return target;
  }
}

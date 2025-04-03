package bot.inker.krypix.ir.branch;

import bot.inker.krypix.ir.CodeBlock;

import java.util.function.Function;

public final class IRGoto implements IRBranch {
  private final CodeBlock target;

  public IRGoto(CodeBlock target) {
    this.target = target;
  }

  public CodeBlock target() {
    return target;
  }

  @Override
  public String toString(Function<CodeBlock, String> codeBlockNameProvider) {
    return "goto " + codeBlockNameProvider.apply(target);
  }

  @Override
  public String toString() {
    return toString(CodeBlock::defaultName);
  }
}

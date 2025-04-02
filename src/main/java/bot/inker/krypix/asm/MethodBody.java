package bot.inker.krypix.asm;

import bot.inker.krypix.ir.CodeBlock;

import java.util.ArrayList;
import java.util.List;

public final class MethodBody {
  private final List<CodeBlock> codeBlocks = new ArrayList<>();
  private CodeBlock entryBlock;

  public List<CodeBlock> codeBlocks() {
    return codeBlocks;
  }

  public CodeBlock entryBlock() {
    return entryBlock;
  }

  public void entryBlock(CodeBlock entryBlock) {
    this.entryBlock = entryBlock;
  }
}

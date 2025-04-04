package bot.inker.krypix.ir;

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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (CodeBlock block : codeBlocks) {
      sb.append(block).append("\n");
    }
    return sb.toString();
  }
}

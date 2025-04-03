package bot.inker.krypix.ir.branch;

import bot.inker.krypix.ir.CodeBlock;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.function.Function;

public final class IRBranchSwitch implements IRBranch {
  private final Int2ObjectMap<CodeBlock> branches;
  private final CodeBlock defaultBranch;

  public IRBranchSwitch(Int2ObjectMap<CodeBlock> branches, CodeBlock defaultBranch) {
    this.branches = branches;
    this.defaultBranch = defaultBranch;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Int2ObjectMap<CodeBlock> branches() {
    return branches;
  }

  public CodeBlock defaultBranch() {
    return defaultBranch;
  }

  @Override
  public String toString(Function<CodeBlock, String> codeBlockNameProvider) {
    StringBuilder sb = new StringBuilder("switch ");
    for (Int2ObjectMap.Entry<CodeBlock> entry : branches.int2ObjectEntrySet()) {
      sb.append(entry.getIntKey()).append(" -> ").append(codeBlockNameProvider.apply(entry.getValue())).append(", ");
    }
    if (defaultBranch != null) {
      sb.append("default -> ").append(codeBlockNameProvider.apply(defaultBranch));
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return toString(CodeBlock::defaultName);
  }

  public static class Builder {
    private final Int2ObjectAVLTreeMap<CodeBlock> branches = new Int2ObjectAVLTreeMap<>();
    private CodeBlock defaultBranch;

    public Builder addBranch(int value, CodeBlock target) {
      branches.put(value, target);
      return this;
    }

    public Builder defaultBranch(CodeBlock defaultBranch) {
      this.defaultBranch = defaultBranch;
      return this;
    }

    public IRBranchSwitch build() {
      return new IRBranchSwitch(branches.clone(), defaultBranch);
    }
  }
}

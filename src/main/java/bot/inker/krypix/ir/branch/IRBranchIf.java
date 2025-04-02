package bot.inker.krypix.ir.branch;

import bot.inker.krypix.ir.CodeBlock;

public final class IRBranchIf implements IRBranch {
  private final Operator operator;
  private final CodeBlock target;
  private final CodeBlock alternative;

  public IRBranchIf(Operator operator, CodeBlock target, CodeBlock alternative) {
    this.operator = operator;
    this.target = target;
    this.alternative = alternative;
  }

  public Operator operator() {
    return operator;
  }

  public CodeBlock target() {
    return target;
  }

  public CodeBlock alternative() {
    return alternative;
  }

  public enum Operator {
    EQ, NE, LT, GE, GT, LE,
    ICMP_EQ, ICMP_NE, ICMP_LT, ICMP_GE, ICMP_GT, ICMP_LE,
    ACMP_EQ, ACMP_NE,
    NULL, NONNULL
  }
}

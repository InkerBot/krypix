package bot.inker.krypix.ir.document;

import bot.inker.krypix.ir.CodeBlock;

public final class IRDocumentLineNumber implements IRDocument {
  private final CodeBlock startBlock;
  private final int lineNumber;

  public IRDocumentLineNumber(CodeBlock startBlock, int lineNumber) {
    this.startBlock = startBlock;
    this.lineNumber = lineNumber;
  }

  public CodeBlock startBlock() {
    return startBlock;
  }

  public int lineNumber() {
    return lineNumber;
  }

  @Override
  public String toString() {
    return "@line " + lineNumber;
  }
}

package bot.inker.krypix.ir.document;

import java.util.stream.Collectors;

public final class IRDocumentComment implements IRDocument {
  private final String message;

  public IRDocumentComment(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return message.lines()
      .map(it -> "// " + it)
      .collect(Collectors.joining("\n"));
  }
}

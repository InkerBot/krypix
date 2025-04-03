package bot.inker.krypix.ir;

import java.util.function.Function;

public interface IRAbstract {
  default String toString(Function<CodeBlock, String> codeBlockNameProvider) {
    return toString();
  }
}

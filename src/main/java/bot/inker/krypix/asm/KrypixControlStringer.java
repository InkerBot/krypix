package bot.inker.krypix.asm;

import bot.inker.krypix.AppView;
import bot.inker.krypix.KrypixMethod;
import bot.inker.krypix.directory.DictionaryMaker;
import bot.inker.krypix.directory.NameFactory;
import bot.inker.krypix.ir.CodeBlock;
import bot.inker.krypix.ir.IRAbstract;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class KrypixControlStringer {
  private final AppView appView;
  private final KrypixMethod method;
  private final Map<CodeBlock, String> blockNames = new HashMap<>();
  private final Function<CodeBlock, String> blockNameFunction = blockNames::get;

  public KrypixControlStringer(AppView appView, KrypixMethod method) {
    this.appView = appView;
    this.method = method;
  }

  private void initBlockNames() {
    NameFactory nameFactory = DictionaryMaker.createAlphabet("abcdefghijklmnopqrstuvwxyz");
    for (CodeBlock block : method.body().codeBlocks()) {
      blockNames.put(block, "label_" + nameFactory.nextName());
    }
  }

  private String irToString(IRAbstract ir) {
    if (ir == null) {
      return "!!! null !!!";
    }
    return ir.toString(blockNameFunction);
  }

  public String stringer() {
    initBlockNames();

    StringBuilder sb = new StringBuilder();
    sb.append("owner: ").append(method.owner()).append("\n");
    sb.append("method: ").append(method).append("\n");
    sb.append("------\n\n");

    for (CodeBlock codeBlock : method.body().codeBlocks()) {
      sb.append(":").append(blockNameFunction.apply(codeBlock)).append("\n");
      for (IRAbstract instruction : codeBlock.instructions()) {
        sb.append(irToString(instruction)).append("\n");
      }
      sb.append(irToString(codeBlock.terminatal())).append("\n\n");
    }
    return sb.toString();
  }
}

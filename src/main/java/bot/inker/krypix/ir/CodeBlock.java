package bot.inker.krypix.ir;

import bot.inker.krypix.common.attachment.AttachmentContainer;
import bot.inker.krypix.common.attachment.WithAttachment;
import bot.inker.krypix.ir.terminatal.IRTerminatal;

import java.util.ArrayList;
import java.util.List;

public final class CodeBlock implements WithAttachment.Contained {
  private final AttachmentContainer container = new AttachmentContainer();
  private final List<IRAbstract> instructions = new ArrayList<>();
  private IRTerminatal terminatal;

  @Override
  public AttachmentContainer container() {
    return container;
  }

  public List<IRAbstract> instructions() {
    return instructions;
  }

  public void addCode(IRAbstract instruction) {
    instructions.add(instruction);
  }

  public IRTerminatal terminatal() {
    return terminatal;
  }

  public void terminatal(IRTerminatal terminatal) {
    this.terminatal = terminatal;
  }
}

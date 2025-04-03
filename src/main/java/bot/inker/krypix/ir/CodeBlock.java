package bot.inker.krypix.ir;

import bot.inker.krypix.common.attachment.AttachmentContainer;
import bot.inker.krypix.common.attachment.WithAttachment;
import bot.inker.krypix.ir.branch.IRTerminatal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class CodeBlock implements WithAttachment.Contained {
  private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

  private final long id = ID_GENERATOR.getAndIncrement();
  private final AttachmentContainer container = new AttachmentContainer();
  private final List<IRAbstract> instructions = new ArrayList<>();
  private final List<ExceptionHandler> exceptionHandlers = new ArrayList<>();
  private IRTerminatal terminatal;

  @Override
  public AttachmentContainer container() {
    return container;
  }

  public List<IRAbstract> instructions() {
    return instructions;
  }

  public List<ExceptionHandler> exceptionHandlers() {
    return exceptionHandlers;
  }

  public IRTerminatal terminatal() {
    return terminatal;
  }

  public void addCode(IRAbstract instruction) {
    instructions.add(instruction);
  }

  public void addExceptionHandler(ExceptionHandler handler) {
    exceptionHandlers.add(handler);
  }

  public void terminatal(IRTerminatal terminatal) {
    this.terminatal = terminatal;
  }

  public String defaultName() {
    return Long.toString(id, 36);
  }

  @Override
  public String toString() {
    return "block " + defaultName();
  }
}

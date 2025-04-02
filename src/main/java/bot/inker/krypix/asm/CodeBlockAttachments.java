package bot.inker.krypix.asm;

import bot.inker.krypix.common.attachment.AttachmentKey;

public final class CodeBlockAttachments {
  public static final AttachmentKey<Integer> LINE_NUMBER = AttachmentKey.create("line-number");

  private CodeBlockAttachments() {
    throw new UnsupportedOperationException();
  }
}

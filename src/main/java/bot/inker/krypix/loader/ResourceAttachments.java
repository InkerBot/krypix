package bot.inker.krypix.loader;

import bot.inker.krypix.common.attachment.AttachmentKey;

public final class ResourceAttachments {
  public static final AttachmentKey<String> ARCHIVE_COMMENT = AttachmentKey.create("archive-comment");

  private ResourceAttachments() {
    throw new UnsupportedOperationException();
  }
}

package bot.inker.krypix.loader;

import bot.inker.krypix.common.attachment.AttachmentKey;

import java.nio.file.attribute.FileTime;

public final class ResourceAttachments {
  public static final AttachmentKey<Long> ENTRY_TIME = AttachmentKey.create("entry-time");
  public static final AttachmentKey<FileTime> ENTRY_CREATION_TIME = AttachmentKey.create("entry-creation-time");
  public static final AttachmentKey<FileTime> ENTRY_LAST_ACCESS_TIME = AttachmentKey.create("entry-last-access-time");
  public static final AttachmentKey<FileTime> ENTRY_LAST_MODIFIED_TIME = AttachmentKey.create("entry-last-modified-time");
  public static final AttachmentKey<Integer> ENTRY_METHOD = AttachmentKey.create("entry-method");
  public static final AttachmentKey<String> ENTRY_COMMENT = AttachmentKey.create("entry-comment");
  public static final AttachmentKey<byte[]> ENTRY_EXTRA = AttachmentKey.create("entry-extra");
  public static final AttachmentKey<String> ARCHIVE_COMMENT = AttachmentKey.create("archive-comment");

  private ResourceAttachments() {
    throw new UnsupportedOperationException();
  }
}

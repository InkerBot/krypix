package bot.inker.krypix.common.attachment;

import java.util.concurrent.atomic.AtomicLong;

public final class AttachmentKey<K> {
  private static final AtomicLong ID_GENERATOR = new AtomicLong();
  private final long id;
  private final String comment;

  private AttachmentKey(long id, String comment) {
    this.id = id;
    this.comment = comment;
  }

  public static <K> AttachmentKey<K> create() {
    return new AttachmentKey<>(ID_GENERATOR.getAndIncrement(), "");
  }

  public static <K> AttachmentKey<K> create(String comment) {
    return new AttachmentKey<>(ID_GENERATOR.getAndIncrement(), comment);
  }

  public AttachmentKey<K> withComment(String comment) {
    return new AttachmentKey<>(id, comment);
  }

  @Override
  public int hashCode() {
    return Long.hashCode(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    AttachmentKey<?> that = (AttachmentKey<?>) obj;
    return id == that.id;
  }

  @Override
  public String toString() {
    if (comment.isEmpty()) {
      return "(" + id + ")";
    } else {
      return "(" + id + ", " + comment + ")";
    }
  }
}

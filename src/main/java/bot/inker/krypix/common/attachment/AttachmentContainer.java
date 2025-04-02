package bot.inker.krypix.common.attachment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class AttachmentContainer implements WithAttachment {
  private final Map<AttachmentKey<?>, Object> attachments = new HashMap<>();

  @Override
  public <T> Optional<T> getAttachment(AttachmentKey<T> key) {
    return Optional.ofNullable((T) attachments.get(key));
  }

  @Override
  public <T> void setAttachment(AttachmentKey<T> key, T value) {
    attachments.put(key, value);
  }

  @Override
  public <T> Optional<T> removeAttachment(AttachmentKey<T> key) {
    return Optional.ofNullable((T) attachments.remove(key));
  }
}

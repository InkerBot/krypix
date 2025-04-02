package bot.inker.krypix.common.attachment;

import java.util.Optional;
import java.util.function.Supplier;

public interface WithAttachment {
  <T> Optional<T> getAttachment(AttachmentKey<T> key);

  <T> void setAttachment(AttachmentKey<T> key, T value);

  default <T> void setAttachment(AttachmentValue<T> value) {
    setAttachment(value.key(), value.value());
  }

  <T> Optional<T> removeAttachment(AttachmentKey<T> key);

  default <T> T computeAttachmentIfAbsent(AttachmentKey<T> key, Supplier<T> valueSupplier) {
    return getAttachment(key).orElseGet(() -> {
      T value = valueSupplier.get();
      setAttachment(key, value);
      return value;
    });
  }

  interface Contained extends WithAttachment {
    WithAttachment container();

    @Override
    default <T> Optional<T> getAttachment(AttachmentKey<T> key) {
      return container().getAttachment(key);
    }

    @Override
    default <T> void setAttachment(AttachmentKey<T> key, T value) {
      container().setAttachment(key, value);
    }

    @Override
    default <T> Optional<T> removeAttachment(AttachmentKey<T> key) {
      return container().removeAttachment(key);
    }

    @Override
    default <T> T computeAttachmentIfAbsent(AttachmentKey<T> key, Supplier<T> valueSupplier) {
      return container().computeAttachmentIfAbsent(key, valueSupplier);
    }
  }
}

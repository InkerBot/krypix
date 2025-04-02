package bot.inker.krypix.common.attachment;

public final class AttachmentValue<T> {
  private final AttachmentKey<T> key;
  private final T value;

  private AttachmentValue(AttachmentKey<T> key, T value) {
    this.key = key;
    this.value = value;
  }

  public static <T> AttachmentValue<T> of(AttachmentKey<T> key, T value) {
    return new AttachmentValue<>(key, value);
  }

  public AttachmentKey<T> key() {
    return key;
  }

  public T value() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    AttachmentValue<?> that = (AttachmentValue<?>) obj;
    return key.equals(that.key) && value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return key.hashCode() * 31 + value.hashCode();
  }

  @Override
  public String toString() {
    return key + " -> " + value;
  }
}

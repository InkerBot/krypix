package bot.inker.krypix.common;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class LazyBox<T> {
  private final AtomicReference<T> value = new AtomicReference<>();
  private final Supplier<T> supplier;
  private final BiFunction<T, T, T> onSet;

  public LazyBox(Supplier<T> supplier, BiFunction<T, T, T> onSet) {
    this.supplier = supplier;
    this.onSet = onSet;
  }

  public LazyBox(Supplier<T> supplier, Runnable onSet) {
    this(supplier, (oldValue, newValue) -> {
      onSet.run();
      return newValue;
    });
  }


  public LazyBox(Supplier<T> supplier) {
    this(supplier, (oldValue, newValue) -> newValue);
  }

  public T get() {
    T value = this.value.get();
    if (value == null) {
      synchronized (this) {
        value = this.value.get();
        if (value == null) {
          value = supplier.get();
          this.value.set(value);
        }
      }
    }
    return value;
  }

  public void set(T value) {
    synchronized (this) {
      this.value.updateAndGet(oldValue -> oldValue == null ? value : onSet.apply(oldValue, value));
    }
  }
}

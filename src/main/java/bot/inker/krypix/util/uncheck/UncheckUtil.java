package bot.inker.krypix.util.uncheck;

import java.util.function.Function;
import java.util.function.Supplier;

public final class UncheckUtil {
  private UncheckUtil() {
    throw new UnsupportedOperationException();
  }

  private static <T extends Throwable, R extends RuntimeException> R uncheckImpl(Throwable throwable) throws T {
    throw (T) throwable;
  }

  public static <R extends RuntimeException> R uncheck(Throwable e) {
    return uncheckImpl(e);
  }

  public static Runnable uncheckRunnable(UncheckRunnable runnable) {
    return () -> {
      try {
        runnable.run();
      } catch (Throwable throwable) {
        throw uncheckImpl(throwable);
      }
    };
  }

  public static <T> Supplier<T> uncheckSupplier(UncheckSupplier<T> supplier) {
    return () -> {
      try {
        return supplier.get();
      } catch (Throwable throwable) {
        throw uncheckImpl(throwable);
      }
    };
  }

  public static <T, R> Function<T, R> uncheckFunction(UncheckFunction<T, R> function) {
    return t -> {
      try {
        return function.apply(t);
      } catch (Throwable throwable) {
        throw uncheckImpl(throwable);
      }
    };
  }

  @FunctionalInterface
  public interface UncheckRunnable {
    void run() throws Throwable;
  }

  @FunctionalInterface
  public interface UncheckSupplier<T> {
    T get() throws Throwable;
  }

  @FunctionalInterface
  public interface UncheckFunction<T, R> {
    R apply(T t) throws Throwable;
  }
}

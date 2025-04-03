package bot.inker.krypix.util;

import org.slf4j.Logger;

import java.util.function.Supplier;

public final class StopWatchUtil {
  private StopWatchUtil() {
    throw new UnsupportedOperationException();
  }

  public static <R> R debugStopWatch(Logger logger, String message, Supplier<R> supplier, Object... args) {
    if (logger.isDebugEnabled()) {
      logger.debug("Start " + message, args);
      var start = System.currentTimeMillis();
      var result = supplier.get();
      var passed = System.currentTimeMillis() - start;

      logger.debug("Run " + message + " success [" + passed + " ms]", args);
      return result;
    } else {
      return supplier.get();
    }
  }

  public static void debugStopWatch(Logger logger, String message, Runnable runnable, Object... args) {
    debugStopWatch(logger, message, () -> {
      runnable.run();
      return null;
    }, args);
  }

  public static <R> R infoStopWatch(Logger logger, String message, Supplier<R> supplier, Object... args) {
    if (logger.isInfoEnabled()) {
      logger.info("Start " + message, args);
      var start = System.currentTimeMillis();
      var result = supplier.get();
      var passed = System.currentTimeMillis() - start;

      logger.info("Run " + message + " success [" + passed + " ms]", args);
      return result;
    } else {
      return supplier.get();
    }
  }

  public static void infoStopWatch(Logger logger, String message, Runnable runnable, Object... args) {
    infoStopWatch(logger, message, () -> {
      runnable.run();
      return null;
    }, args);
  }
}

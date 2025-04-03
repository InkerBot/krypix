package bot.inker.krypix.common.directory;

public interface NameFactory {
  String nextName();

  default void reset() {
    //
  }
}

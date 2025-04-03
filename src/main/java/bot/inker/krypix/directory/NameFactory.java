package bot.inker.krypix.directory;

public interface NameFactory {
  String nextName();

  default void reset() {
    //
  }
}

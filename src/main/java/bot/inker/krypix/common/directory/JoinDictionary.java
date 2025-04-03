package bot.inker.krypix.common.directory;

public abstract class JoinDictionary implements NameFactory {
  protected int[] indexes = new int[1];

  protected void increment() {
    for (int i = indexes.length - 1; i >= 0; i--) {
      indexes[i]++;
      if (indexes[i] < elementCount()) {
        return;
      }
      indexes[i] = 0;
    }

    indexes = new int[indexes.length + 1];
  }

  protected abstract String split();

  protected abstract int elementCount();

  protected abstract String getElement(int index);

  @Override
  public void reset() {
    indexes = new int[1];
  }

  @Override
  public String nextName() {
    StringBuilder builder = new StringBuilder();
    boolean isFirst = true;
    for (int index : indexes) {
      if (isFirst) {
        isFirst = false;
      } else {
        builder.append(split());
      }

      builder.append(getElement(index));
    }
    increment();
    return builder.toString();
  }

}

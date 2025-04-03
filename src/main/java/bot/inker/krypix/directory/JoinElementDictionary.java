package bot.inker.krypix.directory;

public final class JoinElementDictionary extends JoinDictionary {
  private final String split;
  private final String[] elements;

  public JoinElementDictionary(String split, String... elements) {
    this.split = split;
    this.elements = elements;
  }

  @Override
  protected int elementCount() {
    return elements.length;
  }

  @Override
  protected String getElement(int index) {
    return elements[index];
  }

  @Override
  protected String split() {
    return split;
  }
}

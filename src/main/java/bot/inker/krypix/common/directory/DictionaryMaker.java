package bot.inker.krypix.common.directory;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class DictionaryMaker {
  private DictionaryMaker() {
    throw new UnsupportedOperationException();
  }

  public static NameFactory createAlphabet(String alphabet) {
    String[] elements = alphabet.codePoints()
      .mapToObj(it -> new String(new int[]{it}, 0, 1))
      .toArray(String[]::new);

    return createElements(elements);
  }

  public static NameFactory createAlphabet(int... alphabet) {
    String[] elements = IntStream.of(alphabet)
      .mapToObj(it -> new String(new int[]{it}, 0, 1))
      .toArray(String[]::new);

    return createElements(elements);
  }

  public static NameFactory createAlphabet(char... alphabet) {
    String[] elements = new String[alphabet.length];
    for (int i = 0; i < alphabet.length; i++) {
      elements[i] = String.valueOf(alphabet[i]);
    }
    return createElements(elements);
  }

  public static NameFactory createElements(String... elements) {
    return new JoinElementDictionary("", elements);
  }

  public static NameFactory createElementsWithSplit(String split, String... elements) {
    return new JoinElementDictionary(split, elements);
  }

  public static NameFactory createEnglishAlphabet() {
    return createAlphabet("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
  }

  public static NameFactory createJavaKeywords() {
    return createElementsWithSplit(
      " ",
      "private", "protected", "public", "abstract", "class", "extends", "final", "implements", "interface", "native",
      "new", "static", "strictfp", "synchronized", "transient", "volatile", "break", "case", "continue", "do", "else",
      "for", "if", "instanceof", "return", "switch", "while", "assert", "case", "finally", "throw", "throws", "try",
      "import", "package", "boolean", "byte", "char", "double", "float", "int", "long", "short", "super", "this",
      "void", "goto", "const"
    );
  }

  public static NameFactory createUtf16NotUtf8() {
    String[] elements = IntStream.rangeClosed(Character.MIN_CODE_POINT, Character.MAX_CODE_POINT)
      .filter(Character::isValidCodePoint)
      .filter(c -> c > 0x7F && c < 0xD800 || c > 0xDFFF)
      .mapToObj(it -> new String(new int[]{it}, 0, 1))
      .toArray(String[]::new);
    return createElements(elements);
  }

  public static NameFactory createUtf16InvalidCodePoint() {
    String[] elements = IntStream.concat(
      IntStream.of(0xD800),
      IntStream.rangeClosed(0xDC00, 0xDCFF)
    ).mapToObj(it -> new String(new int[]{it}, 0, 1))
      .toArray(String[]::new);
    return createElements(elements);  }
}

package bot.inker.krypix.common.directory;

import java.util.stream.IntStream;

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

  public static NameFactory createUtf16() {
    /*
    List<String> utf16NotUtf8Chars = new ArrayList<>();

    for (int i = Character.MAX_VALUE; i < Integer.MAX_VALUE; i++) {
      switch (Character.getType(i)) {
        case Character.CONTROL:
        case Character.FORMAT: {
          utf16NotUtf8Chars.add(new String(new int[]{i}, 0, 1));
          break;
        }
      }
    }
    */

    return createAlphabet(
      69821, 119155, 119156, 119157, 119158, 119159, 119160, 119161, 119162, 917505, 917536, 917537, 917538, 917539,
      917540, 917541, 917542, 917543, 917544, 917545, 917546, 917547, 917548, 917549, 917550, 917551, 917552, 917553,
      917554, 917555, 917556, 917557, 917558, 917559, 917560, 917561, 917562, 917563, 917564, 917565, 917566, 917567,
      917568, 917569, 917570, 917571, 917572, 917573, 917574, 917575, 917576, 917577, 917578, 917579, 917580, 917581,
      917582, 917583, 917584, 917585, 917586, 917587, 917588, 917589, 917590, 917591, 917592, 917593, 917594, 917595,
      917596, 917597, 917598, 917599, 917600, 917601, 917602, 917603, 917604, 917605, 917606, 917607, 917608, 917609,
      917610, 917611, 917612, 917613, 917614, 917615, 917616, 917617, 917618, 917619, 917620, 917621, 917622, 917623,
      917624, 917625, 917626, 917627, 917628, 917629, 917630, 917631
    );
  }
}

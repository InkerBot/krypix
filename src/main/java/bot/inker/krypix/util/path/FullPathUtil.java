package bot.inker.krypix.util.path;

/**
 * Utility class for working with full paths.
 * <p>
 * Full paths are strings that can be used to identify a resource in a file system.
 * <code><br>
 * some-directory/some-file.txt<br>
 * some-jar.jar!/some-directory/some-file.txt<br>
 * some-directory/some-jar.jar!/some-file.txt
 * </code>
 * <p>
 * Short paths are strings that can be used to identify a resource in a file system.
 * <code><br>
 * some-file.txt<br>
 * some-directory/some-file.txt<br>
 * </code>
 */
public final class FullPathUtil {
  /**
   * Returns the path part of a full path.
   * <code><br>
   * some-jar.jar!/some-directory/some-file.txt -> some-directory/some-file.txt
   * </code>
   *
   * @param fullPath the full path
   * @return the path part of the full path
   */
  public static String getShortPath(String fullPath) {
    int lastIndexOf = fullPath.lastIndexOf('!');
    if (lastIndexOf == -1) {
      return removeLeadingSlash(fullPath);
    }
    return removeLeadingSlash(fullPath.substring(lastIndexOf + 1));
  }

  public static String getPreviousFullPath(String fullPath) {
    int lastIndexOf = fullPath.lastIndexOf('!');
    if (lastIndexOf == -1) {
      throw new IllegalArgumentException("No path found in full path: " + fullPath);
    }
    return fullPath.substring(0, lastIndexOf);
  }

  public static String removeLeadingSlash(String path) {
    int i = 0;
    while (i < path.length() && (path.charAt(i) == '/' || path.charAt(i) == '\\')) {
      i++;
    }
    return path.substring(i);
  }

  public static String addLeadingSlash(String path) {
    int i = 0;
    while (i < path.length() && (path.charAt(i) == '/' || path.charAt(i) == '\\')) {
      i++;
    }
    return i == 0 ? '/' + path : path;
  }

  /**
   * Replaces the path part of a full path.
   *
   * @param fullPath the full path
   * @param newPath  the new path
   * @return the full path with the new path
   */
  public static String replacePath(String fullPath, String newPath) {
    int lastIndexOf = fullPath.lastIndexOf('!');
    if (lastIndexOf == -1) {
      return newPath;
    }
    return fullPath.substring(0, lastIndexOf + 1) + addLeadingSlash(newPath);
  }
}

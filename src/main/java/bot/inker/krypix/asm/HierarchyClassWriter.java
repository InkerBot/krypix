package bot.inker.krypix.asm;

import bot.inker.krypix.AppView;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class HierarchyClassWriter extends ClassWriter {
  private final AppView appView;
  private final ClassLoader classLoader;
  private final List<String> missingClasses = new ArrayList<>();

  public HierarchyClassWriter(AppView appView, int flags) {
    super(flags);
    this.appView = appView;
    this.classLoader = ClassLoader.getSystemClassLoader();
  }

  public HierarchyClassWriter(AppView appView, ClassLoader classLoader, int flags) {
    super(flags);
    this.appView = appView;
    this.classLoader = classLoader;
  }

  public HierarchyClassWriter(AppView appView, ClassLoader classLoader, ClassReader classReader, int flags) {
    super(classReader, flags);
    this.appView = appView;
    this.classLoader = classLoader;
  }

  @Override
  protected String getCommonSuperClass(String type1, String type2) {
    var class1 = appView.getClass(type1);
    var class2 = appView.getClass(type2);

    if (class1.isEmpty() || class2.isEmpty()) {
      try {
        return getCommonSuperClassFromClassPath(type1, type2);
      } catch (TypeNotPresentException e) {
        if (class1.isEmpty()) {
          missingClasses.add(type1);
        }
        if (class2.isEmpty()) {
          missingClasses.add(type2);
        }
        throw e;
      }
    }

    return appView.hierarchy().getCommonSuperClass(class1.get(), class2.get()).name();
  }

  private String getCommonSuperClassFromClassPath(final String type1, final String type2) {
    try {
      ClassReader info1 = typeInfo(type1);
      ClassReader info2 = typeInfo(type2);
      if ((info1.getAccess() & Opcodes.ACC_INTERFACE) != 0) {
        if (typeImplements(type2, info2, type1)) {
          return type1;
        } else {
          return "java/lang/Object";
        }
      }
      if ((info2.getAccess() & Opcodes.ACC_INTERFACE) != 0) {
        if (typeImplements(type1, info1, type2)) {
          return type2;
        } else {
          return "java/lang/Object";
        }
      }
      StringBuilder b1 = typeAncestors(type1, info1);
      StringBuilder b2 = typeAncestors(type2, info2);
      String result = "java/lang/Object";
      int end1 = b1.length();
      int end2 = b2.length();
      while (true) {
        int start1 = b1.lastIndexOf(";", end1 - 1);
        int start2 = b2.lastIndexOf(";", end2 - 1);
        if (start1 != -1 && start2 != -1
          && end1 - start1 == end2 - start2) {
          String p1 = b1.substring(start1 + 1, end1);
          String p2 = b2.substring(start2 + 1, end2);
          if (p1.equals(p2)) {
            result = p1;
            end1 = start1;
            end2 = start2;
          } else {
            return result;
          }
        } else {
          return result;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e.toString());
    }
  }

  private StringBuilder typeAncestors(String type, ClassReader info)
    throws IOException {
    StringBuilder b = new StringBuilder();
    while (!"java/lang/Object".equals(type)) {
      b.append(';').append(type);
      type = info.getSuperName();
      info = typeInfo(type);
    }
    return b;
  }

  private boolean typeImplements(String type, ClassReader info, String itf)
    throws IOException {
    while (!"java/lang/Object".equals(type)) {
      String[] itfs = info.getInterfaces();
      for (String it : itfs) {
        if (it.equals(itf)) {
          return true;
        }
      }
      for (String it : itfs) {
        if (typeImplements(it, typeInfo(it), itf)) {
          return true;
        }
      }
      type = info.getSuperName();
      info = typeInfo(type);
    }
    return false;
  }

  private ClassReader typeInfo(final String type) throws IOException {
    try (InputStream in = getClassLoader().getResourceAsStream(type + ".class")) {
      if (in == null) {
        throw new TypeNotPresentException(type, new IOException("Resource not found: " + type + ".class"));
      }
      return new ClassReader(in);
    }
  }

  @Override
  protected ClassLoader getClassLoader() {
    return classLoader;
  }
}

package bot.inker.krypix.asm.locator;

import bot.inker.krypix.AppView;
import bot.inker.krypix.KrypixClass;
import bot.inker.krypix.KrypixMethod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class MethodLocator {
  private static final String ANY = new String("*");

  private final AppView appView;

  private String owner = ANY;
  private String name = ANY;
  private String desc = ANY;

  private boolean exactOwner = true;

  public MethodLocator(AppView appView) {
    this.appView = appView;
  }

  public MethodLocator owner(String owner) {
    this.owner = owner;
    return this;
  }

  public MethodLocator name(String name) {
    this.name = name;
    return this;
  }

  public MethodLocator desc(String desc) {
    this.desc = desc;
    return this;
  }

  public MethodLocator exactOwner(boolean exactOwner) {
    this.exactOwner = exactOwner;
    return this;
  }

  @SuppressWarnings("StringEquality")
  private Stream<KrypixClass> locateOwner() {
    return  (owner == ANY)
      ? appView.allClasses()
      : appView.getClasses(owner);
  }

  private Stream<KrypixClass> collectSuperClassesIfNeed(KrypixClass owner) {
    if (exactOwner) {
      return Stream.of(owner);
    }
    List<KrypixClass> classes = new ArrayList<>();
    classes.add(owner);
    for (KrypixClass t = owner; t.superClass() != null; t = t.superClass()) {
      classes.add(t.superClass());
    }
    return classes.stream();
  }

  public Stream<KrypixMethod> locate() {
    return locateOwner()
      .flatMap(this::collectSuperClassesIfNeed)
      .flatMap(c -> c.methods().stream())
      .filter(m -> m.name().equals(name) && m.desc().equals(desc));
  }

  public @Nullable KrypixMethod firstOrNull() {
    return locate().findFirst().orElse(null);
  }

  private static class DescComparator implements Comparator<KrypixMethod> {
    @Override
    public int compare(KrypixMethod o1, KrypixMethod o2) {
      return Integer.compare(o1.desc().length(), o2.desc().length());
    }
  }
}

package bot.inker.krypix.asm.locator;

import bot.inker.krypix.AppView;
import bot.inker.krypix.KrypixClass;
import bot.inker.krypix.KrypixField;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FieldLocator {
  @SuppressWarnings("StringOperationCanBeSimplified") // we need to create new string
  private static final String ANY = new String("*");

  private final AppView appView;

  private String owner = ANY;
  private String name = ANY;
  private String desc = ANY;

  private boolean exactOwner = true;

  public FieldLocator(AppView appView) {
    this.appView = appView;
  }

  public FieldLocator owner(String owner) {
    this.owner = owner;
    return this;
  }

  public FieldLocator name(String name) {
    this.name = name;
    return this;
  }

  public FieldLocator desc(String desc) {
    this.desc = desc;
    return this;
  }

  public FieldLocator exactOwner(boolean exactOwner) {
    this.exactOwner = exactOwner;
    return this;
  }

  @SuppressWarnings("StringEquality")
  private Stream<KrypixClass> locateOwner() {
    return (owner == ANY)
      ? appView.allClasses()
      : appView.getClasses(owner);
  }

  private void collectHierarchyDFS(KrypixClass owner, List<KrypixClass> visitedClasses) {
    visitedClasses.add(owner);
    if (owner.superClass() != null) {
      collectHierarchyDFS(owner.superClass(), visitedClasses);
    }
    for (KrypixClass anInterface : owner.interfaces()) {
      collectHierarchyDFS(anInterface, visitedClasses);
    }
  }

  private Stream<KrypixClass> collectSuperClassesIfNeed(KrypixClass owner) {
    if (exactOwner) {
      return Stream.of(owner);
    }
    List<KrypixClass> classes = new ArrayList<>();
    collectHierarchyDFS(owner, classes);
    return classes.stream();
  }

  public Stream<KrypixField> locate() {
    return locateOwner()
      .flatMap(this::collectSuperClassesIfNeed)
      .flatMap(c -> c.fields().stream())
      .filter(m -> m.name().equals(name) && m.desc().equals(desc));
  }

  public @Nullable KrypixField firstOrNull() {
    return locate().findFirst().orElse(null);
  }
}

package bot.inker.krypix.loader;

import bot.inker.krypix.KrypixClass;
import bot.inker.krypix.KrypixScope;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ClassPool {
  private final KrypixScope scope;
  private final Map<String, KrypixClass> classes = new LinkedHashMap<>();

  public ClassPool(KrypixScope scope) {
    this.scope = scope;
  }

  public KrypixScope scope() {
    return scope;
  }

  public Optional<KrypixClass> get(String name) {
    return Optional.ofNullable(classes.get(name));
  }

  public void put(String name, KrypixClass clazz) {
    classes.put(name, clazz);
  }

  public Collection<KrypixClass> all() {
    return classes.values();
  }
}

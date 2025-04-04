package bot.inker.krypix.ir;

import bot.inker.krypix.ir.ref.TypeRef;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class LocalVariable {
  private final String name;
  private final TypeRef desc;
  private final @Nullable String signature;
  private final int index;

  public LocalVariable(String name, TypeRef desc, @Nullable String signature, int index) {
    this.name = name;
    this.desc = desc;
    this.signature = signature;
    this.index = index;
  }

  public String name() {
    return name;
  }

  public TypeRef desc() {
    return desc;
  }

  public Optional<String> signature() {
    return Optional.ofNullable(signature);
  }

  public int index() {
    return index;
  }
}

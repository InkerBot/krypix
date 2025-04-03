package bot.inker.krypix.ir.ref;

import java.util.Arrays;

public final class MethodType {
  private final TypeRef[] parameterTypes;
  private final TypeRef returnType;

  public MethodType(TypeRef[] parameterTypes, TypeRef returnType) {
    this.parameterTypes = Arrays.copyOf(parameterTypes, parameterTypes.length);
    this.returnType = returnType;
  }

  public TypeRef[] parameterTypes() {
    return Arrays.copyOf(parameterTypes, parameterTypes.length);
  }

  public TypeRef returnType() {
    return returnType;
  }
}

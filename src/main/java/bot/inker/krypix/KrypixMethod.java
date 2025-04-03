package bot.inker.krypix;

import bot.inker.krypix.common.WithModifier;
import bot.inker.krypix.ir.ref.MethodType;
import org.objectweb.asm.tree.MethodNode;

public final class KrypixMethod implements WithModifier.Mutable {
  private final KrypixClass owner;
  private MethodNode methodNode;
  private MethodType typeRef;

  public KrypixMethod(KrypixClass owner, MethodNode methodNode) {
    this.owner = owner;
    this.methodNode = methodNode;
  }

  public MethodNode methodNode() {
    return methodNode;
  }

  public KrypixScope scope() {
    return owner.scope();
  }

  public KrypixClass owner() {
    return owner;
  }

  public void methodNode(MethodNode methodNode) {
    this.methodNode = methodNode;
  }

  public String name() {
    return methodNode.name;
  }

  public void name(String name) {
    this.methodNode.name = name;
  }

  public String desc() {
    return typeRef == null
      ? methodNode.desc
      : typeRef.desc();
  }

  public MethodType type() {
    return typeRef;
  }

  public void type(MethodType typeRef) {
    this.typeRef = typeRef;
  }

  @Override
  public int modifier() {
    return methodNode.access;
  }

  @Override
  public void modifier(int modifier) {
    methodNode.access = modifier;
  }

  public boolean hasCode() {
    return !isAbstract() && !isNative();
  }
}

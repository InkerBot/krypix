package bot.inker.krypix;

import bot.inker.krypix.common.WithModifier;
import bot.inker.krypix.ir.ref.TypeRef;
import org.objectweb.asm.tree.FieldNode;

public final class KrypixField implements WithModifier.Mutable {
  private final KrypixClass owner;
  private FieldNode fieldNode;
  private TypeRef typeRef;

  public KrypixField(KrypixClass owner, FieldNode fieldNode) {
    this.owner = owner;
    this.fieldNode = fieldNode;
  }

  public KrypixClass owner() {
    return owner;
  }

  public FieldNode fieldNode() {
    return fieldNode;
  }

  public void fieldNode(FieldNode fieldNode) {
    this.fieldNode = fieldNode;
  }

  public String name() {
    return fieldNode.name;
  }

  public void name(String name) {
    this.fieldNode.name = name;
  }

  public String desc() {
    return typeRef == null
      ? fieldNode.desc
      : typeRef.desc();
  }

  public TypeRef type() {
    return typeRef;
  }

  public void type(TypeRef typeRef) {
    this.typeRef = typeRef;
  }

  @Override
  public int modifier() {
    return fieldNode.access;
  }

  @Override
  public void modifier(int modifier) {
    fieldNode.access = modifier;
  }
}

package bot.inker.krypix;

import bot.inker.krypix.common.WithModifier;
import org.objectweb.asm.tree.FieldNode;

public final class KrypixField implements WithModifier.Mutable {
  private final KrypixClass owner;
  private FieldNode fieldNode;

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
    return fieldNode.desc;
  }

  public void desc(String desc) {
    this.fieldNode.desc = desc;
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

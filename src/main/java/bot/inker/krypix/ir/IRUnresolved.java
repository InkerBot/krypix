package bot.inker.krypix.ir;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.Collections;

public final class IRUnresolved implements IRAbstract {
  private final AbstractInsnNode insnNode;

  public IRUnresolved(AbstractInsnNode insnNode) {
    this.insnNode = insnNode;
  }

  public AbstractInsnNode getCloned() {
    return insnNode.clone(Collections.emptyMap());
  }

  @Override
  public String toString() {
    return "unknown " + insnNode.getOpcode() + " " + insnNode.getClass().getSimpleName();
  }
}

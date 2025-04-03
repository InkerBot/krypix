package bot.inker.krypix.util;

import org.objectweb.asm.tree.AbstractInsnNode;

public final class CloneUtil {
  private CloneUtil() {
    throw new UnsupportedOperationException();
  }

  public static AbstractInsnNode clone(AbstractInsnNode insnNode) {
    return insnNode.clone(null);
  }
}

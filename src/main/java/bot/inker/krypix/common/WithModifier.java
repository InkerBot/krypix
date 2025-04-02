package bot.inker.krypix.common;

import org.objectweb.asm.Opcodes;

public interface WithModifier {
  int modifier();

  default boolean isPublic() {
    return (modifier() & Opcodes.ACC_PUBLIC) != 0;
  }

  default boolean isPrivate() {
    return (modifier() & Opcodes.ACC_PRIVATE) != 0;
  }

  default boolean isProtected() {
    return (modifier() & Opcodes.ACC_PROTECTED) != 0;
  }

  default boolean isPackagePrivate() {
    return !isPublic() && !isPrivate() && !isProtected();
  }

  default boolean isStatic() {
    return (modifier() & Opcodes.ACC_STATIC) != 0;
  }

  default boolean isFinal() {
    return (modifier() & Opcodes.ACC_FINAL) != 0;
  }

  default boolean isSuper() {
    return (modifier() & Opcodes.ACC_SUPER) != 0;
  }

  default boolean isSynchronized() {
    return (modifier() & Opcodes.ACC_SYNCHRONIZED) != 0;
  }

  default boolean isOpen() {
    return (modifier() & Opcodes.ACC_OPEN) != 0;
  }

  default boolean isTransitive() {
    return (modifier() & Opcodes.ACC_TRANSITIVE) != 0;
  }

  default boolean isVolatile() {
    return (modifier() & Opcodes.ACC_VOLATILE) != 0;
  }

  default boolean isBridge() {
    return (modifier() & Opcodes.ACC_BRIDGE) != 0;
  }

  default boolean isStaticPhase() {
    return (modifier() & Opcodes.ACC_STATIC_PHASE) != 0;
  }

  default boolean isVarargs() {
    return (modifier() & Opcodes.ACC_VARARGS) != 0;
  }

  default boolean isTransient() {
    return (modifier() & Opcodes.ACC_TRANSIENT) != 0;
  }

  default boolean isNative() {
    return (modifier() & Opcodes.ACC_NATIVE) != 0;
  }

  default boolean isInterface() {
    return (modifier() & Opcodes.ACC_INTERFACE) != 0;
  }

  default boolean isAbstract() {
    return (modifier() & Opcodes.ACC_ABSTRACT) != 0;
  }

  default boolean isStrict() {
    return (modifier() & Opcodes.ACC_STRICT) != 0;
  }

  default boolean isSynthetic() {
    return (modifier() & Opcodes.ACC_SYNTHETIC) != 0;
  }

  default boolean isAnnotation() {
    return (modifier() & Opcodes.ACC_ANNOTATION) != 0;
  }

  default boolean isEnum() {
    return (modifier() & Opcodes.ACC_ENUM) != 0;
  }

  default boolean isMandated() {
    return (modifier() & Opcodes.ACC_MANDATED) != 0;
  }

  default boolean isModule() {
    return (modifier() & Opcodes.ACC_MODULE) != 0;
  }

  default boolean isRecord() {
    return (modifier() & Opcodes.ACC_RECORD) != 0;
  }

  default boolean isDeprecated() {
    return (modifier() & Opcodes.ACC_DEPRECATED) != 0;
  }

  interface Mutable extends WithModifier {
    void modifier(int modifier);

    default void setPublic() {
      modifier(modifier() | Opcodes.ACC_PUBLIC);
    }

    default void setPublic(boolean isPublic) {
      if (isPublic) {
        modifier(modifier() | Opcodes.ACC_PUBLIC);
      } else {
        modifier(modifier() & ~Opcodes.ACC_PUBLIC);
      }
    }

    default void setPrivate() {
      modifier(modifier() | Opcodes.ACC_PRIVATE);
    }

    default void setPrivate(boolean isPrivate) {
      if (isPrivate) {
        modifier(modifier() | Opcodes.ACC_PRIVATE);
      } else {
        modifier(modifier() & ~Opcodes.ACC_PRIVATE);
      }
    }

    default void setProtected() {
      modifier(modifier() | Opcodes.ACC_PROTECTED);
    }

    default void setProtected(boolean isProtected) {
      if (isProtected) {
        modifier(modifier() | Opcodes.ACC_PROTECTED);
      } else {
        modifier(modifier() & ~Opcodes.ACC_PROTECTED);
      }
    }

    default void setPackagePrivate() {
      modifier(modifier() & ~(Opcodes.ACC_PUBLIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED));
    }

    default void setPackagePrivate(boolean isPackagePrivate) {
      if (isPackagePrivate) {
        modifier(modifier() & ~(Opcodes.ACC_PUBLIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED));
      } else {
        modifier(modifier() | Opcodes.ACC_PUBLIC);
      }
    }

    default void setStatic() {
      modifier(modifier() | Opcodes.ACC_STATIC);
    }

    default void setStatic(boolean isStatic) {
      if (isStatic) {
        modifier(modifier() | Opcodes.ACC_STATIC);
      } else {
        modifier(modifier() & ~Opcodes.ACC_STATIC);
      }
    }

    default void setFinal() {
      modifier(modifier() | Opcodes.ACC_FINAL);
    }

    default void setFinal(boolean isFinal) {
      if (isFinal) {
        modifier(modifier() | Opcodes.ACC_FINAL);
      } else {
        modifier(modifier() & ~Opcodes.ACC_FINAL);
      }
    }

    default void setSuper() {
      modifier(modifier() | Opcodes.ACC_SUPER);
    }

    default void setSuper(boolean isSuper) {
      if (isSuper) {
        modifier(modifier() | Opcodes.ACC_SUPER);
      } else {
        modifier(modifier() & ~Opcodes.ACC_SUPER);
      }
    }

    default void setSynchronized() {
      modifier(modifier() | Opcodes.ACC_SYNCHRONIZED);
    }

    default void setSynchronized(boolean isSynchronized) {
      if (isSynchronized) {
        modifier(modifier() | Opcodes.ACC_SYNCHRONIZED);
      } else {
        modifier(modifier() & ~Opcodes.ACC_SYNCHRONIZED);
      }
    }

    default void setOpen() {
      modifier(modifier() | Opcodes.ACC_OPEN);
    }

    default void setOpen(boolean isOpen) {
      if (isOpen) {
        modifier(modifier() | Opcodes.ACC_OPEN);
      } else {
        modifier(modifier() & ~Opcodes.ACC_OPEN);
      }
    }

    default void setTransitive() {
      modifier(modifier() | Opcodes.ACC_TRANSITIVE);
    }

    default void setTransitive(boolean isTransitive) {
      if (isTransitive) {
        modifier(modifier() | Opcodes.ACC_TRANSITIVE);
      } else {
        modifier(modifier() & ~Opcodes.ACC_TRANSITIVE);
      }
    }

    default void setVolatile() {
      modifier(modifier() | Opcodes.ACC_VOLATILE);
    }

    default void setVolatile(boolean isVolatile) {
      if (isVolatile) {
        modifier(modifier() | Opcodes.ACC_VOLATILE);
      } else {
        modifier(modifier() & ~Opcodes.ACC_VOLATILE);
      }
    }

    default void setBridge() {
      modifier(modifier() | Opcodes.ACC_BRIDGE);
    }

    default void setBridge(boolean isBridge) {
      if (isBridge) {
        modifier(modifier() | Opcodes.ACC_BRIDGE);
      } else {
        modifier(modifier() & ~Opcodes.ACC_BRIDGE);
      }
    }

    default void setStaticPhase() {
      modifier(modifier() | Opcodes.ACC_STATIC_PHASE);
    }

    default void setStaticPhase(boolean isStaticPhase) {
      if (isStaticPhase) {
        modifier(modifier() | Opcodes.ACC_STATIC_PHASE);
      } else {
        modifier(modifier() & ~Opcodes.ACC_STATIC_PHASE);
      }
    }

    default void setVarargs() {
      modifier(modifier() | Opcodes.ACC_VARARGS);
    }

    default void setVarargs(boolean isVarargs) {
      if (isVarargs) {
        modifier(modifier() | Opcodes.ACC_VARARGS);
      } else {
        modifier(modifier() & ~Opcodes.ACC_VARARGS);
      }
    }

    default void setTransient() {
      modifier(modifier() | Opcodes.ACC_TRANSIENT);
    }

    default void setTransient(boolean isTransient) {
      if (isTransient) {
        modifier(modifier() | Opcodes.ACC_TRANSIENT);
      } else {
        modifier(modifier() & ~Opcodes.ACC_TRANSIENT);
      }
    }

    default void setNative() {
      modifier(modifier() | Opcodes.ACC_NATIVE);
    }

    default void setNative(boolean isNative) {
      if (isNative) {
        modifier(modifier() | Opcodes.ACC_NATIVE);
      } else {
        modifier(modifier() & ~Opcodes.ACC_NATIVE);
      }
    }

    default void setInterface() {
      modifier(modifier() | Opcodes.ACC_INTERFACE);
    }

    default void setInterface(boolean isInterface) {
      if (isInterface) {
        modifier(modifier() | Opcodes.ACC_INTERFACE);
      } else {
        modifier(modifier() & ~Opcodes.ACC_INTERFACE);
      }
    }

    default void setAbstract() {
      modifier(modifier() | Opcodes.ACC_ABSTRACT);
    }

    default void setAbstract(boolean isAbstract) {
      if (isAbstract) {
        modifier(modifier() | Opcodes.ACC_ABSTRACT);
      } else {
        modifier(modifier() & ~Opcodes.ACC_ABSTRACT);
      }
    }

    default void setStrict() {
      modifier(modifier() | Opcodes.ACC_STRICT);
    }

    default void setStrict(boolean isStrict) {
      if (isStrict) {
        modifier(modifier() | Opcodes.ACC_STRICT);
      } else {
        modifier(modifier() & ~Opcodes.ACC_STRICT);
      }
    }

    default void setSynthetic() {
      modifier(modifier() | Opcodes.ACC_SYNTHETIC);
    }

    default void setSynthetic(boolean isSynthetic) {
      if (isSynthetic) {
        modifier(modifier() | Opcodes.ACC_SYNTHETIC);
      } else {
        modifier(modifier() & ~Opcodes.ACC_SYNTHETIC);
      }
    }

    default void setAnnotation() {
      modifier(modifier() | Opcodes.ACC_ANNOTATION);
    }

    default void setAnnotation(boolean isAnnotation) {
      if (isAnnotation) {
        modifier(modifier() | Opcodes.ACC_ANNOTATION);
      } else {
        modifier(modifier() & ~Opcodes.ACC_ANNOTATION);
      }
    }

    default void setEnum() {
      modifier(modifier() | Opcodes.ACC_ENUM);
    }

    default void setEnum(boolean isEnum) {
      if (isEnum) {
        modifier(modifier() | Opcodes.ACC_ENUM);
      } else {
        modifier(modifier() & ~Opcodes.ACC_ENUM);
      }
    }

    default void setMandated() {
      modifier(modifier() | Opcodes.ACC_MANDATED);
    }

    default void setMandated(boolean isMandated) {
      if (isMandated) {
        modifier(modifier() | Opcodes.ACC_MANDATED);
      } else {
        modifier(modifier() & ~Opcodes.ACC_MANDATED);
      }
    }

    default void setModule() {
      modifier(modifier() | Opcodes.ACC_MODULE);
    }

    default void setRecord() {
      modifier(modifier() | Opcodes.ACC_RECORD);
    }

    default void setRecord(boolean isRecord) {
      if (isRecord) {
        modifier(modifier() | Opcodes.ACC_RECORD);
      } else {
        modifier(modifier() & ~Opcodes.ACC_RECORD);
      }
    }

    default void setDeprecated() {
      modifier(modifier() | Opcodes.ACC_DEPRECATED);
    }

    default void setDeprecated(boolean isDeprecated) {
      if (isDeprecated) {
        modifier(modifier() | Opcodes.ACC_DEPRECATED);
      } else {
        modifier(modifier() & ~Opcodes.ACC_DEPRECATED);
      }
    }
  }
}

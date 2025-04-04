package bot.inker.krypix.asm;

import bot.inker.krypix.AppView;
import bot.inker.krypix.KrypixMethod;
import bot.inker.krypix.common.attachment.AttachmentKey;
import bot.inker.krypix.ir.*;
import bot.inker.krypix.ir.array.IRArrayLength;
import bot.inker.krypix.ir.array.IRArrayLoad;
import bot.inker.krypix.ir.array.IRArrayStore;
import bot.inker.krypix.ir.array.IRNewArray;
import bot.inker.krypix.ir.branch.*;
import bot.inker.krypix.ir.document.IRDocument;
import bot.inker.krypix.ir.field.IRField;
import bot.inker.krypix.ir.field.IRFieldStore;
import bot.inker.krypix.ir.handle.IRHandle;
import bot.inker.krypix.ir.local.IRLocalLoad;
import bot.inker.krypix.ir.local.IRLocalStore;
import bot.inker.krypix.ir.method.IRInvokeDynamic;
import bot.inker.krypix.ir.method.IRInvokeMethod;
import bot.inker.krypix.ir.monitor.IRMonitorEnter;
import bot.inker.krypix.ir.monitor.IRMonitorExit;
import bot.inker.krypix.ir.num.*;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;

public final class ControlRebuild {
  private static final AttachmentKey<LabelNode> REBUILD_LABEL = AttachmentKey.create("rebuild-label");
  private final AppView appView;
  private final KrypixMethod method;
  private final MethodBody methodBody;

  private final InsnList insnList = new InsnList();
  private final List<TryCatchBlockNode> tryCatchBlocks = new ArrayList<>();

  public ControlRebuild(AppView appView, KrypixMethod method) {
    this.appView = appView;
    this.method = method;
    this.methodBody = method.body();
  }

  public void rebuild() {
    for (CodeBlock codeBlock : methodBody.codeBlocks()) {
      codeBlock.setAttachment(REBUILD_LABEL, new LabelNode());
    }

    for (int i = 0; i < methodBody.codeBlocks().size(); i++) {
      var codeBlock = methodBody.codeBlocks().get(i);
      var labelNode = codeBlock.requireAttachment(REBUILD_LABEL);
      var nextBlock = i + 1 < methodBody.codeBlocks().size() ? methodBody.codeBlocks().get(i + 1) : null;
      var nextLabel = nextBlock != null ? nextBlock.requireAttachment(REBUILD_LABEL) : new LabelNode();

      insnList.add(labelNode);

      for (var exceptionHandler : codeBlock.exceptionHandlers()) {
        tryCatchBlocks.add(new TryCatchBlockNode(
          labelNode,
          nextLabel,
          exceptionHandler.handler().requireAttachment(REBUILD_LABEL),
          exceptionHandler.catchType() == null ? null : exceptionHandler.catchType().name()
        ));
      }

      codeBlock.getAttachment(CodeBlockAttachments.LINE_NUMBER).ifPresent(lineNumber -> {
        var lineNumberNode = new LineNumberNode(lineNumber, labelNode);
        insnList.add(lineNumberNode);
      });

      for (var instruction : codeBlock.instructions()) {
        if (instruction instanceof IRNop || instruction instanceof IRDocument) {
          //
        } else if (instruction instanceof IRConst ir) {
          rebuildConst(ir);
        } else if (instruction instanceof IRLocalLoad ir) {
          insnList.add(new VarInsnNode(ir.type().forOpcode(Opcodes.ILOAD), ir.index()));
        } else if (instruction instanceof IRLocalStore ir) {
          insnList.add(new VarInsnNode(ir.type().forOpcode(Opcodes.ISTORE), ir.index()));
        } else if (instruction instanceof IRArrayLoad ir) {
          insnList.add(new InsnNode(ir.type().forOpcode(Opcodes.IALOAD)));
        } else if (instruction instanceof IRArrayStore ir) {
          insnList.add(new InsnNode(ir.type().forOpcode(Opcodes.IASTORE)));
        } else if (instruction instanceof IRStackOperator ir) {
          insnList.add(new InsnNode(ir.type().opcode()));
        } else if (instruction instanceof IRMathBinary ir) {
          int baseOpcode = switch (ir.operation()) {
            case ADD -> Opcodes.IADD;
            case SUB -> Opcodes.ISUB;
            case MUL -> Opcodes.IMUL;
            case DIV -> Opcodes.IDIV;
            case REM -> Opcodes.IREM;
            default -> throw new UnsupportedOperationException("Unsupported math operation: " + ir.operation());
          };
          insnList.add(new InsnNode(ir.type().forOpcode(baseOpcode)));
        } else if (instruction instanceof IRMathUnary ir) {
          int baseOpcode = switch (ir.operation()) {
            case NEG -> Opcodes.INEG;
            default -> throw new UnsupportedOperationException("Unsupported math operation: " + ir.operation());
          };
          insnList.add(new InsnNode(ir.type().forOpcode(baseOpcode)));
        } else if (instruction instanceof IRMathBitwise ir) {
          int baseOpcode = switch (ir.operation()) {
            case SHL -> Opcodes.ISHL;
            case SHR -> Opcodes.ISHR;
            case USHR -> Opcodes.IUSHR;
            case AND -> Opcodes.IAND;
            case OR -> Opcodes.IOR;
            case XOR -> Opcodes.IXOR;
            default -> throw new UnsupportedOperationException("Unsupported math operation: " + ir.operation());
          };
          insnList.add(new InsnNode(ir.type().forOpcode(baseOpcode)));
        } else if (instruction instanceof IRMathCast ir) {
          rebuildCast(ir);
        } else if (instruction instanceof IRMathCmp ir) {
          int opcode = switch (ir.type()) {
            case LONG -> switch (ir.operation()) {
              case CMP -> Opcodes.LCMP;
              default -> throw new UnsupportedOperationException("Unsupported math operation: " + ir.operation());
            };
            case FLOAT -> switch (ir.operation()) {
              case CMPL -> Opcodes.FCMPL;
              case CMPG -> Opcodes.FCMPG;
              default -> throw new UnsupportedOperationException("Unsupported math operation: " + ir.operation());
            };
            case DOUBLE -> switch (ir.operation()) {
              case CMPL -> Opcodes.DCMPL;
              case CMPG -> Opcodes.DCMPG;
              default -> throw new UnsupportedOperationException("Unsupported math operation: " + ir.operation());
            };
            default -> throw new UnsupportedOperationException("Unsupported comparison type: " + ir.type());
          };
          insnList.add(new InsnNode(opcode));
        } else if (instruction instanceof IRField ir) {
          int opcode = Opcodes.GETSTATIC;
          if (ir instanceof IRFieldStore) opcode += 1;
          if (!ir.isStatic()) opcode += 2;

          insnList.add(new FieldInsnNode(
            opcode,
            ir.field().owner().name(),
            ir.field().name(),
            ir.field().desc().desc()
          ));
        } else if (instruction instanceof IRInvokeMethod ir) {
          int opcode = switch (ir.type()) {
            case VIRTUAL -> Opcodes.INVOKEVIRTUAL;
            case SPECIAL -> Opcodes.INVOKESPECIAL;
            case STATIC -> Opcodes.INVOKESTATIC;
            case INTERFACE -> Opcodes.INVOKEINTERFACE;
            default -> throw new UnsupportedOperationException("Unsupported invoke type: " + ir.type());
          };
          insnList.add(new MethodInsnNode(
            opcode,
            ir.method().owner().name(),
            ir.method().name(),
            ir.method().desc().desc(),
            ir.isInterface()
          ));
        } else if (instruction instanceof IRInvokeDynamic ir) {
          insnList.add(new InvokeDynamicInsnNode(
            ir.name(),
            ir.desc().desc(),
            rebuildHandle(ir.bsm()),
            rebuildConstArray(ir.bsmArgs())
          ));
        } else if (instruction instanceof IRNew ir) {
          insnList.add(new TypeInsnNode(Opcodes.NEW, ir.type().internalName()));
        } else if (instruction instanceof IRNewArray ir) {
          if (ir.dimension() != 1) {
            insnList.add(new MultiANewArrayInsnNode(ir.type().desc(), ir.dimension()));
          } else if (ir.type().isPrimitive()) {
            insnList.add(new IntInsnNode(Opcodes.NEWARRAY, ir.type().opcode()));
          } else {
            insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, ir.type().internalName()));
          }
        } else if (instruction instanceof IRArrayLength ir) {
          insnList.add(new InsnNode(Opcodes.ARRAYLENGTH));
        } else if (instruction instanceof IRCheckCast ir) {
          insnList.add(new TypeInsnNode(Opcodes.CHECKCAST, ir.type().internalName()));
        } else if (instruction instanceof IRInstanceOf ir) {
          insnList.add(new TypeInsnNode(Opcodes.INSTANCEOF, ir.type().internalName()));
        } else if (instruction instanceof IRMonitorEnter ir) {
          insnList.add(new InsnNode(Opcodes.MONITORENTER));
        } else if (instruction instanceof IRMonitorExit ir) {
          insnList.add(new InsnNode(Opcodes.MONITOREXIT));
        } else if (instruction instanceof IRUnresolved ir) {
          insnList.add(ir.getCloned());
        } else {
          throw new UnsupportedOperationException("Unsupported instruction type: " + instruction.getClass().getSimpleName());
        }
      }

      if (codeBlock.terminatal() instanceof IRGoto ir) {
        if (ir.target() != nextBlock) {
          var targetLabel = ir.target().requireAttachment(REBUILD_LABEL);
          insnList.add(new JumpInsnNode(Opcodes.GOTO, targetLabel));
        }
      } else if (codeBlock.terminatal() instanceof IRBranchIf ir) {
        var targetLabel = ir.target().requireAttachment(REBUILD_LABEL);
        int opcode = switch (ir.operator()) {
          case EQ -> Opcodes.IFEQ;
          case NE -> Opcodes.IFNE;
          case LT -> Opcodes.IFLT;
          case GE -> Opcodes.IFGE;
          case GT -> Opcodes.IFGT;
          case LE -> Opcodes.IFLE;
          case ICMP_EQ -> Opcodes.IF_ICMPEQ;
          case ICMP_NE -> Opcodes.IF_ICMPNE;
          case ICMP_LT -> Opcodes.IF_ICMPLT;
          case ICMP_GE -> Opcodes.IF_ICMPGE;
          case ICMP_GT -> Opcodes.IF_ICMPGT;
          case ICMP_LE -> Opcodes.IF_ICMPLE;
          case ACMP_EQ -> Opcodes.IF_ACMPEQ;
          case ACMP_NE -> Opcodes.IF_ACMPNE;
          case NULL -> Opcodes.IFNULL;
          case NONNULL -> Opcodes.IFNONNULL;
          default -> throw new UnsupportedOperationException("Unsupported branch operator: " + ir.operator());
        };
        insnList.add(new JumpInsnNode(opcode, targetLabel));
        if (ir.target() != nextBlock) {
          var alternativeLabel = ir.alternative().requireAttachment(REBUILD_LABEL);
          insnList.add(new JumpInsnNode(Opcodes.GOTO, alternativeLabel));
        }
      } else if (codeBlock.terminatal() instanceof IRBranchSwitch ir) {
        LabelNode defaultLabel = ir.defaultBranch().requireAttachment(REBUILD_LABEL);
        long lowestBranch = ir.branches().keySet().intStream().min().orElseThrow();
        long highestBranch = ir.branches().keySet().intStream().max().orElseThrow();

        if ((highestBranch - lowestBranch) * 0.7 <= ir.branches().size() - 1) {
          var labels = new LabelNode[(int) (highestBranch - lowestBranch + 1)];
          for (long j = lowestBranch; j <= highestBranch; j++) {
            var branch = ir.branches().get((int) j);
            labels[(int) (j - lowestBranch)] = (branch == null)
              ? defaultLabel
              : branch.requireAttachment(REBUILD_LABEL);
          }
          insnList.add(new TableSwitchInsnNode((int) lowestBranch, (int) highestBranch, defaultLabel, labels));
        } else {
          var keys = ir.branches().keySet().toIntArray();
          var labels = new LabelNode[keys.length];
          for (int j = 0; j < keys.length; j++) {
            labels[j] = ir.branches().get(keys[j]).requireAttachment(REBUILD_LABEL);
          }
          insnList.add(new LookupSwitchInsnNode(defaultLabel, keys, labels));
        }
      } else if (codeBlock.terminatal() instanceof IRReturn ir) {
        insnList.add(new InsnNode(ir.type() == null ? Opcodes.RETURN : ir.type().forOpcode(Opcodes.IRETURN)));
      } else if (codeBlock.terminatal() instanceof IRThrow ir) {
        insnList.add(new InsnNode(Opcodes.ATHROW));
      } else {
        throw new UnsupportedOperationException("Unsupported terminatal type: " + codeBlock.terminatal().getClass().getSimpleName());
      }

      if (nextBlock == null && nextLabel != null) {
        insnList.add(nextLabel);
      }
    }

    method.methodNode().tryCatchBlocks = tryCatchBlocks;
    method.methodNode().instructions = insnList;
  }

  private void rebuildCast(IRMathCast ir) {
    BaseFrameType sourceType = ir.type();
    BaseValueType targetType = ir.targetType();

    if (sourceType != BaseFrameType.INT && (targetType == BaseValueType.BYTE || targetType == BaseValueType.CHAR || targetType == BaseValueType.SHORT)) {
      insnList.add(new InsnNode(switch (ir.type()) {
        case LONG -> Opcodes.L2I;
        case FLOAT -> Opcodes.F2I;
        case DOUBLE -> Opcodes.D2I;
        default -> throw new UnsupportedOperationException("Unsupported cast type: " + ir.type());
      }));
      sourceType = BaseFrameType.INT;
    }

    int opcode = switch (sourceType) {
      case INT -> switch (targetType) {
        case INT -> Opcodes.NOP;
        case LONG -> Opcodes.I2L;
        case FLOAT -> Opcodes.I2F;
        case DOUBLE -> Opcodes.I2D;
        case BYTE -> Opcodes.I2B;
        case CHAR -> Opcodes.I2C;
        case SHORT -> Opcodes.I2S;
        default -> throw new UnsupportedOperationException("Unsupported cast type: " + targetType);
      };
      case LONG -> switch (targetType) {
        case INT -> Opcodes.L2I;
        case LONG -> Opcodes.NOP;
        case FLOAT -> Opcodes.L2F;
        case DOUBLE -> Opcodes.L2D;
        default -> throw new UnsupportedOperationException("Unsupported cast type: " + targetType);
      };
      case FLOAT -> switch (targetType) {
        case INT -> Opcodes.F2I;
        case LONG -> Opcodes.F2L;
        case FLOAT -> Opcodes.NOP;
        case DOUBLE -> Opcodes.F2D;
        default -> throw new UnsupportedOperationException("Unsupported cast type: " + targetType);
      };
      case DOUBLE -> switch (targetType) {
        case INT -> Opcodes.D2I;
        case LONG -> Opcodes.D2L;
        case FLOAT -> Opcodes.D2F;
        case DOUBLE -> Opcodes.NOP;
        default -> throw new UnsupportedOperationException("Unsupported cast type: " + targetType);
      };
      default -> throw new UnsupportedOperationException("Unsupported cast type: " + sourceType);
    };

    if (opcode == Opcodes.NOP) {
      return;
    }
    insnList.add(new InsnNode(opcode));
  }

  private Handle rebuildHandle(IRHandle handle) {
    String owner, name, desc;
    if (handle.isField()) {
      var fieldRef = handle.field();
      owner = fieldRef.owner().name();
      name = fieldRef.name();
      desc = fieldRef.desc().desc();
    } else {
      var methodRef = handle.method();
      owner = methodRef.owner().name();
      name = methodRef.name();
      desc = methodRef.desc().desc();
    }
    return new Handle(handle.type().opcode(), owner, name, desc, handle.isInterface());
  }

  private Object[] rebuildConstArray(IRConst[] ir) {
    Object[] args = new Object[ir.length];
    for (int i = 0; i < ir.length; i++) {
      args[i] = rebuildConstantValue(ir[i]);
    }
    return args;
  }

  private Object rebuildConstantValue(IRConst ir) {
    return switch (ir.type()) {
      case NULL -> null;
      case INT -> ir.intValue();
      case LONG -> ir.longValue();
      case FLOAT -> ir.floatValue();
      case DOUBLE -> ir.doubleValue();
      case STRING -> ir.stringValue();
      case TYPE -> Type.getType(ir.typeValue().desc());
      case METHOD_TYPE -> Type.getMethodType(ir.methodTypeValue().desc());
      case HANDLE -> rebuildHandle(ir.handleValue());
      case DYNAMIC -> {
        var dynamic = ir.dynamicValue();
        yield new ConstantDynamic(
          dynamic.name(),
          dynamic.desc().desc(),
          rebuildHandle(dynamic.bsm()),
          rebuildConstArray(dynamic.bsmArgs())
        );
      }
    };
  }

  private void rebuildConst(IRConst ir) {
    AbstractInsnNode insn = switch (ir.type()) {
      case NULL -> new InsnNode(Opcodes.ACONST_NULL);
      case INT -> {
        var value = ir.intValue();
        if (-1 <= value && value <= 5) {
          yield new InsnNode(Opcodes.ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
          yield new IntInsnNode(Opcodes.BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
          yield new IntInsnNode(Opcodes.SIPUSH, value);
        } else {
          yield new LdcInsnNode(value);
        }
      }
      case LONG -> {
        var value = ir.longValue();
        if (value == 0L) {
          yield new InsnNode(Opcodes.LCONST_0);
        } else if (value == 1L) {
          yield new InsnNode(Opcodes.LCONST_1);
        } else {
          yield new LdcInsnNode(value);
        }
      }
      case FLOAT -> {
        var value = ir.floatValue();
        if (value == 0.0f) {
          yield new InsnNode(Opcodes.FCONST_0);
        } else if (value == 1.0f) {
          yield new InsnNode(Opcodes.FCONST_1);
        } else if (value == 2.0f) {
          yield new InsnNode(Opcodes.FCONST_2);
        } else {
          yield new LdcInsnNode(value);
        }
      }
      case DOUBLE -> {
        var value = ir.doubleValue();
        if (value == 0.0) {
          yield new InsnNode(Opcodes.DCONST_0);
        } else if (value == 1.0) {
          yield new InsnNode(Opcodes.DCONST_1);
        } else {
          yield new LdcInsnNode(value);
        }
      }
      default -> new LdcInsnNode(rebuildConstantValue(ir));
    };
    insnList.add(insn);
  }
}

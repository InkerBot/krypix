package bot.inker.krypix.asm;

import bot.inker.krypix.AppView;
import bot.inker.krypix.KrypixField;
import bot.inker.krypix.KrypixMethod;
import bot.inker.krypix.common.attachment.AttachmentKey;
import bot.inker.krypix.ir.*;
import bot.inker.krypix.ir.array.IRArrayLength;
import bot.inker.krypix.ir.array.IRArrayLoad;
import bot.inker.krypix.ir.array.IRArrayStore;
import bot.inker.krypix.ir.array.IRNewArray;
import bot.inker.krypix.ir.branch.IRBranchIf;
import bot.inker.krypix.ir.branch.IRBranchSwitch;
import bot.inker.krypix.ir.branch.IRGoto;
import bot.inker.krypix.ir.document.IRDocument;
import bot.inker.krypix.ir.document.IRDocumentLineNumber;
import bot.inker.krypix.ir.field.IRFieldLoadResolved;
import bot.inker.krypix.ir.field.IRFieldLoadUnknown;
import bot.inker.krypix.ir.field.IRFieldStoreResolved;
import bot.inker.krypix.ir.field.IRFieldStoreUnknown;
import bot.inker.krypix.ir.insn.IRNop;
import bot.inker.krypix.ir.local.IRLocalLoad;
import bot.inker.krypix.ir.local.IRLocalStore;
import bot.inker.krypix.ir.method.IRInvokeDynamic;
import bot.inker.krypix.ir.method.IRInvokeMethod;
import bot.inker.krypix.ir.method.IRInvokeMethodResolved;
import bot.inker.krypix.ir.method.IRInvokeMethodUnknown;
import bot.inker.krypix.ir.monitor.IRMonitorEnter;
import bot.inker.krypix.ir.monitor.IRMonitorExit;
import bot.inker.krypix.ir.num.*;
import bot.inker.krypix.ir.ref.TypeRef;
import bot.inker.krypix.ir.terminatal.IRReturn;
import bot.inker.krypix.ir.terminatal.IRTerminatal;
import bot.inker.krypix.ir.terminatal.IRThrow;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class KrypixControlResolver {
  private static final AttachmentKey<LabelNode> SOURCE_LABEL = AttachmentKey.create("source-label");
  private static final Int2ObjectMap<BiConsumer<KrypixControlResolver, AbstractInsnNode>> RESOLVERS =
    new Int2ObjectAVLTreeMap<>();

  static {
    registerResolver(IRNop::new, Opcodes.NOP);
    registerResolver(KrypixControlResolver::resolveConst,
      Opcodes.ACONST_NULL, Opcodes.ICONST_M1, Opcodes.ICONST_0, Opcodes.ICONST_1, Opcodes.ICONST_2,
      Opcodes.ICONST_3, Opcodes.ICONST_4, Opcodes.ICONST_5, Opcodes.LCONST_0, Opcodes.LCONST_1,
      Opcodes.FCONST_0, Opcodes.FCONST_1, Opcodes.FCONST_2, Opcodes.DCONST_0, Opcodes.DCONST_1,
      Opcodes.BIPUSH, Opcodes.SIPUSH);
    registerResolver(KrypixControlResolver::resolveLdc, Opcodes.LDC);
    registerResolver(KrypixControlResolver::resolveVar,
      Opcodes.ILOAD, Opcodes.LLOAD, Opcodes.FLOAD, Opcodes.DLOAD, Opcodes.ALOAD,
      Opcodes.ISTORE, Opcodes.LSTORE, Opcodes.FSTORE, Opcodes.DSTORE, Opcodes.ASTORE);
    registerResolver(KrypixControlResolver::resolveArray,
      Opcodes.IALOAD, Opcodes.LALOAD, Opcodes.FALOAD, Opcodes.DALOAD, Opcodes.AALOAD,
      Opcodes.BALOAD, Opcodes.CALOAD, Opcodes.SALOAD, Opcodes.IASTORE, Opcodes.LASTORE,
      Opcodes.FASTORE, Opcodes.DASTORE, Opcodes.AASTORE, Opcodes.BASTORE, Opcodes.CASTORE, Opcodes.SASTORE);
    registerResolver(KrypixControlResolver::resolveStack,
      Opcodes.POP, Opcodes.POP2, Opcodes.DUP, Opcodes.DUP_X1, Opcodes.DUP_X2, Opcodes.DUP2, Opcodes.DUP2_X1,
      Opcodes.DUP2_X2, Opcodes.SWAP);
    registerResolver(KrypixControlResolver::resolveMath,
      Opcodes.IADD, Opcodes.LADD, Opcodes.FADD, Opcodes.DADD, Opcodes.ISUB, Opcodes.LSUB, Opcodes.FSUB,
      Opcodes.DSUB, Opcodes.IMUL, Opcodes.LMUL, Opcodes.FMUL, Opcodes.DMUL, Opcodes.IDIV, Opcodes.LDIV,
      Opcodes.FDIV, Opcodes.DDIV, Opcodes.IREM, Opcodes.LREM, Opcodes.FREM, Opcodes.DREM, Opcodes.INEG,
      Opcodes.LNEG, Opcodes.FNEG, Opcodes.DNEG, Opcodes.ISHL, Opcodes.LSHL, Opcodes.ISHR, Opcodes.LSHR,
      Opcodes.IUSHR, Opcodes.LUSHR, Opcodes.IAND, Opcodes.LAND, Opcodes.IOR, Opcodes.LOR, Opcodes.IXOR,
      Opcodes.LXOR);
    registerResolver(KrypixControlResolver::expandIinc, Opcodes.IINC);
    registerResolver(KrypixControlResolver::resolveCast,
      Opcodes.I2L, Opcodes.I2F, Opcodes.I2D, Opcodes.L2I, Opcodes.L2F, Opcodes.L2D, Opcodes.F2I, Opcodes.F2L,
      Opcodes.F2D, Opcodes.D2I, Opcodes.D2L, Opcodes.D2F, Opcodes.I2B, Opcodes.I2C, Opcodes.I2S);
    registerResolver(KrypixControlResolver::resolveCmp,
      Opcodes.LCMP, Opcodes.FCMPL, Opcodes.FCMPG, Opcodes.DCMPL, Opcodes.DCMPG);
    registerResolver(KrypixControlResolver::resolveIf,
      Opcodes.IFEQ, Opcodes.IFNE, Opcodes.IFLT, Opcodes.IFGE, Opcodes.IFGT, Opcodes.IFLE,
      Opcodes.IF_ICMPEQ, Opcodes.IF_ICMPNE, Opcodes.IF_ICMPLT, Opcodes.IF_ICMPGE, Opcodes.IF_ICMPGT, Opcodes.IF_ICMPLE,
      Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE, Opcodes.IFNULL, Opcodes.IFNONNULL);
    registerResolver(KrypixControlResolver::resolveGoto, Opcodes.GOTO);
    registerResolver(KrypixControlResolver::resolveSwitch,
      Opcodes.LOOKUPSWITCH, Opcodes.TABLESWITCH);
    registerResolver((resolver, insnNode) -> {
        throw new IllegalStateException("Unsupported opcode: JSR RET");
      },
      Opcodes.JSR, Opcodes.RET);
    registerResolver(KrypixControlResolver::resolveReturn,
      Opcodes.IRETURN, Opcodes.LRETURN, Opcodes.FRETURN, Opcodes.DRETURN, Opcodes.ARETURN, Opcodes.RETURN);
    registerResolver(KrypixControlResolver::resolveField,
      Opcodes.GETSTATIC, Opcodes.PUTSTATIC, Opcodes.GETFIELD, Opcodes.PUTFIELD);
    registerResolver(KrypixControlResolver::resolveInvoke,
      Opcodes.INVOKEVIRTUAL, Opcodes.INVOKESPECIAL, Opcodes.INVOKESTATIC, Opcodes.INVOKEINTERFACE);
    registerResolver(KrypixControlResolver::resolveDynamic, Opcodes.INVOKEDYNAMIC);
    registerResolver(KrypixControlResolver::resolveNew, Opcodes.NEW);
    registerResolver(KrypixControlResolver::resolveNewArray,
      Opcodes.NEWARRAY, Opcodes.ANEWARRAY, Opcodes.MULTIANEWARRAY);
    registerResolver(IRArrayLength::new, Opcodes.ARRAYLENGTH);
    registerResolver(KrypixControlResolver::resolveCheckcast, Opcodes.CHECKCAST);
    registerResolver(KrypixControlResolver::resolveInstanceof, Opcodes.INSTANCEOF);
    registerResolver(IRMonitorEnter::new, Opcodes.MONITORENTER);
    registerResolver(IRMonitorExit::new, Opcodes.MONITOREXIT);
    registerResolver(KrypixControlResolver::resolveThrow, Opcodes.ATHROW);
  }

  private final AppView appView;
  private final KrypixMethod method;

  private final MethodBody methodBody = new MethodBody();
  private final Map<LabelNode, CodeBlock> labelToBlock = new HashMap<>();
  private final Map<LabelNode, List<CodeBlock>> labelEffectiveBlock = new HashMap<>();

  private CodeBlock currentBlock;
  private LabelNode latestLabel;

  public KrypixControlResolver(AppView appView, KrypixMethod method) {
    this.appView = appView;
    this.method = method;
  }

  private static void registerResolver(BiConsumer<KrypixControlResolver, AbstractInsnNode> resolver, int opcode) {
    RESOLVERS.put(opcode, resolver);
  }

  private static void registerResolver(Supplier<IRAbstract> factory, int opcode) {
    RESOLVERS.put(opcode, (resolver, instruction) -> resolver.addCode(factory.get()));
  }

  private static void registerResolver(BiConsumer<KrypixControlResolver, AbstractInsnNode> resolver, int... opcodes) {
    for (int opcode : opcodes) registerResolver(resolver, opcode);
  }

  private CodeBlock createCodeBlock(LabelNode label) {
    CodeBlock codeBlock = new CodeBlock();
    methodBody.codeBlocks().add(codeBlock);
    if (label != null) {
      codeBlock.setAttachment(SOURCE_LABEL, label);
      labelToBlock.put(label, codeBlock);
      labelEffectiveBlock.computeIfAbsent(label, k -> new ArrayList<>()).add(codeBlock);
    }

    if (methodBody.entryBlock() == null) {
      methodBody.entryBlock(codeBlock);
    }

    return codeBlock;
  }

  private CodeBlock createCodeBlock() {
    return createCodeBlock(null);
  }

  private CodeBlock requireBlock(LabelNode label) {
    return Optional.ofNullable(labelToBlock.get(label))
      .orElseThrow(() -> new IllegalStateException("No block found for label " + label));
  }

  private void createInitialCodeBlocks() {
    CodeBlock entryBlock = null;
    for (AbstractInsnNode instruction : method.methodNode().instructions) {
      if (entryBlock == null) {
        entryBlock = (instruction instanceof LabelNode labelNode)
          ? createCodeBlock(labelNode)
          : createCodeBlock();
      } else if (instruction instanceof LabelNode labelNode) {
        entryBlock = createCodeBlock(labelNode);
      }
    }

    if (entryBlock == null) {
      throw new IllegalStateException("No code found in method " + method);
    }

    methodBody.entryBlock(entryBlock);
  }

  private void addCode(IRAbstract ir) {
    if (currentBlock.terminatal() != null && !(ir instanceof IRDocument)) {
      throw new IllegalArgumentException("Cannot add code after terminatl");
    }
    currentBlock.addCode(ir);
  }

  private void resolveDocument(AbstractInsnNode instruction) {
    if (instruction instanceof LineNumberNode lineNumberNode) {
      for (CodeBlock codeBlock : labelEffectiveBlock.getOrDefault(lineNumberNode.start, Collections.emptyList())) {
        codeBlock.setAttachment(CodeBlockAttachments.LINE_NUMBER, lineNumberNode.line);
      }
      addCode(new IRDocumentLineNumber(requireBlock(lineNumberNode.start), lineNumberNode.line));
    }
  }

  private void resolveConst(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof InsnNode || insnNode instanceof IntInsnNode,
      "Expected InsnNode or IntInsnNode, got %s", insnNode);

    switch (insnNode.getOpcode()) {
      case Opcodes.ACONST_NULL -> addCode(IRConst.createNull());
      case Opcodes.ICONST_M1, Opcodes.ICONST_0, Opcodes.ICONST_1, Opcodes.ICONST_2, Opcodes.ICONST_3, Opcodes.ICONST_4,
           Opcodes.ICONST_5 -> addCode(IRConst.createInt(insnNode.getOpcode() - Opcodes.ICONST_0));
      case Opcodes.LCONST_0, Opcodes.LCONST_1 -> addCode(IRConst.createLong(insnNode.getOpcode() - Opcodes.LCONST_0));
      case Opcodes.FCONST_0 -> addCode(IRConst.createFloat(0.0F));
      case Opcodes.FCONST_1 -> addCode(IRConst.createFloat(1.0F));
      case Opcodes.FCONST_2 -> addCode(IRConst.createFloat(2.0F));
      case Opcodes.DCONST_0 -> addCode(IRConst.createDouble(0.0D));
      case Opcodes.DCONST_1 -> addCode(IRConst.createDouble(1.0D));
      case Opcodes.BIPUSH, Opcodes.SIPUSH -> addCode(IRConst.createInt(((IntInsnNode) insnNode).operand));
      default -> throw new IllegalArgumentException("Unexpected opcode: " + insnNode.getOpcode());
    }
  }

  private IRConst mapConst(Object cst) {
    if (cst instanceof ConstantDynamic) {
      throw new IllegalStateException("ConstantDynamic not supported");
    } else if (cst instanceof Type asmType) {
      if (asmType.getSort() == Type.METHOD) {
        return IRConst.createMethodType(appView.parseMethodTypeRef(asmType.getDescriptor()));
      } else {
        return IRConst.createType(appView.parseTypeRef(asmType.getDescriptor()));
      }
    } else {
      return IRConst.create(cst);
    }
  }

  private void resolveLdc(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof LdcInsnNode, "Expected LdcInsnNode, got %s", insnNode);

    LdcInsnNode ldcInsnNode = (LdcInsnNode) insnNode;
    if (ldcInsnNode.cst instanceof ConstantDynamic) {
      // TODO: Resolve constant dynamic
      addCode(new IRUnresolved(new LdcInsnNode(ldcInsnNode.cst)));
    } else {
      addCode(mapConst(ldcInsnNode.cst));
    }
  }

  private void resolveVar(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof VarInsnNode, "Expected VarInsnNode, got %s", insnNode);

    VarInsnNode varInsnNode = (VarInsnNode) insnNode;
    switch (insnNode.getOpcode()) {
      case Opcodes.ILOAD -> addCode(new IRLocalLoad(BaseFrameType.INT, varInsnNode.var));
      case Opcodes.LLOAD -> addCode(new IRLocalLoad(BaseFrameType.LONG, varInsnNode.var));
      case Opcodes.FLOAD -> addCode(new IRLocalLoad(BaseFrameType.FLOAT, varInsnNode.var));
      case Opcodes.DLOAD -> addCode(new IRLocalLoad(BaseFrameType.DOUBLE, varInsnNode.var));
      case Opcodes.ALOAD -> addCode(new IRLocalLoad(BaseFrameType.OBJECT, varInsnNode.var));
      case Opcodes.ISTORE -> addCode(new IRLocalStore(BaseFrameType.INT, varInsnNode.var));
      case Opcodes.LSTORE -> addCode(new IRLocalStore(BaseFrameType.LONG, varInsnNode.var));
      case Opcodes.FSTORE -> addCode(new IRLocalStore(BaseFrameType.FLOAT, varInsnNode.var));
      case Opcodes.DSTORE -> addCode(new IRLocalStore(BaseFrameType.DOUBLE, varInsnNode.var));
      case Opcodes.ASTORE -> addCode(new IRLocalStore(BaseFrameType.OBJECT, varInsnNode.var));
      default -> throw new IllegalArgumentException("Unexpected opcode: " + insnNode.getOpcode());
    }
  }

  private void resolveArray(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof InsnNode, "Expected InsnNode, got %s", insnNode);

    switch (insnNode.getOpcode()) {
      case Opcodes.IALOAD -> addCode(new IRArrayLoad(BaseValueType.INT));
      case Opcodes.LALOAD -> addCode(new IRArrayLoad(BaseValueType.LONG));
      case Opcodes.FALOAD -> addCode(new IRArrayLoad(BaseValueType.FLOAT));
      case Opcodes.DALOAD -> addCode(new IRArrayLoad(BaseValueType.DOUBLE));
      case Opcodes.AALOAD -> addCode(new IRArrayLoad(BaseValueType.OBJECT));
      case Opcodes.BALOAD -> addCode(new IRArrayLoad(BaseValueType.BYTE));
      case Opcodes.CALOAD -> addCode(new IRArrayLoad(BaseValueType.CHAR));
      case Opcodes.SALOAD -> addCode(new IRArrayLoad(BaseValueType.SHORT));
      case Opcodes.IASTORE -> addCode(new IRArrayStore(BaseValueType.INT));
      case Opcodes.LASTORE -> addCode(new IRArrayStore(BaseValueType.LONG));
      case Opcodes.FASTORE -> addCode(new IRArrayStore(BaseValueType.FLOAT));
      case Opcodes.DASTORE -> addCode(new IRArrayStore(BaseValueType.DOUBLE));
      case Opcodes.AASTORE -> addCode(new IRArrayStore(BaseValueType.OBJECT));
      case Opcodes.BASTORE -> addCode(new IRArrayStore(BaseValueType.BYTE));
      case Opcodes.CASTORE -> addCode(new IRArrayStore(BaseValueType.CHAR));
      case Opcodes.SASTORE -> addCode(new IRArrayStore(BaseValueType.SHORT));
      default -> throw new IllegalArgumentException("Unexpected opcode: " + insnNode.getOpcode());
    }
  }

  private void resolveStack(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof InsnNode, "Expected InsnNode, got %s", insnNode);

    addCode(IRStackOperator.fromOpcode(insnNode.getOpcode()));
  }

  private void resolveMath(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof InsnNode, "Expected InsnNode, got %s", insnNode);

    switch (insnNode.getOpcode()) {
      case Opcodes.IADD -> addCode(new IRMathBinary(BaseFrameType.INT, IRMathBinary.Operation.ADD));
      case Opcodes.LADD -> addCode(new IRMathBinary(BaseFrameType.LONG, IRMathBinary.Operation.ADD));
      case Opcodes.FADD -> addCode(new IRMathBinary(BaseFrameType.FLOAT, IRMathBinary.Operation.ADD));
      case Opcodes.DADD -> addCode(new IRMathBinary(BaseFrameType.DOUBLE, IRMathBinary.Operation.ADD));
      case Opcodes.ISUB -> addCode(new IRMathBinary(BaseFrameType.INT, IRMathBinary.Operation.SUB));
      case Opcodes.LSUB -> addCode(new IRMathBinary(BaseFrameType.LONG, IRMathBinary.Operation.SUB));
      case Opcodes.FSUB -> addCode(new IRMathBinary(BaseFrameType.FLOAT, IRMathBinary.Operation.SUB));
      case Opcodes.DSUB -> addCode(new IRMathBinary(BaseFrameType.DOUBLE, IRMathBinary.Operation.SUB));
      case Opcodes.IMUL -> addCode(new IRMathBinary(BaseFrameType.INT, IRMathBinary.Operation.MUL));
      case Opcodes.LMUL -> addCode(new IRMathBinary(BaseFrameType.LONG, IRMathBinary.Operation.MUL));
      case Opcodes.FMUL -> addCode(new IRMathBinary(BaseFrameType.FLOAT, IRMathBinary.Operation.MUL));
      case Opcodes.DMUL -> addCode(new IRMathBinary(BaseFrameType.DOUBLE, IRMathBinary.Operation.MUL));
      case Opcodes.IDIV -> addCode(new IRMathBinary(BaseFrameType.INT, IRMathBinary.Operation.DIV));
      case Opcodes.LDIV -> addCode(new IRMathBinary(BaseFrameType.LONG, IRMathBinary.Operation.DIV));
      case Opcodes.FDIV -> addCode(new IRMathBinary(BaseFrameType.FLOAT, IRMathBinary.Operation.DIV));
      case Opcodes.DDIV -> addCode(new IRMathBinary(BaseFrameType.DOUBLE, IRMathBinary.Operation.DIV));
      case Opcodes.IREM -> addCode(new IRMathBinary(BaseFrameType.INT, IRMathBinary.Operation.REM));
      case Opcodes.LREM -> addCode(new IRMathBinary(BaseFrameType.LONG, IRMathBinary.Operation.REM));
      case Opcodes.FREM -> addCode(new IRMathBinary(BaseFrameType.FLOAT, IRMathBinary.Operation.REM));
      case Opcodes.DREM -> addCode(new IRMathBinary(BaseFrameType.DOUBLE, IRMathBinary.Operation.REM));
      case Opcodes.INEG -> addCode(new IRMathUnary(BaseFrameType.INT, IRMathUnary.Operation.NEG));
      case Opcodes.LNEG -> addCode(new IRMathUnary(BaseFrameType.LONG, IRMathUnary.Operation.NEG));
      case Opcodes.FNEG -> addCode(new IRMathUnary(BaseFrameType.FLOAT, IRMathUnary.Operation.NEG));
      case Opcodes.DNEG -> addCode(new IRMathUnary(BaseFrameType.DOUBLE, IRMathUnary.Operation.NEG));
      case Opcodes.ISHL -> addCode(new IRMathBitwise(BaseFrameType.INT, IRMathBitwise.Operation.SHL));
      case Opcodes.LSHL -> addCode(new IRMathBitwise(BaseFrameType.LONG, IRMathBitwise.Operation.SHL));
      case Opcodes.ISHR -> addCode(new IRMathBitwise(BaseFrameType.INT, IRMathBitwise.Operation.SHR));
      case Opcodes.LSHR -> addCode(new IRMathBitwise(BaseFrameType.LONG, IRMathBitwise.Operation.SHR));
      case Opcodes.IUSHR -> addCode(new IRMathBitwise(BaseFrameType.INT, IRMathBitwise.Operation.USHR));
      case Opcodes.LUSHR -> addCode(new IRMathBitwise(BaseFrameType.LONG, IRMathBitwise.Operation.USHR));
      case Opcodes.IAND -> addCode(new IRMathBitwise(BaseFrameType.INT, IRMathBitwise.Operation.AND));
      case Opcodes.LAND -> addCode(new IRMathBitwise(BaseFrameType.LONG, IRMathBitwise.Operation.AND));
      case Opcodes.IOR -> addCode(new IRMathBitwise(BaseFrameType.INT, IRMathBitwise.Operation.OR));
      case Opcodes.LOR -> addCode(new IRMathBitwise(BaseFrameType.LONG, IRMathBitwise.Operation.OR));
      case Opcodes.IXOR -> addCode(new IRMathBitwise(BaseFrameType.INT, IRMathBitwise.Operation.XOR));
      case Opcodes.LXOR -> addCode(new IRMathBitwise(BaseFrameType.LONG, IRMathBitwise.Operation.XOR));
      default -> throw new IllegalArgumentException("Unexpected opcode: " + insnNode.getOpcode());
    }
  }

  private void expandIinc(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof IincInsnNode, "Expected IincInsnNode, got %s", insnNode);

    IincInsnNode iincInsnNode = (IincInsnNode) insnNode;
    addCode(new IRLocalLoad(BaseFrameType.INT, iincInsnNode.var));
    addCode(IRConst.createInt(iincInsnNode.incr));
    addCode(new IRMathBinary(BaseFrameType.INT, IRMathBinary.Operation.ADD));
    addCode(new IRLocalStore(BaseFrameType.INT, iincInsnNode.var));
  }

  private void resolveCast(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof InsnNode, "Expected InsnNode, got %s", insnNode);

    switch (insnNode.getOpcode()) {
      case Opcodes.I2L -> addCode(new IRMathCast(BaseFrameType.INT, BaseValueType.LONG));
      case Opcodes.I2F -> addCode(new IRMathCast(BaseFrameType.INT, BaseValueType.FLOAT));
      case Opcodes.I2D -> addCode(new IRMathCast(BaseFrameType.INT, BaseValueType.DOUBLE));
      case Opcodes.L2I -> addCode(new IRMathCast(BaseFrameType.LONG, BaseValueType.INT));
      case Opcodes.L2F -> addCode(new IRMathCast(BaseFrameType.LONG, BaseValueType.FLOAT));
      case Opcodes.L2D -> addCode(new IRMathCast(BaseFrameType.LONG, BaseValueType.DOUBLE));
      case Opcodes.F2I -> addCode(new IRMathCast(BaseFrameType.FLOAT, BaseValueType.INT));
      case Opcodes.F2L -> addCode(new IRMathCast(BaseFrameType.FLOAT, BaseValueType.LONG));
      case Opcodes.F2D -> addCode(new IRMathCast(BaseFrameType.FLOAT, BaseValueType.DOUBLE));
      case Opcodes.D2I -> addCode(new IRMathCast(BaseFrameType.DOUBLE, BaseValueType.INT));
      case Opcodes.D2L -> addCode(new IRMathCast(BaseFrameType.DOUBLE, BaseValueType.LONG));
      case Opcodes.D2F -> addCode(new IRMathCast(BaseFrameType.DOUBLE, BaseValueType.FLOAT));
      case Opcodes.I2B -> addCode(new IRMathCast(BaseFrameType.INT, BaseValueType.BYTE));
      case Opcodes.I2C -> addCode(new IRMathCast(BaseFrameType.INT, BaseValueType.CHAR));
      case Opcodes.I2S -> addCode(new IRMathCast(BaseFrameType.INT, BaseValueType.SHORT));
      default -> throw new IllegalArgumentException("Unexpected opcode: " + insnNode.getOpcode());
    }
  }

  private void resolveCmp(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof InsnNode, "Expected InsnNode, got %s", insnNode);

    switch (insnNode.getOpcode()) {
      case Opcodes.LCMP -> addCode(new IRMathCmp(BaseFrameType.LONG, IRMathCmp.Operation.CMP));
      case Opcodes.FCMPL -> addCode(new IRMathCmp(BaseFrameType.FLOAT, IRMathCmp.Operation.CMPL));
      case Opcodes.FCMPG -> addCode(new IRMathCmp(BaseFrameType.FLOAT, IRMathCmp.Operation.CMPG));
      case Opcodes.DCMPL -> addCode(new IRMathCmp(BaseFrameType.DOUBLE, IRMathCmp.Operation.CMPL));
      case Opcodes.DCMPG -> addCode(new IRMathCmp(BaseFrameType.DOUBLE, IRMathCmp.Operation.CMPG));
      default -> throw new IllegalArgumentException("Unexpected opcode: " + insnNode.getOpcode());
    }
  }

  private void resolveIf(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof JumpInsnNode, "Expected JumpInsnNode, got %s", insnNode);

    CodeBlock previousBlock = currentBlock;
    currentBlock = createCodeBlock();
    if (latestLabel != null) {
      labelEffectiveBlock.computeIfAbsent(latestLabel, k -> new ArrayList<>()).add(currentBlock);
    }
    CodeBlock targetBlock = requireBlock(((JumpInsnNode) insnNode).label);

    IRTerminatal terminatal;

    switch (insnNode.getOpcode()) {
      case Opcodes.IFEQ -> terminatal = new IRBranchIf(IRBranchIf.Operator.EQ, currentBlock, targetBlock);
      case Opcodes.IFNE -> terminatal = new IRBranchIf(IRBranchIf.Operator.NE, currentBlock, targetBlock);
      case Opcodes.IFLT -> terminatal = new IRBranchIf(IRBranchIf.Operator.LT, currentBlock, targetBlock);
      case Opcodes.IFGE -> terminatal = new IRBranchIf(IRBranchIf.Operator.GE, currentBlock, targetBlock);
      case Opcodes.IFGT -> terminatal = new IRBranchIf(IRBranchIf.Operator.GT, currentBlock, targetBlock);
      case Opcodes.IFLE -> terminatal = new IRBranchIf(IRBranchIf.Operator.LE, currentBlock, targetBlock);
      case Opcodes.IF_ICMPEQ -> terminatal = new IRBranchIf(IRBranchIf.Operator.ICMP_EQ, currentBlock, targetBlock);
      case Opcodes.IF_ICMPNE -> terminatal = new IRBranchIf(IRBranchIf.Operator.ICMP_NE, currentBlock, targetBlock);
      case Opcodes.IF_ICMPLT -> terminatal = new IRBranchIf(IRBranchIf.Operator.ICMP_LT, currentBlock, targetBlock);
      case Opcodes.IF_ICMPGE -> terminatal = new IRBranchIf(IRBranchIf.Operator.ICMP_GE, currentBlock, targetBlock);
      case Opcodes.IF_ICMPGT -> terminatal = new IRBranchIf(IRBranchIf.Operator.ICMP_GT, currentBlock, targetBlock);
      case Opcodes.IF_ICMPLE -> terminatal = new IRBranchIf(IRBranchIf.Operator.ICMP_LE, currentBlock, targetBlock);
      case Opcodes.IF_ACMPEQ -> terminatal = new IRBranchIf(IRBranchIf.Operator.ACMP_EQ, currentBlock, targetBlock);
      case Opcodes.IF_ACMPNE -> terminatal = new IRBranchIf(IRBranchIf.Operator.ACMP_NE, currentBlock, targetBlock);
      case Opcodes.IFNULL -> terminatal = new IRBranchIf(IRBranchIf.Operator.NULL, currentBlock, targetBlock);
      case Opcodes.IFNONNULL -> terminatal = new IRBranchIf(IRBranchIf.Operator.NONNULL, currentBlock, targetBlock);
      default -> throw new IllegalArgumentException("Unexpected opcode: " + insnNode.getOpcode());
    }

    previousBlock.terminatal(terminatal);
  }

  private void resolveGoto(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof JumpInsnNode, "Expected JumpInsnNode, got %s", insnNode);

    JumpInsnNode jumpInsnNode = (JumpInsnNode) insnNode;
    currentBlock.terminatal(new IRGoto(requireBlock(jumpInsnNode.label)));
  }

  private void resolveSwitch(AbstractInsnNode insnNode) {
    Preconditions.checkArgument((insnNode instanceof LookupSwitchInsnNode || insnNode instanceof TableSwitchInsnNode),
      "Expected LookupSwitchInsnNode or TableSwitchInsnNode, got %s", insnNode);

    var builder = new IRBranchSwitch.Builder();
    if (insnNode instanceof LookupSwitchInsnNode lookupSwitchInsnNode) {

      for (int i = 0; i < lookupSwitchInsnNode.keys.size(); i++) {
        CodeBlock targetBlock = requireBlock(lookupSwitchInsnNode.labels.get(i));
        builder.addBranch(lookupSwitchInsnNode.keys.get(i), targetBlock);
      }
      builder.defaultBranch(requireBlock(lookupSwitchInsnNode.dflt));
    } else {
      TableSwitchInsnNode tableSwitchInsnNode = (TableSwitchInsnNode) insnNode;

      for (int i = 0; i < tableSwitchInsnNode.labels.size(); i++) {
        CodeBlock targetBlock = requireBlock(tableSwitchInsnNode.labels.get(i));
        builder.addBranch(tableSwitchInsnNode.min + i, targetBlock);
      }
      builder.defaultBranch(requireBlock(tableSwitchInsnNode.dflt));
    }

    currentBlock.terminatal(builder.build());
  }

  private void resolveReturn(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof InsnNode, "Expected InsnNode, got %s", insnNode);

    switch (insnNode.getOpcode()) {
      case Opcodes.IRETURN -> currentBlock.terminatal(new IRReturn(BaseFrameType.INT));
      case Opcodes.LRETURN -> currentBlock.terminatal(new IRReturn(BaseFrameType.LONG));
      case Opcodes.FRETURN -> currentBlock.terminatal(new IRReturn(BaseFrameType.FLOAT));
      case Opcodes.DRETURN -> currentBlock.terminatal(new IRReturn(BaseFrameType.DOUBLE));
      case Opcodes.ARETURN -> currentBlock.terminatal(new IRReturn(BaseFrameType.OBJECT));
      case Opcodes.RETURN -> currentBlock.terminatal(new IRReturn(null));
      default -> throw new IllegalArgumentException("Unexpected opcode: " + insnNode.getOpcode());
    }
  }

  private void resolveField(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof FieldInsnNode, "Expected FieldInsnNode, got %s", insnNode);

    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNode;
    @Nullable KrypixField krypixField = appView.getClass(fieldInsnNode.owner)
      .flatMap(clazz -> clazz.fields().stream()
        .filter(it -> fieldInsnNode.name.equals(it.name()) && fieldInsnNode.desc.equals(it.desc()))
        .findFirst())
      .orElse(null);
    boolean isStatic = (insnNode.getOpcode() == Opcodes.GETSTATIC || insnNode.getOpcode() == Opcodes.PUTSTATIC);

    if (krypixField != null && krypixField.isStatic() != isStatic) {
      throw new IllegalStateException("Field " + fieldInsnNode.name + " is not " + (isStatic ? "static" : "instance"));
    }

    switch (insnNode.getOpcode()) {
      case Opcodes.GETSTATIC, Opcodes.GETFIELD -> addCode(krypixField == null
        ? new IRFieldLoadUnknown(isStatic, fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc)
        : new IRFieldLoadResolved(krypixField));
      case Opcodes.PUTSTATIC, Opcodes.PUTFIELD -> addCode(krypixField == null
        ? new IRFieldStoreUnknown(isStatic, fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc)
        : new IRFieldStoreResolved(krypixField));
      default -> throw new IllegalArgumentException("Unexpected opcode: " + insnNode.getOpcode());
    }
  }

  private void resolveInvoke(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof MethodInsnNode, "Expected MethodInsnNode, got %s", insnNode);

    MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
    @Nullable KrypixMethod krypixMethod = appView.methodLocator()
      .owner(methodInsnNode.owner)
      .name(methodInsnNode.name)
      .desc(methodInsnNode.desc)
      .exactOwner(false)
      .firstOrNull();

    switch (insnNode.getOpcode()) {
      case Opcodes.INVOKESTATIC -> addCode((krypixMethod == null)
        ? new IRInvokeMethodUnknown(IRInvokeMethod.Type.STATIC, methodInsnNode.owner, methodInsnNode.name, appView.parseMethodTypeRef(methodInsnNode.desc))
        : new IRInvokeMethodResolved(IRInvokeMethod.Type.STATIC, krypixMethod));
      case Opcodes.INVOKESPECIAL -> addCode((krypixMethod == null)
        ? new IRInvokeMethodUnknown(IRInvokeMethod.Type.SPECIAL, methodInsnNode.owner, methodInsnNode.name, appView.parseMethodTypeRef(methodInsnNode.desc))
        : new IRInvokeMethodResolved(IRInvokeMethod.Type.SPECIAL, krypixMethod));
      case Opcodes.INVOKEVIRTUAL -> addCode((krypixMethod == null)
        ? new IRInvokeMethodUnknown(IRInvokeMethod.Type.VIRTUAL, methodInsnNode.owner, methodInsnNode.name, appView.parseMethodTypeRef(methodInsnNode.desc))
        : new IRInvokeMethodResolved(IRInvokeMethod.Type.VIRTUAL, krypixMethod));
      case Opcodes.INVOKEINTERFACE -> addCode((krypixMethod == null)
        ? new IRInvokeMethodUnknown(IRInvokeMethod.Type.INTERFACE, methodInsnNode.owner, methodInsnNode.name, appView.parseMethodTypeRef(methodInsnNode.desc))
        : new IRInvokeMethodResolved(IRInvokeMethod.Type.INTERFACE, krypixMethod));
      default -> throw new IllegalArgumentException("Unexpected opcode: " + insnNode.getOpcode());
    }
  }

  private void resolveDynamic(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof InvokeDynamicInsnNode, "Expected InvokeDynamicInsnNode, got %s", insnNode);

    InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) insnNode;
    addCode(new IRInvokeDynamic(
      invokeDynamicInsnNode.name,
      appView.parseMethodTypeRef(invokeDynamicInsnNode.desc),
      invokeDynamicInsnNode.bsm,
      Arrays.stream(invokeDynamicInsnNode.bsmArgs).map(this::mapConst).toArray(IRConst[]::new)
    ));
  }

  private void resolveNew(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof TypeInsnNode, "Expected TypeInsnNode, got %s", insnNode);

    TypeInsnNode typeInsnNode = (TypeInsnNode) insnNode;
    addCode(new IRNew(appView.parseTypeRef("L" + typeInsnNode.desc + ";")));
  }

  private void resolveNewArray(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof IntInsnNode || insnNode instanceof TypeInsnNode || insnNode instanceof MultiANewArrayInsnNode,
      "Expected IntInsnNode, got %s", insnNode);

    TypeRef elementType = switch (insnNode.getOpcode()) {
      case Opcodes.NEWARRAY -> switch (((IntInsnNode) insnNode).operand) {
        case Opcodes.T_BOOLEAN -> TypeRef.ofBoolean();
        case Opcodes.T_CHAR -> TypeRef.ofChar();
        case Opcodes.T_BYTE -> TypeRef.ofByte();
        case Opcodes.T_SHORT -> TypeRef.ofShort();
        case Opcodes.T_INT -> TypeRef.ofInt();
        case Opcodes.T_LONG -> TypeRef.ofLong();
        case Opcodes.T_FLOAT -> TypeRef.ofFloat();
        case Opcodes.T_DOUBLE -> TypeRef.ofDouble();
        default -> throw new IllegalArgumentException("Unexpected type: " + ((IntInsnNode) insnNode).operand);
      };
      case Opcodes.ANEWARRAY -> appView.parseTypeRef("L" + ((TypeInsnNode) insnNode).desc + ";");
      case Opcodes.MULTIANEWARRAY -> appView.parseTypeRef("L" + ((MultiANewArrayInsnNode) insnNode).desc + ";");
      default -> throw new IllegalStateException("Unexpected opcode: " + insnNode.getOpcode());
    };

    int dimensions = (insnNode.getOpcode() == Opcodes.MULTIANEWARRAY)
      ? ((MultiANewArrayInsnNode) insnNode).dims
      : 1;

    addCode(new IRNewArray(elementType, dimensions));
  }

  private void resolveCheckcast(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof TypeInsnNode, "Expected TypeInsnNode, got %s", insnNode);

    TypeInsnNode typeInsnNode = (TypeInsnNode) insnNode;
    addCode(new IRCheckCast(appView.parseTypeRef("L" + typeInsnNode.desc + ";")));
  }

  private void resolveInstanceof(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof TypeInsnNode, "Expected TypeInsnNode, got %s", insnNode);

    TypeInsnNode typeInsnNode = (TypeInsnNode) insnNode;
    addCode(new IRInstanceOf(appView.parseTypeRef("L" + typeInsnNode.desc + ";")));
  }

  private void resolveThrow(AbstractInsnNode insnNode) {
    Preconditions.checkArgument(insnNode instanceof InsnNode, "Expected InsnNode, got %s", insnNode);

    currentBlock.terminatal(new IRThrow());
  }

  private void resolveUnknown(AbstractInsnNode insnNode) {
    addCode(new IRUnresolved(insnNode));
  }

  public void resolve() {
    createInitialCodeBlocks();

    currentBlock = null;
    latestLabel = null;
    for (AbstractInsnNode instruction : method.methodNode().instructions) {
      if (instruction instanceof LabelNode labelNode) {
        currentBlock = labelToBlock.get(labelNode);
        latestLabel = labelNode;
        continue;
      } else if (currentBlock == null) {
        currentBlock = methodBody.entryBlock();
      }

      if (instruction.getOpcode() < 0) {
        resolveDocument(instruction);
        continue;
      }

      RESOLVERS.getOrDefault(instruction.getOpcode(), KrypixControlResolver::resolveUnknown)
        .accept(this, instruction);
    }
  }
}

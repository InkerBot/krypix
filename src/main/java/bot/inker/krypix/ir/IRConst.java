package bot.inker.krypix.ir;

import bot.inker.krypix.ir.handle.IRConstantDynamic;
import bot.inker.krypix.ir.handle.IRHandle;
import bot.inker.krypix.ir.ref.MethodType;
import bot.inker.krypix.ir.ref.TypeRef;
import com.google.common.base.Preconditions;

public final class IRConst implements IRAbstract {
  private final Type type;
  private final Object value;

  public IRConst(Type type, Object value) {
    this.type = type;
    this.value = value;
  }

  public static IRConst createNull() {
    return new IRConst(Type.NULL, null);
  }

  public static IRConst createBoolean(boolean value) {
    return new IRConst(Type.INT, value);
  }

  public static IRConst createByte(byte value) {
    return new IRConst(Type.INT, value);
  }

  public static IRConst createChar(char value) {
    return new IRConst(Type.INT, value);
  }

  public static IRConst createInt(int value) {
    return new IRConst(Type.INT, value);
  }

  public static IRConst createFloat(float value) {
    return new IRConst(Type.FLOAT, value);
  }

  public static IRConst createLong(long value) {
    return new IRConst(Type.LONG, value);
  }

  public static IRConst createDouble(double value) {
    return new IRConst(Type.DOUBLE, value);
  }

  public static IRConst createString(String value) {
    return new IRConst(Type.STRING, value);
  }

  public static IRConst createType(TypeRef value) {
    return new IRConst(Type.TYPE, value);
  }

  public static IRConst createMethodType(MethodType value) {
    return new IRConst(Type.METHOD_TYPE, value);
  }

  public static IRConst createHandle(IRHandle value) {
    return new IRConst(Type.HANDLE, value);
  }

  public static IRConst createDynamic(IRConstantDynamic value) {
    return new IRConst(Type.DYNAMIC, value);
  }

  public static IRConst create(Object value) {
    if (value == null) {
      return createNull();
    } else if (value instanceof Boolean) {
      return createBoolean((boolean) value);
    } else if (value instanceof Byte) {
      return createByte((byte) value);
    } else if (value instanceof Character) {
      return createChar((char) value);
    } else if (value instanceof Integer) {
      return createInt((int) value);
    } else if (value instanceof Float) {
      return createFloat((float) value);
    } else if (value instanceof Long) {
      return createLong((long) value);
    } else if (value instanceof Double) {
      return createDouble((double) value);
    } else if (value instanceof String) {
      return createString((String) value);
    } else if (value instanceof TypeRef) {
      return createType((TypeRef) value);
    } else if (value instanceof MethodType) {
      return createMethodType((MethodType) value);
    } else if (value instanceof IRHandle) {
      return createHandle((IRHandle) value);
    } else if (value instanceof IRConstantDynamic) {
      return createDynamic((IRConstantDynamic) value);
    } else {
      throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
    }
  }

  public Type type() {
    return type;
  }

  public BaseFrameType baseType() {
    if (type == Type.DYNAMIC) {
      return ((IRConstantDynamic) value).desc().asFrameType();
    }
    return type.baseType;
  }

  public Object value() {
    return value;
  }

  public boolean booleanValue() {
    Preconditions.checkState(type == Type.INT, "Expected type INT, got %s", type);
    return (boolean) value;
  }

  public byte byteValue() {
    Preconditions.checkState(type == Type.INT, "Expected type INT, got %s", type);
    return (byte) value;
  }

  public char charValue() {
    Preconditions.checkState(type == Type.INT, "Expected type INT, got %s", type);
    return (char) value;
  }

  public int intValue() {
    Preconditions.checkState(type == Type.INT, "Expected type INT, got %s", type);
    return (int) value;
  }

  public float floatValue() {
    Preconditions.checkState(type == Type.FLOAT, "Expected type FLOAT, got %s", type);
    return (float) value;
  }

  public long longValue() {
    Preconditions.checkState(type == Type.LONG, "Expected type LONG, got %s", type);
    return (long) value;
  }

  public double doubleValue() {
    Preconditions.checkState(type == Type.DOUBLE, "Expected type DOUBLE, got %s", type);
    return (double) value;
  }

  public String stringValue() {
    Preconditions.checkState(type == Type.STRING, "Expected type STRING, got %s", type);
    return (String) value;
  }

  public TypeRef typeValue() {
    Preconditions.checkState(type == Type.TYPE, "Expected type TYPE, got %s", type);
    return (TypeRef) value;
  }

  public MethodType methodTypeValue() {
    Preconditions.checkState(type == Type.METHOD_TYPE, "Expected type METHOD_TYPE, got %s", type);
    return (MethodType) value;
  }

  public IRHandle handleValue() {
    Preconditions.checkState(type == Type.HANDLE, "Expected type HANDLE, got %s", type);
    return (IRHandle) value;
  }

  public IRConstantDynamic dynamicValue() {
    Preconditions.checkState(type == Type.DYNAMIC, "Expected type DYNAMIC, got %s", type);
    return (IRConstantDynamic) value;
  }

  @Override
  public String toString() {
    return "const " + type.name().toLowerCase() + " " + value;
  }

  public enum Type {
    NULL(BaseFrameType.OBJECT), INT(BaseFrameType.INT), FLOAT(BaseFrameType.FLOAT), LONG(BaseFrameType.LONG),
    DOUBLE(BaseFrameType.DOUBLE), STRING(BaseFrameType.OBJECT), TYPE(BaseFrameType.OBJECT),
    METHOD_TYPE(BaseFrameType.OBJECT), HANDLE(BaseFrameType.OBJECT), DYNAMIC(null);

    private final BaseFrameType baseType;

    Type(BaseFrameType baseType) {
      this.baseType = baseType;
    }
  }
}

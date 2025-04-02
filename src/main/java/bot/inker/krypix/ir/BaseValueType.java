package bot.inker.krypix.ir;

public enum BaseValueType {
  INT(BaseFrameType.INT), LONG(BaseFrameType.LONG), FLOAT(BaseFrameType.FLOAT), DOUBLE(BaseFrameType.DOUBLE), OBJECT(BaseFrameType.OBJECT),
  BOOLEAN(BaseFrameType.INT), BYTE(BaseFrameType.INT), CHAR(BaseFrameType.INT), SHORT(BaseFrameType.INT);

  private final BaseFrameType frameType;

  BaseValueType(BaseFrameType frameType) {
    this.frameType = frameType;
  }

  public BaseFrameType frameType() {
    return frameType;
  }
}

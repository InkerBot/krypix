package bot.inker.krypix;

import bot.inker.krypix.common.attachment.AttachmentContainer;
import bot.inker.krypix.common.attachment.WithAttachment;
import bot.inker.krypix.util.path.FullPathUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class KrypixResource implements WithAttachment.Contained {
  private final AttachmentContainer container = new AttachmentContainer();

  private final KrypixScope scope;
  private final String sourceFullPath;
  private final Consumer<String> renameConsumer;
  private final Consumer<byte[]> setConsumer;
  private final Supplier<byte[]> getSupplier;
  private String fullPath;

  public KrypixResource(
    KrypixScope scope,
    String sourceFullPath,
    Consumer<String> renameConsumer,
    Consumer<byte[]> setConsumer,
    Supplier<byte[]> getSupplier
  ) {
    this.scope = scope;
    this.sourceFullPath = sourceFullPath;
    this.fullPath = sourceFullPath;
    this.renameConsumer = renameConsumer;
    this.setConsumer = setConsumer;
    this.getSupplier = getSupplier;
  }

  public byte[] getBytes() {
    return getSupplier.get();
  }

  public void setBytes(byte[] bytes) {
    setConsumer.accept(bytes);
  }

  public KrypixScope scope() {
    return scope;
  }

  public String sourceFullPath() {
    return sourceFullPath;
  }

  public String path() {
    return fullPath.substring(fullPath.lastIndexOf('/') + 1);
  }

  public void path(String newPath) {
    fullPath(FullPathUtil.replacePath(fullPath, newPath));
  }

  public String fullPath() {
    return fullPath;
  }

  public void fullPath(String newFullPath) {
    renameConsumer.accept(newFullPath);
    this.fullPath = newFullPath;
  }

  public String packageName() {
    var shortPath = path();
    var lastIndexOf = shortPath.lastIndexOf('/');
    return lastIndexOf == -1 ? "" : shortPath.substring(0, lastIndexOf);
  }

  public String fileName() {
    var shortPath = path();
    var lastIndexOf = shortPath.lastIndexOf('/');
    return lastIndexOf == -1 ? shortPath : shortPath.substring(lastIndexOf + 1);
  }

  public String extension() {
    var fileName = fileName();
    var lastIndexOf = fileName.lastIndexOf('.');
    return lastIndexOf == -1 ? "" : fileName.substring(lastIndexOf + 1);
  }

  @Override
  public WithAttachment container() {
    return container;
  }
}

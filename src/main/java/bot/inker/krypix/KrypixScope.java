package bot.inker.krypix;

import bot.inker.krypix.common.attachment.AttachmentKey;
import bot.inker.krypix.loader.ClassPool;
import bot.inker.krypix.loader.ResourcePool;
import org.rocksdb.RocksDB;

import java.io.File;
import java.util.List;

public final class KrypixScope {
  private final String name;
  private final ResourcePool resourcePool;
  private final ClassPool classPool;

  private boolean mutable = true;

  public KrypixScope(String name, RocksDB db) {
    this.name = name;
    this.resourcePool = new ResourcePool(this, db);
    this.classPool = new ClassPool(this);
  }

  public String name() {
    return name;
  }

  public ResourcePool resourcePool() {
    return resourcePool;
  }

  public ClassPool classPool() {
    return classPool;
  }

  public boolean mutable() {
    return mutable;
  }

  public void mutable(boolean mutable) {
    this.mutable = mutable;
  }

  public void save(File output) {
    resourcePool.save(output);
  }
}

package bot.inker.krypix.loader;

import bot.inker.krypix.KrypixResource;
import bot.inker.krypix.KrypixScope;
import bot.inker.krypix.util.path.FullPathUtil;
import bot.inker.krypix.util.uncheck.UncheckUtil;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.*;

public final class ResourcePool {
  private final KrypixScope scope;
  private final RocksDB db;
  private final Map<String, List<KrypixResource>> resourcesByShortPath = new HashMap<>();
  private final Map<String, KrypixResource> resourcesByFullPath = new HashMap<>();

  public ResourcePool(KrypixScope scope, RocksDB db) {
    this.scope = scope;
    this.db = db;
  }

  public KrypixScope scope() {
    return scope;
  }

  public List<KrypixResource> getByShortPath(String shortPath) {
    return resourcesByShortPath.getOrDefault(shortPath, Collections.emptyList());
  }

  public Optional<KrypixResource> getByFullPath(String fullPath) {
    return Optional.ofNullable(resourcesByFullPath.get(fullPath));
  }

  public Collection<KrypixResource> all() {
    return resourcesByFullPath.values();
  }

  public KrypixResource createResource(String fullPath) {
    String shortPath = fullPath.substring(fullPath.lastIndexOf('/') + 1);
    var resource = new KrypixResource(scope, shortPath,
      newName -> renameResource(fullPath, newName),
      bytes -> setResourceBySourcePathBytes(FullPathUtil.getPath(fullPath).getBytes(), bytes),
      () -> getResourceBySourcePathBytes(FullPathUtil.getPath(fullPath).getBytes())
    );
    resourcesByShortPath.computeIfAbsent(shortPath, it -> new ArrayList<>()).add(resource);
    resourcesByFullPath.put(fullPath, resource);
    return resource;
  }

  private void setResourceBySourcePathBytes(byte[] sourcePathBytes, byte[] bytes) {
    try {
      db.put(sourcePathBytes, bytes);
    } catch (RocksDBException e) {
      throw UncheckUtil.uncheck(e);
    }
  }

  private byte[] getResourceBySourcePathBytes(byte[] sourcePathBytes) {
    try {
      return db.get(sourcePathBytes);
    } catch (RocksDBException e) {
      throw UncheckUtil.uncheck(e);
    }
  }

  private void renameResource(String oldFullPath, String newFullPath) {
    var oldShortPath = FullPathUtil.getPath(oldFullPath);
    var newShortPath = FullPathUtil.getPath(newFullPath);

    var resource = resourcesByFullPath.remove(oldFullPath);
    var shortResources = resourcesByShortPath.get(oldShortPath);
    if (shortResources != null) {
      shortResources.remove(resource);
      if (shortResources.isEmpty()) {
        resourcesByShortPath.remove(oldShortPath);
      }
    }
    resourcesByFullPath.put(newFullPath, resource);
    resourcesByShortPath.computeIfAbsent(newShortPath, k -> new ArrayList<>()).add(resource);
  }
}

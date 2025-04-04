package bot.inker.krypix.loader;

import bot.inker.krypix.KrypixResource;
import bot.inker.krypix.KrypixScope;
import bot.inker.krypix.common.attachment.AttachmentKey;
import bot.inker.krypix.util.path.FullPathUtil;
import bot.inker.krypix.util.uncheck.UncheckUtil;
import com.google.common.base.Preconditions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ResourcePool {
  private static final AttachmentKey<SaveData> SAVE_DATA_KEY = AttachmentKey.create("save-data");

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
    byte[] fileKey = (scope.name() + "." + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8);
    String shortPath = FullPathUtil.getShortPath(fullPath);

    KrypixResource[] resourcePtr = new KrypixResource[1];
    resourcePtr[0] = new KrypixResource(scope, fullPath,
      newName -> renameResource(resourcePtr[0].fullPath(), newName),
      bytes -> setResourceBySourcePathBytes(fileKey, bytes),
      () -> getResourceBySourcePathBytes(fileKey)
    );

    resourcesByShortPath.computeIfAbsent(shortPath, it -> new ArrayList<>()).add(resourcePtr[0]);
    resourcesByFullPath.put(fullPath, resourcePtr[0]);
    return resourcePtr[0];
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
    var oldShortPath = FullPathUtil.getShortPath(oldFullPath);
    var newShortPath = FullPathUtil.getShortPath(newFullPath);

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

  public void flush() {
    resourcesByFullPath.values()
      .forEach(resource -> resource.setAttachment(SAVE_DATA_KEY, new SaveData()));

    resourcesByFullPath.values().stream()
      .filter(resource -> resource.fullPath().contains("!/"))
      .forEach(resource -> {
        String previousFullPath = FullPathUtil.getPreviousFullPath(resource.fullPath());
        var previousResource = resourcesByFullPath.get(previousFullPath);
        Preconditions.checkState(previousResource != null,
          "Resource %s has no previous resource %s", resource.fullPath(), previousFullPath);
        previousResource.requireAttachment(SAVE_DATA_KEY).addNestedResource(resource);
      });

    resourcesByFullPath.values().forEach(this::flushArchive);
  }

  public void save(File output) {
    flush();
    resourcesByFullPath.values().stream()
      .filter(resource -> !resource.fullPath().contains("!/"))
      .forEach(resource -> {
        File file = new File(output.getAbsolutePath() + "/" + resource.fullPath());
        file.getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(file)) {
          out.write(resource.getBytes());
        } catch (IOException e) {
          throw UncheckUtil.uncheck(e);
        }
      });
  }

  private void flushArchive(KrypixResource resource) {
    var saveData = resource.requireAttachment(SAVE_DATA_KEY);
    if (saveData.nestedResources == null || saveData.flushed) {
      return;
    }

    saveData.nestedResources.forEach(this::flushArchive);

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try (ZipOutputStream zipOut = switch (resource.extension()) {
      case "zip", "jmod" -> new ZipOutputStream(bout);
      case "jar" -> new JarOutputStream(bout);
      default -> throw new IllegalStateException("Unsupported archive type: " + resource.extension());
    }) {
      resource.getAttachment(ResourceAttachments.ARCHIVE_COMMENT)
        .ifPresent(zipOut::setComment);

      Stream.concat(
        saveData.nestedResources.stream().filter(it -> "META-INF/MANIFEST.MF".equals(it.path())),
        saveData.nestedResources.stream().filter(it -> !"META-INF/MANIFEST.MF".equals(it.path()))
          .sorted(Comparator.comparing(KrypixResource::path))
      ).forEach(UncheckUtil.uncheckConsumer(nestedResource -> {
        var entryName = FullPathUtil.getShortPath(nestedResource.fullPath());
        ZipEntry entry = switch (resource.extension()) {
          case "zip", "jmod" -> new ZipEntry(entryName);
          case "jar" -> new JarEntry(entryName);
          default -> throw new IllegalStateException("Unsupported archive type: " + resource.extension());
        };
        applyResource(entry, nestedResource);
        zipOut.putNextEntry(entry);
        zipOut.write(nestedResource.getBytes());
        zipOut.closeEntry();
      }));
    } catch (IOException e) {
      throw UncheckUtil.uncheck(e);
    }

    resource.setBytes(bout.toByteArray());
    saveData.flushed = true;
  }

  private void applyResource(ZipEntry entry, KrypixResource resource) {
    resource.getAttachment(ResourceAttachments.ENTRY_TIME)
      .ifPresent(entry::setTime);
    resource.getAttachment(ResourceAttachments.ENTRY_CREATION_TIME)
      .ifPresent(entry::setCreationTime);
    resource.getAttachment(ResourceAttachments.ENTRY_LAST_ACCESS_TIME)
      .ifPresent(entry::setLastAccessTime);
    resource.getAttachment(ResourceAttachments.ENTRY_LAST_MODIFIED_TIME)
      .ifPresent(entry::setLastModifiedTime);
    resource.getAttachment(ResourceAttachments.ENTRY_METHOD)
      .ifPresent(entry::setMethod);
    resource.getAttachment(ResourceAttachments.ENTRY_COMMENT)
      .ifPresent(entry::setComment);
    resource.getAttachment(ResourceAttachments.ENTRY_EXTRA)
      .ifPresent(entry::setExtra);
  }

  private static final class SaveData {
    private List<KrypixResource> nestedResources = null;
    private boolean flushed = false;

    public void addNestedResource(KrypixResource resource) {
      if (nestedResources == null) {
        nestedResources = new ArrayList<>();
      }
      nestedResources.add(resource);
    }
  }
}

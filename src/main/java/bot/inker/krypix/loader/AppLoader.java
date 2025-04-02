package bot.inker.krypix.loader;

import bot.inker.krypix.AppView;
import bot.inker.krypix.KrypixResource;
import bot.inker.krypix.common.attachment.AttachmentValue;
import bot.inker.krypix.util.StopWatchUtil;
import bot.inker.krypix.util.uncheck.UncheckUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

public final class AppLoader {
  private static final Logger logger = LoggerFactory.getLogger(AppLoader.class);
  private final Visitor visitor;
  private final File tempDirectory;

  public AppLoader(Visitor visitor, File tempDirectory) {
    this.visitor = visitor;
    this.tempDirectory = tempDirectory;

    if (!tempDirectory.exists()) {
      tempDirectory.mkdirs();
    }
  }

  public void load(File baseFile) {
    StopWatchUtil.debugStopWatch(logger,
      "load {}",
      UncheckUtil.uncheckRunnable(() -> loadFileImpl("", baseFile)),
      baseFile
    );
  }

  private void loadFileImpl(String path, File file) throws IOException {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files == null) {
        logger.warn("Failed to list files in directory: {}", file);
        return;
      }
      for (File listFile : files) {
        loadFileImpl(path + "/" + listFile.getName(), listFile);
      }
    } else {
      try (var in = new FileInputStream(file)) {
        loadImpl(path, in);
      }
    }
  }

  private void loadImpl(String path, InputStream in) throws IOException {
    var extension = FilenameUtils.getExtension(path);
    switch (extension) {
      case "jar", "zip", "jmod" -> {
        var tmpFile = File.createTempFile("loading-", "." + extension);
        try (var out = new FileOutputStream(tmpFile)) {
          in.transferTo(out);
        }
        byte[] bytes;
        try (var zin = new FileInputStream(tmpFile)) {
          bytes = zin.readAllBytes();
        }
        try (var zipFile = "jar".equals(extension) ? new JarFile(tmpFile) : new ZipFile(tmpFile)) {
          String comment = zipFile.getComment();
          if (comment != null) {
            visitor.visit(path, bytes, AttachmentValue.of(ResourceAttachments.ARCHIVE_COMMENT, zipFile.getComment()));
          } else {
            visitor.visit(path, bytes);
          }
          loadArchive(path, zipFile);
        }
        tmpFile.delete();
      }
      default -> {
        visitor.visit(path, in.readAllBytes());
      }
    }
  }

  private void loadArchive(String basePath, ZipFile zipFile) throws IOException {
    var entries = zipFile.entries();
    while (entries.hasMoreElements()) {
      var zipEntry = entries.nextElement();
      if (zipEntry.isDirectory()) {
        continue;
      }
      try (InputStream in = zipFile.getInputStream(zipEntry)) {
        loadImpl(basePath + "!/" + zipEntry.getName(), in);
      }
    }
  }

  public interface Visitor {
    void visit(String path, byte[] bytes, AttachmentValue<?> ... attachments) throws IOException;
  }
}

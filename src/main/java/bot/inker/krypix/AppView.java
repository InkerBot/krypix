package bot.inker.krypix;

import bot.inker.krypix.asm.KrypixControlResolver;
import bot.inker.krypix.asm.locator.FieldLocator;
import bot.inker.krypix.asm.locator.MethodLocator;
import bot.inker.krypix.ir.ref.*;
import bot.inker.krypix.loader.AppLoader;
import bot.inker.krypix.util.StopWatchUtil;
import bot.inker.krypix.util.uncheck.UncheckUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class AppView {
  private static final Logger logger = LoggerFactory.getLogger(AppView.class);
  private final File directory;
  private final RocksDB db;
  private final Map<String, KrypixScope> scopes = new HashMap<>();
  private ClassHierarchy hierarchy;

  public AppView(File directory, boolean withStandardScope) {
    this.directory = directory;
    try {
      new File(directory, "resources").mkdirs();
      this.db = RocksDB.open(new File(directory, "resources").getAbsolutePath());
    } catch (RocksDBException e) {
      throw UncheckUtil.uncheck(e);
    }

    if (withStandardScope) {
      scope(KrypixStandards.SCOPE_PROGRAM).mutable(true);
      scope(KrypixStandards.SCOPE_LIBRARY).mutable(false);
      scope(KrypixStandards.SCOPE_EXTERNAL).mutable(false);
    }
  }

  public KrypixScope scope(String name) {
    return scopes.computeIfAbsent(name, it -> new KrypixScope(it, db));
  }

  public Stream<KrypixResource> getResources(String shortPath) {
    return scopes.values().stream()
      .flatMap(it -> it.resourcePool().getByShortPath(shortPath).stream());
  }

  public Optional<KrypixResource> getResourceByFullPath(String fullPath) {
    return scopes.values().stream()
      .map(it -> it.resourcePool().getByFullPath(fullPath))
      .filter(Optional::isPresent)
      .findFirst()
      .orElse(Optional.empty());
  }

  public KrypixResource createResource(String scope, String fullPath, byte[] bytes) {
    var resource = scope(scope).resourcePool().createResource(fullPath);
    resource.setBytes(bytes);
    return resource;
  }

  public Stream<KrypixClass> allClasses() {
    return scopes.values().stream()
      .flatMap(it -> it.classPool().all().stream());
  }

  public Stream<KrypixClass> getClasses(String name) {
    if (name == null) {
      return Stream.empty();
    }
    return scopes.values().stream()
      .flatMap(it -> it.classPool().get(name).stream());
  }

  public Optional<KrypixClass> getClass(String name) {
    if (name == null) {
      return Optional.empty();
    }
    return scopes.values().stream()
      .map(it -> it.classPool().get(name))
      .filter(Optional::isPresent)
      .findFirst()
      .orElse(Optional.empty());
  }

  public KrypixClass requireClass(String name) {
    return getClass(name).orElseThrow(() -> new IllegalArgumentException("Required class not found: " + name));
  }

  public TypeRef parseTypeRef(String desc) {
    var type = Type.getType(desc);
    switch (type.getSort()) {
      case Type.VOID, Type.BOOLEAN, Type.CHAR, Type.BYTE, Type.SHORT, Type.INT, Type.FLOAT, Type.LONG, Type.DOUBLE -> {
        return TypeRef.fromAsmSort(type.getSort());
      }
      case Type.ARRAY -> {
        var elementType = type.getElementType();
        if (elementType.getSort() == Type.OBJECT) {
          return TypeRef.ofClass(parseClassRef(elementType.getInternalName())).withDimensions(type.getDimensions());
        } else {
          return TypeRef.fromAsmSort(elementType.getSort()).withDimensions(type.getDimensions());
        }
      }
      case Type.OBJECT -> {
        return TypeRef.ofClass(parseClassRef(type.getInternalName()));
      }
      default -> throw new IllegalArgumentException("Unknown type sort: " + type.getSort());
    }
  }

  public ClassRef parseClassRef(String name) {
    return getClass(name)
      .<ClassRef>map(ClassRefResolved::new)
      .orElseGet(() -> new ClassRefUnknown(name));
  }

  public MethodType parseMethodTypeRef(String desc) {
    var methodType = Type.getMethodType(desc);
    var returnType = parseTypeRef(methodType.getReturnType().getDescriptor());
    var parameterTypes = Arrays.stream(methodType.getArgumentTypes())
      .map(argType -> parseTypeRef(argType.getDescriptor()))
      .toArray(TypeRef[]::new);
    return new MethodType(parameterTypes, returnType);
  }

  public MethodLocator methodLocator() {
    return new MethodLocator(this);
  }

  public FieldLocator fieldLocator() {
    return new FieldLocator(this);
  }

  public AppLoader loader(String scope) {
    return new AppLoader(((path, bytes, attachments) -> {
      var resource = createResource(scope, path, bytes);
      Arrays.stream(attachments).forEach(resource::setAttachment);
    }), new File(directory, "temp"));
  }

  public void build() {
    StopWatchUtil.infoStopWatch(logger, "Building classes", () -> scopes.values().forEach(scope ->
      scope.resourcePool().all().stream()
        .filter(it -> "class".equals(it.extension()))
        .forEach(clazzResource -> {
          byte[] bytes = clazzResource.getBytes();
          ClassNode node = new ClassNode();
          ClassReader reader = new ClassReader(bytes);
          reader.accept(node, 0);

          KrypixClass clazz = new KrypixClass(clazzResource, node);
          for (MethodNode method : node.methods) {
            KrypixMethod krypixMethod = new KrypixMethod(clazz, method);
            clazz.methods().add(krypixMethod);
          }
          for (FieldNode field : node.fields) {
            KrypixField krypixField = new KrypixField(clazz, field);
            clazz.fields().add(krypixField);
          }

          scope.classPool().put(clazz.name(), clazz);
        })
    ));

    StopWatchUtil.infoStopWatch(logger, "Linking classes", () -> scopes.values().forEach(scope -> {
      scope.classPool().all().forEach(clazz -> {
        if (clazz.classNode().superName != null) {
          clazz.superClass(requireClass(clazz.classNode().superName));
        }

        if (clazz.classNode().interfaces != null) {
          clazz.interfaces().addAll(clazz.classNode().interfaces.stream()
            .map(this::requireClass)
            .toList());
        }

        if (clazz.classNode().nestHostClass != null) {
          clazz.nestHostClass(requireClass(clazz.classNode().nestHostClass));
        }

        if (clazz.classNode().nestMembers != null) {
          clazz.nestMembers().addAll(clazz.classNode().nestMembers.stream()
            .map(this::requireClass)
            .toList());
        }

        if (clazz.classNode().permittedSubclasses != null) {
          clazz.permittedSubclasses().addAll(clazz.classNode().permittedSubclasses.stream()
            .map(this::requireClass)
            .toList());
        }

        clazz.fields().forEach(field -> {
          field.type(parseTypeRef(field.desc()));
        });

        clazz.methods().forEach(method -> {
          method.type(parseMethodTypeRef(method.desc()));
        });
      });
    }));

    this.hierarchy = new ClassHierarchy(this);

    StopWatchUtil.infoStopWatch(logger, "Resolving methods", () -> scopes.values().stream()
      .filter(KrypixScope::mutable)
      .forEach(scope ->
        scope.classPool().all().stream()
          .flatMap(clazz -> clazz.methods().stream())
          .filter(KrypixMethod::hasCode)
          .forEach(method -> {
            method.body(new KrypixControlResolver(this, method).resolve());
          })
      ));
  }
}

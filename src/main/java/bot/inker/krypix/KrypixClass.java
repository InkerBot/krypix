package bot.inker.krypix;

import bot.inker.krypix.common.WithModifier;
import bot.inker.krypix.common.attachment.AttachmentContainer;
import bot.inker.krypix.common.attachment.WithAttachment;
import bot.inker.krypix.util.path.FullPathUtil;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class KrypixClass implements WithModifier.Mutable, WithAttachment.Contained {
  private final AttachmentContainer container = new AttachmentContainer();
  private final KrypixResource resource;
  private final List<KrypixClass> interfaces;
  private final List<KrypixClass> nestMembers;
  private final List<KrypixClass> permittedSubclasses;
  private final List<KrypixField> fields;
  private final List<KrypixMethod> methods;
  private ClassNode classNode;
  private @Nullable KrypixClass superClass;
  private @Nullable KrypixClass nestHostClass;

  public KrypixClass(KrypixResource resource, ClassNode classNode) {
    this.resource = resource;
    this.classNode = classNode;
    this.interfaces = new ArrayList<>();
    this.nestMembers = new ArrayList<>();
    this.permittedSubclasses = new ArrayList<>();
    this.fields = new ArrayList<>();
    this.methods = new ArrayList<>();
  }

  @Override
  public AttachmentContainer container() {
    return container;
  }

  public KrypixScope scope() {
    return resource.scope();
  }

  public KrypixResource resource() {
    return resource;
  }

  public ClassNode classNode() {
    return classNode;
  }

  public void classNode(ClassNode classNode) {
    this.classNode = classNode;
  }

  public String name() {
    return classNode.name;
  }

  public void name(String name) {
    resource.path(resource.path().replace(this.classNode.name, name));
    this.classNode.name = name;
  }

  @Override
  public int modifier() {
    return classNode.access;
  }

  @Override
  public void modifier(int modifier) {
    classNode.access = modifier;
  }

  public @Nullable KrypixClass superClass() {
    return superClass;
  }

  public void superClass(@Nullable KrypixClass superClass) {
    this.superClass = superClass;
  }

  public List<KrypixClass> interfaces() {
    return interfaces;
  }

  public @Nullable KrypixClass nestHostClass() {
    return nestHostClass;
  }

  public void nestHostClass(@Nullable KrypixClass nestHostClass) {
    this.nestHostClass = nestHostClass;
  }

  public List<KrypixClass> nestMembers() {
    return nestMembers;
  }

  public List<KrypixClass> permittedSubclasses() {
    return permittedSubclasses;
  }

  public List<KrypixField> fields() {
    return fields;
  }

  public List<KrypixMethod> methods() {
    return methods;
  }

  public boolean isObject() {
    return "java/lang/Object".equals(name());
  }

  public void applyMetadata() {
    classNode.interfaces = interfaces.stream().map(KrypixClass::name).toList();
    classNode.superName = superClass == null ? null : superClass.name();
    classNode.nestHostClass = nestHostClass == null ? null : nestHostClass.name();
    classNode.nestMembers = nestMembers.stream().map(KrypixClass::name).toList();
    classNode.permittedSubclasses = permittedSubclasses.stream().map(KrypixClass::name).toList();
    classNode.methods = methods.stream().map(KrypixMethod::methodNode).toList();
    classNode.fields = fields.stream().map(KrypixField::fieldNode).toList();
  }

  public List<KrypixClass> allImplementingClasses() {
    var set = new LinkedHashSet<KrypixClass>();
    resolveAllImplementingClasses(this, set);
    return List.copyOf(set);
  }

  private void resolveAllImplementingClasses(KrypixClass clazz, Set<KrypixClass> subClasses) {
    if (subClasses.add(clazz)) {
      var superClass = clazz.superClass;
      if (superClass != null) {
        resolveAllImplementingClasses(superClass, subClasses);
      }

      for (KrypixClass interface0 : clazz.interfaces) {
        resolveAllImplementingClasses(interface0, subClasses);
      }
    }
  }

  @Override
  public String toString() {
    var sb = new StringBuilder("KrypixClass{");
    if (isPublic()) sb.append("public ");
    if (isProtected()) sb.append("protected ");
    if (isPrivate()) sb.append("private ");
    if (isPackagePrivate()) sb.append("/* package-private */ ");
    if (!isInterface() && isAbstract()) sb.append("abstract ");
    if (isFinal()) sb.append("final ");
    if (isInterface()) {
      if (isAnnotation()) sb.append("@");
      sb.append("interface ");
    }
    if (isEnum()) sb.append("enum ");
    if (!isInterface() && !isEnum()) sb.append("class ");
    sb.append(name().replace('/', '.'));
    var superClass = superClass();
    if (superClass != null && !superClass.isObject()) {
      sb.append(" extends ");
      sb.append(superClass.name().replace('/', '.'));
    }

    var filteredInterfaces = interfaces.stream().filter(it ->
      !isAnnotation() || !"java/lang/annotation/Annotation".equals(it.name())
    ).toList();
    if (!filteredInterfaces.isEmpty()) {
      sb.append(" implements ");
      for (int i = 0; i < filteredInterfaces.size(); i++) {
        if (i > 0) sb.append(", ");
        sb.append(filteredInterfaces.get(i).name().replace('/', '.'));
      }
    }

    sb.append("}");
    return sb.toString();
  }
}

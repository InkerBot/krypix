package bot.inker.krypix;

import bot.inker.krypix.util.StopWatchUtil;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public final class ClassHierarchy {
  private static final Logger logger = LoggerFactory.getLogger(ClassHierarchy.class);

  private final AppView appView;
  private final SimpleDirectedGraph<KrypixClass, DefaultEdge> P;
  private final List<KrypixClass> topClasses;

  private final SimpleGraph<KrypixMethod, DefaultEdge> G;
  private final Map<KrypixMethod, List<KrypixMethod>> methodHierarchy;

  public ClassHierarchy(AppView appView) {
    this.appView = appView;
    this.P = new SimpleDirectedGraph<>(DefaultEdge.class);

    StopWatchUtil.infoStopWatch(logger, "build P (class hierarchy)", this::buildHierarchy);
    StopWatchUtil.infoStopWatch(logger, "test P (detect cycles)", this::detectCycles);

    this.topClasses = this.P.vertexSet().stream()
      .filter(it -> P.inDegreeOf(it) == 0)
      .toList();
    this.G = new SimpleGraph<>(DefaultEdge.class);
    this.methodHierarchy = StopWatchUtil.infoStopWatch(logger, "build G (method group)", this::buildMethodHierarchy);

    logger.info("Class hierarchy built with {} classes and {} methods", P.vertexSet().size(), G.vertexSet().size());
  }

  private void buildHierarchy() {
    appView.allClasses()
      .forEach(P::addVertex);

    appView.allClasses()
      .forEach(clazz -> {
        if (clazz.superClass() != null) {
          P.addEdge(clazz, clazz.superClass());
        }
        for (var anInterface : clazz.interfaces()) {
          P.addEdge(clazz, anInterface);
        }
      });
  }

  private void detectCycles() {
    var CycleDetector = new CycleDetector<>(P);
    if (CycleDetector.detectCycles()) {
      var cycles = CycleDetector.findCycles();
      throw new IllegalStateException("Dependency cycle detected in class hierarchy:\n" + cycles);
    }
  }

  private Map<KrypixMethod, List<KrypixMethod>> buildMethodHierarchy() {
    var tmpMethodClassesGroup = new HashMap<KrypixMethod, Set<KrypixClass>>();
    appView.allClasses().forEach(clazz -> {
      clazz.methods().forEach(method -> {
        G.addVertex(method);
        tmpMethodClassesGroup.computeIfAbsent(method, it -> new LinkedHashSet<>()).add(clazz);
      });
    });

    topClasses.forEach(topClazz -> {
      var allImplementingClasses = topClazz.allImplementingClasses();
      var allPossibleMethods = allImplementingClasses.stream()
        .flatMap(it -> it.methods().stream())
        .filter(it -> !it.name().startsWith("<"))
        .toList();
      var allPossibleMethodsGroup = allPossibleMethods.stream()
        .collect(Collectors.groupingBy(it -> it.name() + it.desc()));

      allPossibleMethods.forEach(method -> {
        tmpMethodClassesGroup.computeIfAbsent(method, it -> new LinkedHashSet<>()).addAll(allImplementingClasses);
      });

      allPossibleMethodsGroup.values().forEach(methods -> {
        methods.forEach(methodA -> {
          methods.forEach(methodB -> {
            if (methodA != methodB) {
              G.addEdge(methodA, methodB);
            }
          });
        });
      });
    });

    var result = new HashMap<KrypixMethod, List<KrypixMethod>>();
    new ConnectivityInspector<>(G)
      .connectedSets()
      .forEach(connectedSet ->{
        var methodGroup = new ArrayList<>(connectedSet);
        for (KrypixMethod method : connectedSet) {
          result.put(method, methodGroup);
        }
      });

    return result;
  }

  public SimpleDirectedGraph<KrypixClass, DefaultEdge> P() {
    return P;
  }

  public List<KrypixClass> topClasses() {
    return topClasses;
  }

  public SimpleGraph<KrypixMethod, DefaultEdge> G() {
    return G;
  }

  public Map<KrypixMethod, List<KrypixMethod>> methodHierarchy() {
    return methodHierarchy;
  }

  public boolean isAssignableFrom(KrypixClass classA, KrypixClass classB) {
    return instanceOf(classA, classB);
  }

  public boolean instanceOf(KrypixClass classA, KrypixClass classB) {
    if (classA == classB || classB.name().equals("java/lang/Object")) {
      return true;
    }

    if (classB.isInterface()) {
      return resolveInterface(classA, classB, new HashSet<>());
    } else {
      KrypixClass current = classA.superClass();
      while (current != null) {
        if (current == classB) {
          return true;
        }
        current = current.superClass();
      }
      return false;
    }
  }

  private boolean resolveInterface(KrypixClass classA, KrypixClass classB, Set<KrypixClass> visited) {
    var interfaces = classA.interfaces();
    for (var aInterface : interfaces) {
      if (aInterface == classB) {
        return true;
      }
      if (visited.add(aInterface) && resolveInterface(aInterface, classB, visited)) {
        return true;
      }
    }
    return false;
  }

  public KrypixClass getCommonSuperClass(KrypixClass classA, KrypixClass classB) {
    if (classA == classB) return classB;
    if (classA.isObject() || classB.isObject()) {
      return appView.requireClass("java/lang/Object");
    }

    if (isAssignableFrom(classA, classB)) return classA;
    if (isAssignableFrom(classB, classA)) return classB;

    // If one of them is an interface, fallback to Object
    if (classA.isInterface() || classB.isInterface()) {
      return appView.requireClass("java/lang/Object");
    }

    // Climb up the superclass chain of typeA until we find a common supertype
    do {
      KrypixClass superClass = classA.superClass();
      if (superClass == null) {
        throw new IllegalStateException("Internal error, reached top of hierarchy");
      }
      classA = superClass;
    } while (!isAssignableFrom(classA, classB));

    return classA;
  }
}

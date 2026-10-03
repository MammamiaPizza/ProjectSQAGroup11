TARGETS: UnsafeAllocator.newInstance(Class) when given interface/abstract class
ORACLES: Expected InvocationTargetException for interface/abstract instantiation (test assertions)
CASES: testInterfaceInstantiation (interface), testAbstractClassInstantiation (abstract), concrete
instantiation (normal)
RISKS: Unsafe.allocateInstance throws UnsupportedOperationException for non-instantiable types; need
to remap to InvocationTargetException
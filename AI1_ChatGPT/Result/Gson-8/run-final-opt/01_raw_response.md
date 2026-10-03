TARGETS: UnsafeAllocator.create() and returned allocator.newInstance(Class<T>).
ORACLES: Trigger expects InvocationTargetException for interface and abstract-class allocation.
CASES: newInstance(interface Class) must surface InvocationTargetException.
CASES: newInstance(abstract Class) must surface InvocationTargetException.
CASES: Include a concrete class allocation only if behavior is observable from this version.
RISKS: create() selects reflective allocation paths; runtime/JDK access may affect reachable path.
RISKS: Context does not specify expected constructors, concrete allocation result, or null-class behavior.
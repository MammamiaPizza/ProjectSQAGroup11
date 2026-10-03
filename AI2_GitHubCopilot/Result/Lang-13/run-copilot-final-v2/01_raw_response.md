TARGETS: clone(Class) for primitive types (int, void, boolean, ...)
ORACLES: clone must not throw SerializationException/ClassNotFoundException; return same Class
CASES: clone(int.class), void.class, boolean.class, byte.class
CASES: clone(char.class), short.class, long.class, float.class, double.class
CASES: clone for non-primitive Class (e.g., String.class) as regression guard
ORACLES: ClassLoaderAwareObjectInputStream must resolve primitive class names correctly
TARGETS: deserialize(byte[]) internally used by clone
ORACLES: round-trip serialize/deserialize of primitive Class bytes succeeds
RISKS: No fix source; inferred from LANG-788 title; ClassLoader resolution may be
environment-specific
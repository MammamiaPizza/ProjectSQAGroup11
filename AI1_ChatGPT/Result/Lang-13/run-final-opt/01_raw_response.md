TARGETS: clone(T), deserialize(byte[]/InputStream), and ClassLoaderAwareObjectInputStream.resolveClass(ObjectStreamClass).
ORACLES: Trigger expects cloning primitive type Class objects without SerializationException/ClassNotFoundException.
CASES: clone(int.class), clone(boolean.class), clone(void.class); assert returned Class equals original.
CASES: clone representative non-primitive Class objects to preserve normal Class serialization behavior.
CASES: deserialize serialized primitive Class bytes through byte[] and InputStream overloads.
RISKS: Primitive class names (for example "int") are not loadable via ordinary ClassLoader class-name lookup.
RISKS: Validate null/error behavior only where existing SerializationUtils tests establish expectations.
RISKS: Context is limited to buggy-version API and trigger; no alternate-version behavior is available.
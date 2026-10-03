TARGETS: JsonMappingException.getPathReference()/getPathReference(StringBuilder), _appendPathDesc,
prependPath, Reference.getDescription()
ORACLES: Expected pathReference for inner-class members embeds full enclosing class name (e.g.
TestExceptionHandlingWithDefaultDeserialization$Foo not just Foo)
CASES: path with multiple inner classes in chain; top-level class only; mix of field & index
references; single inner class; empty path; null _from in Reference
RISKS: No other modified classes; must not rely on fixed behaviour; ensure inner-class canonical
name is checked from _from object class
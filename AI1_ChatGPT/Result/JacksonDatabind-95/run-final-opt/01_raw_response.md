TARGETS: TypeFactory.constructFromCanonical(String) canonical-type parsing used by testCanonicalNames  
TARGETS: TypeParser.parse(String), parseType/parseTypes, and class lookup/error reporting  
ORACLES: JavaType canonical-name round-trip/equality expected by existing TestTypeFactory::testCanonicalNames  
CASES: Normal canonical simple class and nested generic canonical names accepted by TypeFactory parser  
CASES: Boundary nested/parameterized canonical names; verify all generic arguments are retained  
CASES: Error malformed canonical syntax and unresolvable class produce IllegalArgumentException, not NPE  
RISKS: Only trigger name and partial signatures are available; exact canonical inputs/assertions are not shown
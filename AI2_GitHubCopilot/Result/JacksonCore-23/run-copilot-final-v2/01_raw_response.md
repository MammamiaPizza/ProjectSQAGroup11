TARGETS: createInstance() subclass validation, withArrayIndenter, withObjectIndenter,
writeRootValueSeparator
ORACLES: IllegalArgumentException from createInstance for subclass returning wrong class; output via
JsonGenerator
CASES: createInstance() returns same class (pass), returns different class (must throw); null/empty
rootSeparator
CASES: indenter substitution propagation to new instance; serialization output consistency after
indent change
RISKS: unknown exact validation logic; must infer from "Should not pass" that fix enforces class
identity
TARGETS: registerSubtypes(NamedType...), init of _registeredSubtypes; resolution (by class)
iterating _registeredSubtypes
ORACLES: No NullPointerException; subtypes added to set; null/empty arrays handled without error
CASES: register null/empty/valid array; array with null entries; resolve baseType=null; resolve
after registration to verify subtype list
RISKS: uninitialized _registeredSubtypes (NPE on add); null NamedType entries (NPE in iteration);
exact location unknown
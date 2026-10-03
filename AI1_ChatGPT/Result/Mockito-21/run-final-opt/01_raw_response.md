TARGETS: ConstructorInstantiator.newInstance(Class), withOuterClass(Class), noArgConstructor(Class)
ORACLES: Trigger test creates_instances_of_inner_classes; successful creation is the primary expected result.
CASES: Instantiate a non-static inner class using a compatible outerClassInstance.
CASES: Instantiate a class with a parameter-less constructor via the no-argument path.
CASES: Incompatible outer instance or missing parameter-less constructor should raise InstantationException.
ORACLES: Error text specifies correct outer type and parameter-less target constructor requirements.
RISKS: Private helper behavior must be exercised through public newInstance only.
RISKS: Context omits constructor visibility, argument-selection rules, and exact expected exception assertions.
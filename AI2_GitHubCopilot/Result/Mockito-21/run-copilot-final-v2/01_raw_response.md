TARGETS: ConstructorInstantiator.newInstance(Class) for non-static inner classes
TARGETS: withOuterClass(Class) private method resolving constructor with outer instance
ORACLES: Given correct outer instance, inner class instantiation must succeed
ORACLES: Given null outer instance, must throw InstantationException with guidance
ORACLES: Given wrong-type outer instance, must throw informative InstantationException
CASES: inner class with matching outer; null outer; mismatched outer; static inner; non-inner class
RISKS: Limited visibility into fix; constructor argument matching and accessibility are key unknowns
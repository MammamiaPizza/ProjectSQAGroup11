TARGETS: ToStringStyle.getRegistry/isRegistered/register/unregister cycle bookkeeping
TARGETS: appendCyclicObject, appendSummary, appendInternal array/collection/map detection
TARGETS: appendStart/appendEnd register/unregister symmetry per append call
ORACLES: existing ToStringBuilderTest cyclic/hierarchy expected strings and null-vs-{} assertions
CASES: self-referential object graph, array containing itself, long/int/double/char/boolean arrays
CASES: nested array-array cycles (testReflectionArrayArrayCycle, Level2)
CASES: mixed object+array cycles (testReflectionArrayAndObjectCycle)
CASES: superclass reflection hierarchy (testReflectionHierarchy)
CASES: single-var and two-var self cycles (testSelfInstance*)
RISKS: ThreadLocal registry isolation vs static WeakHashMap synchronization and unregister cleanup
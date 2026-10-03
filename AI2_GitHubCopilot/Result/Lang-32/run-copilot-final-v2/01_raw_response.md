TARGETS: reflectionHashCode(Object) with cycle detection via REGISTRY;
register/isRegistered/unregister static helpers; append(Object) for non-cyclic cases.
ORACLES: Equal objects must yield equal hash; cycles produce stable hash (e.g., 0 or a computed
value) without infinite recursion; expected null indicates a method that returns a set when empty.
CASES: Normal: simple POJO. Cycle: object A that holds ref to itself. Edge: null object, empty
object, transient fields, excludeFields list, concurrent REGISTRY access.
RISKS: ThreadLocal REGISTRY leakage across test runs, IDKey equality when multiple instances share
same identity hash, reflective access to private fields may fail under security constraints.
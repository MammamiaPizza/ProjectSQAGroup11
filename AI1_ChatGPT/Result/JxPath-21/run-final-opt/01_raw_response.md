TARGETS: PropertyPointer.equals(Object), hashCode(), getImmediateNode(), isActual(), getLength()
ORACLES: Existing JXPath151Test expects map/b != map/a
ORACLES: Existing MixedModelTest expects $testnull/nothing[1] iterator to yield [null]
CASES: Distinct map property pointers a and b must compare unequal
CASES: Equality/hashCode consistency for pointers representing the same property
CASES: Null-valued property must remain addressable as one node, not an empty iterator
RISKS: Behavior depends on subclasses and NodePointer context not provided

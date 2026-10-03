TARGETS: Expression.iterate(EvalContext) when computeValue returns NodeSet
TARGETS: ValueIterator.next() extracting property values from Pointer items
TARGETS: PointerIterator constructor with QName/locale for value conversion
ORACLES: iterate() must return Iterator that yields actual node values, not paths
ORACLES: ValueIterator.next() should call pointer.getValue() not pointer string
ORACLES: PointerIterator uses QName/locale to produce expected value strings
CASES: NodeSet with 2 pointers (Nested values), single node, empty NodeSet
CASES: Non-NodeSet computeValue result falls back to PointerIterator
RISKS: Cannot access internal NodeSet/Pointer; need mocks for EvalContext, etc.
RISKS: Full ExtensionFunction integration untestable; limited to unit-level Expression
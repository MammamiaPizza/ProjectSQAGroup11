TARGETS: CoreOperationCompare.findMatch(Iterator,Iterator) and equal(Object,Object)
TARGETS: CoreOperationCompare.contains(Iterator,Object) for collection element matching
ORACLES: Expression "$d = 'a'" comparing iterated variable against string literal returns true
ORACLES: javax.jxpath.JXPathContext iterate() traverses collection items; compare matches each
CASES: Single-value variable vs string literal normal equality
CASES: Multi-value collection variable iterated vs string; match-on-any/first semantics
CASES: Boundary: null variable, empty collection, mixed types in collection vs literal
RISKS: Abstract class; instantiate via concrete subclass or test indirectly through JXPathContext
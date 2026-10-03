TARGETS: ConstructorConstructor.get ObjectConstructor selection for Map subinterface raw types.
ORACLES: MapTest asserts result assignable to ConcurrentMap/ConcurrentNavigableMap;
ClassCastException exposes wrong defaults.
CASES: raw type ConcurrentMap must yield a ConcurrentMap-usable instance, not LinkedHashMap.
CASES: raw type ConcurrentNavigableMap must yield a SortedMap/NavigableMap-usable instance, not
TreeMap.
CASES: boundary SortedMap still gets TreeMap-compatible; plain Map gets LinkedHashMap-compatible.
CASES: error: interface-map default path in get must not fall through to LinkedHashMap/TreeMap.
RISKS: Only partial signatures supplied; exact chosen concrete constructors for concurrent maps not
shown.
RISKS: Cannot inspect full constructor branches or MapTest assertion bodies beyond trigger names.
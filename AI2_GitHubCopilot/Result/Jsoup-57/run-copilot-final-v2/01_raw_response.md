TARGETS: Attributes.remove, removeIgnoreCase, iterator, dataset, asList, Element.removeAttr chaining
TARGETS: State after sequential removals, removal while iterating, removal after put/addAll
ORACLES: No ConcurrentModificationException; size, hasKey, get reflect correct post-removal state
ORACLES: Dataset().entrySet() content matches attributes map after modifications
CASES: Remove while iterating attributes via iterator(); remove while iterating dataset().entrySet()
CASES: Remove same key multiple times; remove nonexistent key; chained removeIgnoreCase
CASES: Remove during asList() iteration; remove via iterator.remove(); addAll then remove some keys
RISKS: Only public API; internal map not accessible; concurrency only simulated by
iteration/modification in single thread
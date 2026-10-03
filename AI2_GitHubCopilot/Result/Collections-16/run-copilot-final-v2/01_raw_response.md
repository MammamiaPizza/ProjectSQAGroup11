TARGETS: SetUniqueList.set(int,Object), addAll(int,Collection), subList().set(), ListIterator.set(),
ListIterator.add()
ORACLES: Unique constraint: no duplicates after any mutation; size equals set size; remove returns
index consistency
CASES: Normal: set(index,dup); addAll(index,dupColl); boundary: set at 0, size-1; subList set/add;
iterator set/add with dups
RISKS: Lacking exact bug details. COLLECTIONS-307 may involve set/addAll ignoring set; subList break
uniqueness; iterator leak
TARGETS: SetUniqueList.subList(int,int) and all mutating List ops on returned view:
add,addAll,set,remove,removeAll,retainAll,clear,sort; ListIterator add/set
ORACLES: Expected: subList must be unmodifiable; all mutating methods must throw
UnsupportedOperationException per testSubListIsUnmodifiable
CASES: Full-range subList; partial subList; singleton subList; empty subList; add at index; set;
remove; clear; listIterator().add; boundary fromIndex==toIndex; indices at 0, size
RISKS: Normal List.subList contract allows modification; only add/set that breaks uniqueness might
be blocked; test may rely on strict unmodifiable contract per fix
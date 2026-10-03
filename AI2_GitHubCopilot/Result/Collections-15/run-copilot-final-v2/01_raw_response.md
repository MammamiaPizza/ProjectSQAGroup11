TARGETS: addAll(int,Collection) must skip duplicates when inserting at index
ORACLES: size() after addAll(int,Collection) equals distinct element count
ORACLES: asSet().size() and list.size() must remain equal
CASES: addAll(index, coll w/ dupes of existing) – size returns unique count only
CASES: addAll at index 0, size()-1, size() with mixture of new/duplicate elements
CASES: addAll(int,Collection) with all elements already in list – size unchanged
CASES: set(index,obj) where obj exists elsewhere – remove old duplicate, add at index
CASES: add(Object) duplicate – returns false, size unchanged
CASES: add(int,Object) duplicate – must not increase size, set unchanged
RISKS: inner SetListListIterator.previous() may bypass uniqueness
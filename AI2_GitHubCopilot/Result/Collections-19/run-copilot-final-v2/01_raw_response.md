TARGETS: addAll(Collection), addAll(int,Collection) uniqueness, backing set update, boolean return.
TARGETS: add(int,E), set(int,E), subList(), iterator/listIterator add/remove with asSet.
ORACLES: SetUniqueListTest.testSetCollections444 assertions (size, contains, uniqueness) as failing
evidence.
ORACLES: java.util.List.addAll contract + SetUniqueList invariant: no duplicates, asSet consistent.
CASES: addAll(coll with duplicates); addAll(coll overlapping current list); empty coll -> false.
CASES: addAll at index 0 and index==size; duplicate before/after insertion index; subList
clear/remove.
CASES: set(index,e) where e exists at another index; iterator remove; element already in set.
RISKS: only trigger test name shown; source bodies truncated so exact mismatch is unknown.
RISKS: cannot consult fixed version; avoid assuming null/ordering behavior beyond supplied evidence.
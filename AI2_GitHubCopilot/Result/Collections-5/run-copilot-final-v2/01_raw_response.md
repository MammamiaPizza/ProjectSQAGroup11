TARGETS: SetUniqueList.addAll(int index, Collection coll) index/order logic; affected fix likely in
its iterator loop over coll.
ORACLES: Use list get(index)/size() to verify first new element lands at expected position; trigger
asserts expected index 0, actual 1.
ORACLES: Expected uniqueness preserved by internal set; only first-time elements should be inserted,
others skipped without shifting index.
CASES: addAll(0, coll) on empty list and on list with pre-existing elements; coll with internal
duplicates and duplicates vs existing set.
CASES: Boundary indexes 0 and size(); then verify element order/indices after partially filtered
bulk insert.
RISKS: Only public API (get/size/contains/asSet) visible; cause is likely off-by-one index increment
when a duplicate is skipped.
TARGETS: getRangeAxisIndex(ValueAxis), getDomainAxisIndex(CategoryAxis) – locate an axis in the
plot's internal list.
ORACLES: Index matches setRangeAxis(int,ValueAxis)/setDomainAxis(int,CategoryAxis) placement; null →
IllegalArgumentException; not found → -1.
CASES: axis at 0, axis at 1 after two adds, remove+re-add, null arg, axis never added, axis from
another plot.
RISKS: Axis.equals() may misidentify; tests must use distinct instances; behaviour for empty axis
list or cloned axes unknown.
RISKS: No specification for transient/deserialized axes; modified code may have introduced
off-by-one or wrong list iteration.
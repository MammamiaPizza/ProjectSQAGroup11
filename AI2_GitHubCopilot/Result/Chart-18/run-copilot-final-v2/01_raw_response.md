TARGETS: DefaultKeyedValues: getIndex(Comparable), removeValue(int), removeValue(Comparable),
rebuildIndex()
TARGETS: DefaultKeyedValues2D: removeRow, removeColumn, getRowIndex, getColumnIndex
ORACLES: getIndex returns -1 for removed key; getItemCount decrements after removeValue; keys list
excludes removed key
ORACLES: removeRow/Column decrements row/column count; getRowIndex/getColumnIndex returns -1 for
removed row/column key
CASES: Remove existing/nonexistent key; remove from empty; remove by index 0; sequential removals
then getIndex
CASES: removeColumn then getColumnIndex returns -1; removeRow after addRow; removeValue then
insertValue check index map
RISKS: Internal indexMap may be stale after removal; rebuildIndex() may not be called correctly
RISKS: Interactions with sortByKeys/Values unknown; limited visibility into private state;
clone/equals may be affected
TARGETS: removeColumn(key/index), removeRow(key/index), removeObject(rowKey,colKey), getObject after
removal, getColumnCount/RowCount
ORACLES: JavaDoc contract; internal consistency (key lists, counts); test expectations: getObject
returns null for removed key, not throw
CASES: remove existing column then getObject with that key; removeColumn reduces columnCount;
removeObject on last cell in column reduces columnCount
CASES: remove non-existent key throws UnknownKeyException (not ArrayIndexOutOfBounds); removeRow
null/negative index; removeColumn then setObject
RISKS: No specification document; expected behaviour (auto-removal of empty columns, null-return vs
exception) deduced only from failing test assertions
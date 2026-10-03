TARGETS: add(int,E), get(int), AVLNode.insert, AVLNode.get, iterator
ORACLES: List contract: get(i)==element added at i; iterator yields items in insertion order
CASES: add at 0, mid, end; add ascending/descending; mixed inserts; remove then re-add
CASES: after many adds, verify get(0), get(size-1), iterator next sequence; add at 0 repeatedly
RISKS: exact trigger sequence unknown; bug likely depends on rotation-offset miscalculation in
specific insert order
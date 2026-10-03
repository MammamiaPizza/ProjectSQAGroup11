TARGETS: OpenMapRealVector.sparseIterator(), OpenMapSparseIterator.hasNext()/next(),
OpenMapEntry.setValue(double)
ORACLES: No MathRuntimeException$6 (concurrent mod) after setValue; getEntry(index) matches new
value; hasNext() consistent
CASES: non-empty vector: iter next, setValue, call hasNext, iterate to end; vector with all zeros:
sparseIterator hasNext false
CASES: vector with one non-zero: setValue then next hasNext false; setValue on multiple entries
during single iteration
RISKS: buggy code throws MathRuntimeException$6 during hasNext/next after setValue; entry may hold
same index; need up-to-date entry value after setValue
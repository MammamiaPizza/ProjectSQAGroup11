TARGETS: collatedIterator(Comparator,Iterator,Iterator), collatedIterator(Comparator,Iterator...),
collatedIterator without comparator
ORACLES: Javadoc requires comparator before iteration; bug report NPE "You must invoke
setComparator() to set a comparator first."
CASES: normal merge of two sorted iterators; boundary empty/single-element iterators; next() without
setComparator→NPE; null comparator→NPE
CASES: verify hasNext/next preserve total order; test setComparator after creation works
RISKS: Incomplete API view; only testCollatedIterator trigger known; need to match buggy version NPE
message
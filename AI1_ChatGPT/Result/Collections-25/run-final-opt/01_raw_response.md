TARGETS: IteratorUtils.collatedIterator overload(s), especially no-comparator invocation used by testCollatedIterator.
ORACLES: Existing testCollatedIterator and COLLECTIONS-566 failure: no-comparator call must not throw the comparator NPE.
ORACLES: Collated output order is determined by supplied comparator or the overload's intended default ordering.
CASES: Merge multiple sorted iterators without an explicit comparator; consume all results and verify order.
CASES: Merge sorted iterators with an explicit comparator; verify comparator-defined order remains unchanged.
CASES: Include empty iterator(s) and one non-empty iterator in no-comparator merging.
CASES: Boundary: all input iterators empty; verify normal empty iteration without comparator setup failure.
RISKS: Natural/default ordering requirements and null-element behavior are not specified in the provided context.
RISKS: Available API excerpt is truncated; exact collatedIterator overload signatures are not fully shown.
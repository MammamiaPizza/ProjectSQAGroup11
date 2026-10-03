TARGETS: DOMNodePointer.namespaceIterator() and JDOMNodePointer.namespaceIterator() – iterate
aliased namespace children.
TARGETS: getRelativePositionByQName(), getRelativePositionOfElement() – produce correct sibling
index for pointer path.
ORACLES: iterator must yield distinct pointer paths with ascending indices for distinct same-ns
siblings (as expected by AliasedNamespaceIterationTest).
ORACLES: pointer path format /prefix:doc[i]/prefix:elem[j] with j incrementing for each child in
order.
CASES: Normal: 2+ sibling elements in aliased ns; expect pointers with indices [1], [2] not
duplicate [1].
CASES: Boundary: single child, deeply nested children, mixed text/element siblings in ns.
CASES: Error: namespaceIterator with unused prefix – verify empty or non-null empty iterator.
RISKS: Buggy version duplicates first element’s index for all children; fix may involve parent
pointer in iterator or asPath() logic.
RISKS: Tests must cover both DOM and JDOM pointer classes; they share similar but distinct code
paths.
RISKS: No access to fixed version; oracle derived solely from test expected output.
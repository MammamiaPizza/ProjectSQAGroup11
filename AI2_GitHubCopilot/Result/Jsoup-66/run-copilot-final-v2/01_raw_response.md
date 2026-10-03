TARGETS: clone(), nextElementSibling(), previousElementSibling(), firstElementSibling()
TARGETS: lastElementSibling(), elementSiblingIndex(), siblingElements(), doClone()
ORACLES: Javadoc: sibling methods return null/empty when no sibling; existing passing sibling tests
ORACLES: Expected after clone: sibling methods return null because clone is not in any parent's
child list
CASES: clone element with parent then each sibling query; element without parent each query
CASES: orphan element after removeChild; clone and append to different parent, query siblings
CASES: clone only child; clone middle child among multiple siblings; boundary: empty parent children
RISKS: NPE in indexInList when parent null or clone missing from parent's children
RISKS: All sibling methods share helper; clone may retain stale parent reference if not reset
RISKS: After fix, ensure elementSiblingIndex returns 0 for only child; -1 or0 for detached per spec
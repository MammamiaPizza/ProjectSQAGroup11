TARGETS: TreeList listIterator(int), TreeListIterator next/previous/remove/add/set and indexed list consistency.
ORACLES: Trigger expects iterator mutation sequence result [A], not [C]; public List get/toArray/size expose order.
CASES: Reproduce COLLECTIONS-447 iterator edits around elements A/C; assert final list order and size.
CASES: Exercise iterator from index 0, middle, and size; verify next/previous indices after mutations.
CASES: Check remove/set/add legal iterator states and resulting indexed get values.
RISKS: AVL rotations/relative positions may corrupt order after iterator-driven structural changes.
RISKS: No full trigger operation sequence or intended behavior beyond expected [A] versus actual [C].
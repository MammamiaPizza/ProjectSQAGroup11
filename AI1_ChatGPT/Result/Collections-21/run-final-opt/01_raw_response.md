TARGETS: SetUniqueList.subList(int,int), and mutation behavior of its returned List view.
ORACLES: Trigger states subList must be unmodifiable; mutators should throw UnsupportedOperationException.
CASES: Create unique list; subList valid range; assert add, remove, set, clear, addAll reject mutation.
CASES: Cover empty and non-empty sublists, including boundary ranges [0,0] and [0,size].
RISKS: Verify parent contents/set remain unchanged after rejected sublist mutations.
RISKS: Context provides no exact expected behavior for sublist reads, nested views, or invalid indices.
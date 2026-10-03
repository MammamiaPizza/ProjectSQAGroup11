TARGETS: SetUniqueList indexed addAll(int, Collection); uniqueness and insertion-order behavior  
TARGETS: decorate(List), add/addAll, set, remove, clear, subList and iterator mutation consistency  
ORACLES: Existing trigger TestSetUniqueList::testCollections307 assertion behavior  
ORACLES: SetUniqueList contract implied by list/set synchronization and duplicate exclusion  
CASES: Indexed bulk insert with new values; verify result, order, size, and asSet consistency  
CASES: Indexed bulk insert containing duplicates and values already present  
CASES: Boundary indices 0 and size; empty collection and no-new-element collection  
CASES: Mutations through iterator/listIterator/subList followed by contains and asSet checks  
RISKS: Trigger test body and implementation details are unavailable; exact regression scenario is unknown
TARGETS: FlatMapIterator.setValue, EntrySetIterator.setValue (flat & delegate modes).
ORACLES: Map.Entry.setValue must return previous value; test expects old but got new.
CASES: 1 entry, 2 entries, 3 entries (flat), 4+ entries (HashedMap delegate after convertToMap).
RISKS: setValue may not update internal key/hash arrays; after put, get(key) may return stale old
value.
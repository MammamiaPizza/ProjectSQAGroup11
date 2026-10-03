TARGETS: UnboundedFifoBuffer.add(Object) and size().
TARGETS: UnboundedFifoBuffer writeObject/readObject serialization round-trip.
TARGETS: get() to verify restored element after deserialization.
ORACLES: testCollections220 asserts size()==1 after serialize+deserialize (bug gives 0).
ORACLES: deserialized size must equal pre-serialization size; element retained.
CASES: add one element, serialize, deserialize, assert size()==1.
CASES: boundary empty buffer round-trip keeps size 0.
CASES: multiple elements and wrap (head/tail offset) preserve size/order.
CASES: add after deserialization still increments size correctly.
RISKS: internal serialization field layout not shown; only stated expected value (1 vs 0).
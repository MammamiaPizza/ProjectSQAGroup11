TARGETS: equals(Object), hashCode(), size(), getKey(int) after serialization round-trip
TARGETS: All MultiKey constructors (2..5 args, array, array+clone) must survive serialization
ORACLES: Deserialized object equals original and has same hashCode; size() returns original count
ORACLES: getKey(i) returns same value as original; getKeys() elements match original (not
necessarily same array)
CASES: Normal: 2-key ("a","b"), 5-key (1..5), array constructor with/without clone
CASES: Boundary: keys with null values, array with null element, array of exactly 2 elements
(trigger)
CASES: Error: deserialize a 2-key MultiKey and assert size()==2, getKey(0)!=null, equals works
RISKS: The buggy MultiKey may lose internal keys array after deserialization, causing null returns
RISKS: Only the buggy version is available; test must expose the known failure (expected:<2> but
was:<null>)
TARGETS: toString() after deserialization; verify it returns non-null with expected key
representation
TARGETS: CSVRecord.get(String name) when record mapping is inconsistent (index out of bounds)
TARGETS: CSVRecord.isConsistent(), isMapped(String name), isSet(String name) to detect inconsistency
ORACLES: get(String name) must throw IllegalArgumentException if mapping index >= size or mapping
null and record is inconsistent
ORACLES: get(String name) returns correct value for known valid name; isConsistent() returns false
if any mapping index out of range
CASES: normal consistent mapping, boundary: name mapped to index just within/outside size, error:
name not mapped, inconsistent record
CASES: get with null name, empty name, whitespace; get after mutation of underlying mapping or
values
RISKS: limited context on mapping storage; inconsistent record definition inferred from trigger test
and method signatures
TARGETS: putAll(int,Map) handling null keys/values.
TARGETS: put(int,K,V) with null key/value or existing null entry.
TARGETS: putAll index-bounds validation.
ORACLES: no exception for valid index; IndexOutOfBounds for index>size; order preserved.
CASES: putAll with null-valued entries at index 0, mid, size.
CASES: putAll after putting null key, map with mixed null/non-null keys.
CASES: putAll with index = size (append) and map containing only null values.
CASES: putAll with index out of range (negative, >size) -> IndexOutOfBounds.
RISKS: HashMap-backed decorated map supports null key; insertOrder indexOf(null)=-1 may cause
off-by-one.
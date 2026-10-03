TARGETS: values(), valueList(), and ValuesView iterator/get with null map values  
ORACLES: Trigger test testCOLLECTIONS_474_nullValues; collection size/order must match map entries  
CASES: Multiple inserted keys with null values; iterate all values without IndexOutOfBoundsException  
CASES: Null at first, middle, and last insertion positions; mixed null and non-null values  
CASES: valueList().get(index) and values().iterator().next() across every valid index  
RISKS: Null values may be mistaken for absent entries, desynchronizing insertion order and value traversal  
RISKS: Context lacks implementation details and explicit expected assertions beyond the trigger failure
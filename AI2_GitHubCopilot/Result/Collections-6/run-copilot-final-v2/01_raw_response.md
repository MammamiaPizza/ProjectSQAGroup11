TARGETS: Flat3Map.get, containsKey, size, and delegate-map conversion
(convertToMap/createDelegateMap)
ORACLES: expected value 1 from TestFlat3Map::testCollections261; standard Map contract semantics
CASES: put then get same key; get after delegate conversion; get null key; containsKey null key;
remove then size
RISKS: only failure assertion given (expected 1 was 0); exact get/path not shown in provided trigger
info
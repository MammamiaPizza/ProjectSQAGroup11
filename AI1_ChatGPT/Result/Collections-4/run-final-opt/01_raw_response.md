TARGETS: MultiValueMap.put(Object,Object) with list-backed and set-backed decorated maps.  
TARGETS: MultiValueMap.putAll(Object,Collection) for adding a collection under one key.  
ORACLES: Trigger assertions: put with List/Set must return "a", not null.  
ORACLES: Trigger assertion: putAll(key, collection) must return true when values are added.  
CASES: decorate(map, ArrayList.class), put absent key/value; verify returned value.  
CASES: decorate(map, HashSet.class), put absent key/value; verify returned value.  
CASES: putAll with nonempty collection on absent key; verify true and stored mappings.  
RISKS: Exact duplicate/null/empty-collection semantics are not specified by supplied context.
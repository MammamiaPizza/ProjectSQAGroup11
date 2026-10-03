TARGETS: BeanPropertyMap.remove(SettableBeanProperty); case-insensitive name lookup/removal behavior.  
TARGETS: find(String), withCaseInsensitivity(boolean), and insertion-order property tracking after removal.  
ORACLES: Trigger expects removal of "businessAddress" not to throw NoSuchElementException under case-insensitivity.  
ORACLES: Public size(), find(String), iterator(), and getPropertiesInInsertionOrder() provide observable results.  
CASES: Construct case-insensitive map; remove a property whose logical name differs only by letter case.  
CASES: Verify removed property is no longer found by case variants and size/order reflect one removal.  
CASES: Case-sensitive map retains distinct behavior for differently cased lookup/removal names.  
CASES: Remove existing exact-name property and verify remaining properties remain retrievable.  
RISKS: SettableBeanProperty construction/setup details are not provided; use available project test utilities only.  
RISKS: No alternate-version behavior is available; derive expectations solely from trigger and exposed API.
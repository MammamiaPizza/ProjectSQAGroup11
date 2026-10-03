TARGETS: load(InputStream): ensure it doesn't overwrite static include field.
ORACLES: Default include="include"; getInclude() must return that after any load.
CASES: load with property key "include"=value; assert getInclude() returns "include".
RISKS: static include shared across tests; must reset after each test.
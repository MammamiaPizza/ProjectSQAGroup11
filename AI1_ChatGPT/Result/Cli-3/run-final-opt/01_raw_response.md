TARGETS: TypeHandler.createValue(String,Object), createValue(String,Class), and createNumber(String)
ORACLES: PatternOptionBuilderTest.testSimplePattern assertion for number flag n; its expected runtime value/type
CASES: Decimal input "4.5" through PatternOptionBuilder number option; verify equality and concrete Number behavior
CASES: Integral numeric strings through createNumber/createValue; compare against existing test expectations
CASES: Invalid numeric text and null input where supported; assert documented/current exception behavior
RISKS: Failure text shows equal rendering ("4.5"); mismatch may be Number subtype rather than numeric value
RISKS: No implementation or full test source is provided; derive expectations only from available tests/API
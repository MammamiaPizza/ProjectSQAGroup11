TARGETS: Parser.processProperties(Properties), Parser.parse(Options,String[],Properties,boolean)
ORACLES: testPropertyOptionFlags expects property-set boolean options to be recognized after parsing
CASES: Normal: property key (longOpt) with value "true" sets flag; property value null/empty sets
flag
CASES: Boundary: property value "false" not setting flag; property overridden by explicit CLI arg
CASES: Error: property with unrecognized key should be silently ignored per existing CLI-201 spec
RISKS: Exact bug in processOption vs processProperties unknown; no test oracle beyond ValueTest
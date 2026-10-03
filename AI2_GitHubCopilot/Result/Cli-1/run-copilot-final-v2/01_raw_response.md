TARGETS: getOptionValue(char), getOptionValue(String), getOptionObject, hasOption, getOptionValues
ORACLES: API spec: getOptionValue(opt) returns null if opt not set; getOptionObject returns null
similarly
ORACLES: getOptionValue(opt, defaultValue) returns defaultValue when opt absent; hasOption returns
false
CASES: option char/String not added; null opt arg; empty string opt; option added but no value;
option with value
CASES: getOptionValues on absent option→empty/null array; getOptionObject on absent→null;
defaultValue fallback
RISKS: Only CommandLine API visible; no access to Option class or parser to pre-populate; rely on
inferred contracts
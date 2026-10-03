TARGETS: OptionBuilder static-state carryover across multiple create() calls;
Option.setType/getType; typed value parsing in CommandLine.
ORACLES: testTwoCompleteOptions expects getType()==String.class for both options;
testGetParsedOptionValue expects "foo" not null.
CASES: create two full options successively with same builder config → both retain type; parse a
value with type=String; parse with type=null; multiple create() calls verify property reset; build
with optional args.
RISKS: OptionBuilder.reset() semantics unknown; static side effects; no internal state access;
limited to public API and supplied triggers.
TARGETS: JsDocInfoParser parseFunctionType, parseParametersType, parseTypeName, and any method
consuming new: in type expressions.
ORACLES: Expect no warnings; existing test methods asserting clean parse (no "Bad type annotation"
errors) via ErrorReporterParser.
CASES: Type annotations with function(new:Type), function(new:Type):Ret, {new:Type}, nested new:
with unions, missing colon, multiple new:, whitespace variations.
RISKS: Only failure descriptions available; exact annotation strings not given, so must infer from
typical Closure structural constructor syntax and bug report.
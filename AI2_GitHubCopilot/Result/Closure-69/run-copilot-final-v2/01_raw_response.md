TARGETS: TypeCheck.visitCall — emit warning when 'this' type incompatible with called function's
expected this type.
ORACLES: Compiler warning count (expected >0) from test assertions; warning message text pattern.
CASES: Normal matching this; incompatible this type (error case); this on prototype method;
null/undefined this.
RISKS: Warning message text may not be documented; risk of false warnings for valid dynamic this;
may break other tests.
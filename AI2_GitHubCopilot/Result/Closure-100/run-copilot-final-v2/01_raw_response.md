ARGETS: shouldReportThis(Node,Node), getFunctionJsDocInfo – detect global-this context of
static/instance functions.
ORACLES: CheckGlobalThisTest .assertNoErrors / .assertError(JSC_USED_GLOBAL_THIS, count) for given
JS snippets.
CASES: static function with @this annotation; static function without @this; inner function;
prototype method; constructor; top‑level this.
RISKS: Test JS inputs not visible; risk of mismatched expected count due to incomplete JSDoc
analysis; private methods limit direct unit testing.
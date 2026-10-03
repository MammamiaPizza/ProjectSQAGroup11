TARGETS: CodeGenerator.addExpr() handling of FUNCTION/DO nodes when parent is BLOCK in IE/SAFARI
compat mode.
ORACLES: CodePrinterTest.testFunctionSafariCompatiblity expects BLOCK-wrapped FUNCTION to remain in
braces ({...}).
ORACLES: CodePrinterTest.testDoLoopIECompatiblity expects DO-WHILE inside labeled BLOCK to remain in
braces ({...}).
CASES: FUNCTION as only child of BLOCK in labeled if-body (Safari mode) — verify braces are not
dropped.
CASES: DO-WHILE as only child of BLOCK in labeled if-body (IE mode) — verify braces are not dropped.
CASES: FUNCTION/DO with siblings in BLOCK — braces must always stay.
RISKS: addExpr may strip BLOCK braces when single child is FUNCTION or DO, breaking compat output.
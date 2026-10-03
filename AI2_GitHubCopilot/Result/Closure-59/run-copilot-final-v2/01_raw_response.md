TARGETS: Compiler.check() — global‑this check (suppressed by --check_global_this_level=off)
TARGETS: Compiler.compile() / compileInternal() — pipeline that invokes the check
ORACLES: Compiler.getErrors()/getWarnings(): empty arrays when global‑this check is off
ORACLES: CommandLineRunnerTest.testCheckGlobalThisOff expects no warnings/errors on valid JS
CASES: JS with "this" at top‑level; check off → 0 errors, 0 warnings
CASES: Same JS with check on → warning (e.g., JSC_USED_GLOBAL_THIS) appears
CASES: JS without top‑level "this"; check off → 0 warnings (baseline)
CASES: "this" inside a function (should never warn) with check off → 0 warnings
RISKS: Exact suppressed‑warning key not in provided Compiler API; need to infer from message text
RISKS: CompilerOptions enabling/suppression not visible in given signatures; rely on initOptions
TARGETS: MethodCompilerPass signature gathering, extern-method detection, prototype-parent processing.
TARGETS: RenameVars ProcessVars, name eligibility, global/local assignment, prior-map reuse.
ORACLES: Existing trigger assertions/compiler diagnostics are the only stated expected-result sources.
CASES: Getter/property calls with object-literal methods; preserve emitted call instead of empty output.
CASES: Separate same-named methods with 1–2 args; a 3-arg call must not report wrong argument count.
CASES: Getter-related input that previously caused INTERNAL COMPILER ERROR; compilation must complete.
CASES: Rename variables involving `$super`/export naming as covered by testDollarSignSuperExport2.
RISKS: No source bodies, compiler harness API, or exact RenameVars expected output is provided.
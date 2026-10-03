TARGETS: CommandLineRunner constructor & flag parsing for --process_closure_primitives
TARGETS: BooleanOptionHandler handling of --process_closure_primitives
TARGETS: Compiler/CompilerOptions affected by --process_closure_primitives (goog.provide/require
processing)
ORACLES: Existing failing test testProcessClosurePrimitives expected output transformation
CASES: Flag present (true) / absent (default false) / explicitly false on valid goog.provide/require
JS
CASES: JS with no goog.provide/require (no-op), multiple goog.provide, goog.require namespaces
CASES: Combined with --js, --js_output_file, --externs; boundary: empty JS input
RISKS: Exact transformation string may be version-specific; expected behavior only inferred from
test name
RISKS: Only partial API signatures provided; cannot see CompilerOptions or transform internals
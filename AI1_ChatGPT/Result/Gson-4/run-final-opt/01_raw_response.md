TARGETS: JsonReader top-level scalar peek/read/skipValue behavior in strict and lenient modes.
TARGETS: JsonWriter top-level scalar value emission and root-state validation.
ORACLES: Trigger test names/failures specify top-level values must not be rejected at column 1.
CASES: Reader top-level string, number, boolean, null; verify peek then matching next* result.
CASES: Reader skipValue for each top-level scalar, then EOF/end-of-document behavior.
CASES: Writer top-level string, number, boolean, null; verify exact serialized scalar output.
CASES: Writer top-level array/object remain valid; incomplete/nested-state errors remain checked.
RISKS: Strict-mode malformed-input rules are unspecified beyond reported top-level-value rejection.
RISKS: Available context truncates JsonReader API details, including full skipValue signature/semantics.
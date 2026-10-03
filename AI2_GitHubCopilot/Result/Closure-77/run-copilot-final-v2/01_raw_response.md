TARGETS: static jsString, escapeToDoubleQuotedJsString, identifierEscape, regexpEscape (escaping
entry points)
TARGETS: CodeGenerator output logic that uses above methods for string literals in JS code
ORACLES: Expected from testZero: null char escaped as "\0", not broken "[u000]0" sequence
ORACLES: ECMA-262 string literal escape rules: \0 for NUL, \xNN, \uNNNN for valid codepoints only
ORACLES: Existing testZero expected string as ground-truth for null-char codegen output
CASES: Null char U+0000 (boundary), control chars 0x01-0x1F, supplementary codepoints, unpaired
surrogates
CASES: Zero at start/middle/end of string, repeated zeros, string containing only zero
CASES: Valid Unicode escapes like \u0041 (A) to ensure they are handled correctly alongside NUL
RISKS: No diff or full source; bug could be in any escaping method or in addExpr invocation
RISKS: Cannot inspect Node structure or how string values are embedded; limited to API signatures
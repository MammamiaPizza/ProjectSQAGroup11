TARGETS: doubleMetaphone(String, boolean), especially alternate encoding of "Angier".
ORACLES: Trigger specifies doubleMetaphone("Angier", true) returns "ANJR", not "ANKR".
CASES: Verify primary/alternate overload distinction for "Angier" where applicable.
CASES: Boundary: null, empty, and whitespace input behavior via public overloads.
CASES: Max-code-length effects using getMaxCodeLen/setMaxCodeLen and encoded output truncation.
RISKS: Only one reported alternate-output example; broader phonetic expectations are not provided.
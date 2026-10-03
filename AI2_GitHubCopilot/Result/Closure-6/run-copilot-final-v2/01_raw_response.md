TARGETS: registerMismatch, containsForwardDeclaredUnresolvedName, type-redefinition detection in
setJSType/getJSType.
ORACLES: JSC_DUP_VAR_DECLARATION warning count; equals/hashCode/toString on TypeMismatch for report
accuracy.
CASES: enum→function redeclaration (expected 2 warnings); redeclare same type (0 warnings);
interface not implemented.
RISKS: Only public API visible; internal type-checking flow hidden; failure suggests missing
detection of duplicate declaration with enum.
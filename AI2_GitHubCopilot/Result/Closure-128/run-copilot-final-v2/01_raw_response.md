TARGETS: CodeGenerator method printing object literal computed keys (likely uses
addJsString/jsString and isSimpleNumber).
ORACLES: Expected output from CodePrinterTest::testIssue942: var x={[0]:1} (numeric key, no quotes).
ORACLES: Closure JS style rule: numeric-looking string keys in object literals should be printed as
number literals.
CASES: Normal: string key "0" printed as [0]; "1", "42", "-1", "1.5" as number literals.
CASES: Boundary: "00" (leading zero), "0x0" (hex), "1e2" (scientific) may remain quoted; negative
zero.
CASES: Error: non‑numeric string keys e.g. "abc" must stay quoted; empty string "".
RISKS: addJsString is also used for string literals in other expressions; change must limit to
object key context.
RISKS: isSimpleNumber may not handle all numeric forms (hex, sci‑notation), causing unexpected
quoting regressions.
RISKS: The fix could alter bracket‑property access (e.g. obj["0"]) if same code path is shared.
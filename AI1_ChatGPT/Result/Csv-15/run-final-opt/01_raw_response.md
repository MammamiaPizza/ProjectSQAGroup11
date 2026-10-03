TARGETS: CSVFormat.format and print escaping/quoting for values beginning with non-ASCII Euro (€).
ORACLES: Trigger assertion expects first field unquoted: "€,Deux" (as shown by expected failure text).
CASES: Format/print record with "€" first field and "Deux" second field; verify delimiter and no leading quote.
CASES: Compare Euro at first position versus ordinary non-special first characters under the same format.
CASES: Boundary values: empty field, delimiter/quote/line-break-containing fields to preserve required quoting behavior.
RISKS: Expected format settings and complete expected output are truncated; rely only on stated trigger expectation.
RISKS: Do not assume behavior for other Unicode characters, quote modes, record separators, or parser round trips.
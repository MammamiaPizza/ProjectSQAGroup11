TARGETS: StrBuilder appendFixedWidthPadLeft/Right null handling (LANG-412 left/right triggers).
ORACLES: Trigger tests must not throw NullPointerException for the affected left/right paths.
ORACLES: Result formatting should follow configured null text and fixed-width padding behavior.
CASES: Null object with default null text for left padding; verify no NPE and width-sized output.
CASES: Null object with default null text for right padding; verify no NPE and width-sized output.
CASES: Configured null text shorter/equal/longer than width for both padding directions.
CASES: Boundary widths: zero and widths relative to null-text length.
RISKS: API details and exact expected strings are truncated; derive assertions from existing test conventions.
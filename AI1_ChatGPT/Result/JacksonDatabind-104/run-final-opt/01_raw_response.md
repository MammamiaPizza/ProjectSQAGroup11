TARGETS: StdDateFormat date serialization/formatting for ISO-8601 extended years and BCE years.
ORACLES: Trigger expectations: year 10204 formats "+10204-01-01T00:00:00.000..."; BCE formats "+0000-01-01T00:00:00.000+...".
CASES: Serialize Date/calendar at year 10204; verify leading '+' and complete five-digit year.
CASES: Serialize BCE boundary corresponding to year zero; verify "+0000", not "0001".
CASES: Cover ordinary four-digit CE ISO-8601 output to guard unchanged standard-year formatting.
RISKS: Exact timezone suffix and ellipsized trigger text are unavailable; set an explicit timezone if existing test utilities permit.
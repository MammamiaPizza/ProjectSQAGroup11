TARGETS: Partial(DateTimeFieldType[],int[]) and Partial(DateTimeFieldType[],int[],Chronology)
constructors must check for duplicate types
ORACLES: IllegalArgumentException expected when types array contains duplicate entries (as per Ex7
trigger test)
CASES: Normal: non‑duplicate [year, month] succeeds; boundary: single‑type [year] succeeds; error:
duplicate [year, year] must throw IAE
CASES: Error: duplicate [year, month, year] and [year, year, year] in both 2‑arg and 3‑arg
(Chronology) constructors must throw IAE
RISKS: No explicit API doc on duplicates; exact exception message unknown; UnsupportedDurationField
modified but likely unrelated to constructor validation
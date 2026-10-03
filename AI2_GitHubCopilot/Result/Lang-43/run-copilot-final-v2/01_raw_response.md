TARGETS: parseFormatDescription (infinite-loop/OOM source), seekNonWs, next, applyPattern,
ExtendedMessageFormat constructors
ORACLES: OutOfMemoryError absence is primary; toPattern() round-trip must preserve escaped quotes;
applyPattern must not hang/infinite-loop
CASES: single/double escaped quote (''), embedded quotes in format descs, quotes at start/end,
no-quote simple patterns, deeply nested quotes
RISKS: No expected-output reference beyond avoiding OOM; insufficient context to know exact correct
escaped-quote behavior; must infer from adjacent method logic only
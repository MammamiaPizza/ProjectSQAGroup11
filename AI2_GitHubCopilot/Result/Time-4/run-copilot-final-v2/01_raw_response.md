TARGETS: Partial.with(DateTimeFieldType,int) when fieldType already present; size; getValue;
getFieldType; Property setCopy; getField.
ORACLES: size unchanged after replacement; value updated; chronology retained; no duplicate field
types; expected values from equivalent Partial constructor.
CASES: normal — new fieldType (size+1); existing fieldType → replace value, size unchanged; same
value; invalid value (exception); unsupported fieldType (exception).
CASES: boundary — max/min value via chronology; field added at position 0 or last; empty Partial
(size 0) with replacement?; null fieldType (throws).
CASES: cross‑chronology — Partial(ISO) replaced with BuddhistEra field may keep ISO?; Partial(ISO)
received Islamic month number → chronology conflict.
RISKS: chronology recomputation when replacing a field is untested; may silently allow duplicate
field types, causing later size/value mismatches.
RISKS: no insight into internal chronology update rules; rely only on public API and trigger test;
need to ensure tests avoid assumption of specific chronology sharing.
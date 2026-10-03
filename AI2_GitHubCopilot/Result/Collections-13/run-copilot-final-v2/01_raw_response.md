TARGETS: convertProperties(Properties) with null values; put/putAll with null value; load/include
with missing file
ORACLES: No NullPointerException; output preserves non-null entries; null values handled gracefully
CASES: Normal: empty Properties, single key; Error: null input, mixed null/non‑null values
CASES: Boundary: duplicate includes, missing include file, interpolation of unresolvable references
RISKS: Exact failing method unknown; convertProperties is likely but not confirmed; only class
outline seen
RISKS: No internal logic visible; must test all public methods that accept null or sparse inputs
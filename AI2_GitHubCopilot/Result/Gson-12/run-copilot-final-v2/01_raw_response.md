TARGETS: JsonTreeReader.skipValue() stack pops/peeks; popStack, peekStack, push; array nesting
end-of-element tracking.
ORACLES: skipValue should consume exactly one value and leave reader at next token (or end); no
underflow/exception.
CASES: skip simple value; skip empty object {}; skip empty array []; skip filled object; skip filled
array; skip nested empty.
RISKS: Only two triggers provided; other skipValue paths (e.g., arrays, multiple values) may also
hide stack underflow.
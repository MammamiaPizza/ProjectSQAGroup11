TARGETS: JsonReader.nextLong, nextInt, peek, nextString, nextUnquotedValue, peekNumber
ORACLES: Unquoted numeric keys must parse as long/int, not STRING
CASES: Unquoted long keys (e.g. 123L), integer keys (42), mixed string+number, boundary
Long.MAX_VALUE/MIN_VALUE
RISKS: Only bug report and triggers available; no source diff for exact fix lines
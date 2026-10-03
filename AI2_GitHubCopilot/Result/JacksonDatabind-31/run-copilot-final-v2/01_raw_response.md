TARGETS: TokenBuffer.writeFieldName, getOutputContext, asParser (context-name propagation)
ORACLES: JsonParser.getCurrentName must match last-written field name in active object context
ORACLES: check TokenBuffer.asParser.getCurrentName after writing objects/arrays
CASES: writeFieldName("a"), writeStartObject, writeFieldName("b") → asParser should have name "b"
CASES: nested array inside object: writeFieldName, writeStartArray, writeEndArray → current name
unchanged
CASES: root-level field name in outer object vs. nested object context
CASES: empty object, no field name written → getCurrentName null
CASES: writeFieldName(null) → should behave gracefully
RISKS: no access to expected-value sources beyond failure message; internal _writeContext sync
unknown
TARGETS: parseTypeString, parseAndRecordTypeNode, parseTypeExpression, parseFunctionType,
parseRecordType
ORACLES: Assert zero warnings from ErrorReporterParser after parseTypeString; no "Unexpected end of
file" message
CASES: Malformed but acceptable type strings: "function(", "{prop:", "Array<", union trailing "|",
empty string
RISKS: Fix limited to reported issue; no full coverage of all JsDoc parsing branches; only one test
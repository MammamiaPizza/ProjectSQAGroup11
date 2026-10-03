TARGETS: FromXmlParser.nextToken() parsing nested unwrapped lists with empty elements;
_mayBeLeaf,_isEmpty logic.
ORACLES: Trigger test assertions: expect 1 entry for empty nested list; spec: empty element => empty
string entry in unwrapped list.
CASES: Empty element in unwrapped list; nested empty list; mixed empty/non-empty; deep nesting;
whitespace-only text; multiple empty siblings.
RISKS: Internal state (XmlTokenStream,_mayBeLeaf) not observable; fix may change private token
resolution; limited visibility into helper _isEmpty().
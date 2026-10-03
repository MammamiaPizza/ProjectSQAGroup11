TARGETS: DOMNodePointer.getValue/stringValue and JDOMNodePointer.getValue/addContent whitespace handling.
ORACLES: XMLSpaceTest trigger assertions define expected XML-space-preserved/trimmed string values.
CASES: DOM/JDOM xml:space="preserve": value " foo " should yield "foo" per test failure oracle.
CASES: Nested content, with/without comments, must not yield "foo;bar; baz " when expected empty/structured value.
CASES: Nested DOM content must match expected multiline "foo..." result; include comments as text-boundary case.
RISKS: Exact XML fixtures, XPath expressions, and full expected nested strings are not provided.
RISKS: Do not infer behavior beyond existing XMLSpaceTest assertions; use JUnit 3 and Ant context.
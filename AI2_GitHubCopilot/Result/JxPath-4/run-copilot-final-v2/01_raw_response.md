TARGETS: DOMNodePointer.getValue() and private stringValue(Node); JDOMNodePointer.getValue(); both
equalStrings()
TARGETS: text accumulation over nested element/text/comment children and xml:space preserve handling
in DOM and JDOM
ORACLES: XMLSpaceTest assertion diffs: "foo" without surrounding spaces; nested text joined from
child nodes
ORACLES: JXPATH-83 bug summary and XMLSpaceTest names Preserve/Nested/NestedWithComments; exact
nested golden from tests
CASES: element with single text child and leading/trailing whitespace (testPreserveDOM/JDOM)
CASES: nested element with mixed text/element children (testNestedDOM/JDOM) and with comments
(testNestedWithComments)
CASES: DOM vs JDOM parity on equivalent XML, including empty text collections and whitespace-only
text nodes
RISKS: private DOM stringValue(Node) not directly invocable; verify via public getValue()
RISKS: JDOM nested expected shown as empty "<>"; DOM nested expected truncated, so golden values
need test source inspection
RISKS: Do not infer beyond supplied diffs or use another program version to reconstruct expected
strings
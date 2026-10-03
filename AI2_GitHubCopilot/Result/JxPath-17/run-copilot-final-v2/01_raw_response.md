TARGETS: DOMAttributeIterator ctor/setPosition, JDOMAttributeIterator ctor/setPosition (attribute
iteration across multiple parent nodes)
ORACLES: DOMModelTest/JDOMModelTest testAxisAttribute assertions; expected all attrs from all
matched nodes e.g., [10%,20%] after @*
CASES: @* on path matching 2+ nodes each with attrs; specific attr name across multiple nodes
CASES: path with nodes having no attrs; single node with multiple attrs; first node no attr second
has attr
RISKS: Iterator may collect attrs only from first parent node; need to accumulate across all parents
RISKS: Wildcard QName (*) expansion may skip early nodes; namespace-qualified attrs may be
mishandled
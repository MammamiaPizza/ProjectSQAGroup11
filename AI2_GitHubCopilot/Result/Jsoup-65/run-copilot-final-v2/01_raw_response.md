TARGETS: HtmlTreeBuilderState.process StartTag/EndTag transitions for template inside table scopes
(Table/InTableBody/InRow/InCell).
TARGETS: HtmlTreeBuilder.insertNode, clearStackToContext, inSpecificScope, isElementInQueue,
formattingElements list.
ORACLES: Asserted serialized tree/string from
org.jsoup.parser.HtmlParserTest.testTemplateInsideTable.
ORACLES: HTML5 parsing expectations implied by the single trigger assertion (template must not stay
inside table).
CASES: Normal: <table><template>...</template></table>, template with inner rows/cells, template
after/between rows.
CASES: Boundary: empty template, whitespace-only content, adjacent/nested templates, unclosed
template at EOF/table end.
CASES: Error: foster-parenting, missing <tr>/<td> paths (handleMissingTr, closeCell, exitTableBody).
CASES: Null char (Constants.nullString), unsupported tags via anythingElse, same-formatting
elements.
RISKS: Exact expected output string and buggy diff are not supplied; oracle limited to the trigger
method.
RISKS: Private state methods unreachable directly; must test via HtmlParser/TreeBuilder public parse
entry point.
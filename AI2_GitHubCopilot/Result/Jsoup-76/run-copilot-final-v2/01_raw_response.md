TARGETS: HtmlTreeBuilderState process(), especially states handling pre/code/text area tags and
initial whitespace newline skipping behavior
ORACLES: HTML spec-compliant parsing; text content after <pre>/<textarea>/<code> tags should NOT
consume a leading newline if immediately following the start tag
CASES: Leading newline after <pre> (boundary), no-newline after <pre> (normal), empty <pre></pre>,
nested tags inside <pre>, <textarea> newline handling, attribute-rich <pre> tags
RISKS: Enum state machine complex; behavior may differ across InBody/AfterHead states; limited to
public HtmlParserTest context only
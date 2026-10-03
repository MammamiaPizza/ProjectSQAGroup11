TARGETS Element.html() output respects Document.OutputSettings.prettyPrint=false
TARGETS Element.toString() (via outerHtml) uses no indentation/newlines for compact
ORACLES expected compact HTML: no leading/trailing newlines, no added indentation
CASES normal: nested block elements, inline elements, text with internal newlines
CASES boundary: element with only blank text, single child, detached element
CASES error: element with newline-only text; element after change of output settings
RISKS exact expected string unknown; may need to cover many HTML tag combinations
RISKS bug may be in private helper html(StringBuilder) - must test via public methods
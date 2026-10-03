TARGETS: process(Token.Comment) via HtmlTreeBuilderState or TreeBuilder; processStartTag(String) for
table elements
ORACLES: testInvalidTableContents asserts comment precedes specific text; Jsoup HTML parsing spec
(WHATWG)
CASES: table with comment then text; comment before/after <td>; nested table comment; multiple
comments
RISKS: No patch access; bug likely in foster-parenting or insertion mode; test both HtmlParser and
XmlTreeBuilder
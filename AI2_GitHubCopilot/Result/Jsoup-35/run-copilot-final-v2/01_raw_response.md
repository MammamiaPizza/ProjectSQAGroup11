TARGETS: HtmlTreeBuilderState.process() for <a> start/end tags; anythingElse fallback;
adoption-agency/reconstruction logic
ORACLES: Compare outerHtml() or html() output to expected string; verify DOM node type=Element, tag
name="a", child text presence
CASES: unclosed <a> at EOF; nested <a> without close; multiple consecutive unclosed <a>; <a> with
text; <a><b>text</b>; empty <a />; <a> around block (<div>)
RISKS: No HTML5 spec available; test expected output is the only oracle; may miss edge cases for
formatting-element list manipulation
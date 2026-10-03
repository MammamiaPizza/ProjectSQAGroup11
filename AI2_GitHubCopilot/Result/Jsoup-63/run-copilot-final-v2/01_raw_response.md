TARGETS: HtmlTreeBuilder.process(Token) self-closing tag validation; Tokeniser.emit error message
generation.
ORACLES: Test assertions: non-void self-closing tags => error "Tag cannot be self closing; not a
void tag"; void tags => 0 errors.
CASES: <div/> (non-void) => error; <br/> (void) => no error; <p/> => error; <img/> => no error;
self-closing in nested tags, with attributes.
RISKS: Only error message and error count observable; Tokeniser internal error–logic signatures not
fully provided; deeper parsing side effects of self-closing flag not directly testable.
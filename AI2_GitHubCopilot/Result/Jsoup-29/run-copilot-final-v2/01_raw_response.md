TARGETS: Document.title()—get title, Document.title(String)—set title, Document.normalise(),
Document.head()/body()
ORACLES: DocumentTest.testTitles expected title text; triggered assertEquals "Hello there now" vs
"Hello \n"
CASES: title() with text nodes split across newline; title setter normalizes whitespace; title()
trim/empty when absent
RISKS: Limited to title()/title(String) normalisation path; no full spec or non-title behavior
visible
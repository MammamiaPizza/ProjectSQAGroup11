TARGETS: Document.normalise(), especially ordering of nodes moved into the document body  
TARGETS: Document.body(), Document.head(), and Document.outerHtml() after normalization  
ORACLES: ParserTest::createsStructureFromBodySnippet expects body text "foo bar baz"  
ORACLES: Document.title() contract: trimmed title or empty string when no title exists  
CASES: Parse/body-snippet content with text before and after nested/body elements; verify text order  
CASES: Normal document with html/head/body structure; verify body and head remain accessible  
CASES: Boundary empty body or absent title; verify title() returns empty string  
RISKS: Available context does not expose parser input/API details or intended full normalization rules
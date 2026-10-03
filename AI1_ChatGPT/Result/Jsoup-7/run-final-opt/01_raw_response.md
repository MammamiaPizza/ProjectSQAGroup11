TARGETS: Document.normalise(), head(), body(), outerHtml(); normalization of document structure.  
ORACLES: Trigger test expected outerHtml places empty <noscript></noscript> in <head>.  
ORACLES: Expected source is DocumentTest::testNormalisesStructure and reported expected/actual HTML.  
CASES: Normalise document containing head content, script, noscript, and body paragraphs.  
CASES: Verify body content becomes <body><p>two</p><p>three</p></body>, not nested bodies in noscript.  
CASES: Verify noscript remains in head and has no incorrectly moved body children.  
RISKS: Context lacks full DocumentTest input/setup and parser behavior; derive assertions only from shown failure.
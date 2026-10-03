TARGETS QueryParser.parse handling of ':matchText' pseudo-selector token in findElements.
ORACLES splitOnBr/textAsElements tests should pass; parse must not throw SelectorParseException.
CASES valid "p:matchText","p:matchText:first-child" combos; boundary empty query, improper
":matchText"; error unrecognized pseudo.
RISKS Unclear if :matchText expects a regex argument; semantics of PseudoTextElement undefined
without full source.
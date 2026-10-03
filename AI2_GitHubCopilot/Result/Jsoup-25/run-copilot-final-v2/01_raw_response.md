TARGETS: Tag.preserveWhitespace() must return true for textarea/pre tags
TARGETS: Tag.valueOf("textarea") and register(Tag) whitespace tag setup
TARGETS: Tag.valueOf("pre") preserveWhitespace behavior
ORACLES: HtmlParserTest::preservesSpaceInTextArea expects preserved "One[\n Two"
ORACLES: parsed TextNode equals source content exactly for whitespace-preserving tags
CASES: textarea preserves inner newlines/spaces; normal div collapses/trims
CASES: pre preserves whitespace; unknown/custom tag does not preserve
CASES: valueOf null/blank tagName normalization and isKnownTag checks
RISKS: only trigger test visible; Tag internal field mapping may be incomplete
TARGETS: Tag.valueOf(String), Tag.preserveWhitespace(), and textarea tag metadata/lookup behavior.
ORACLES: HtmlParserTest.preservesSpaceInTextArea is the only stated expected-result source.
CASES: Known "textarea" lookup preserves whitespace when parsed as textarea text.
CASES: Verify normal known-tag lookup and preserveWhitespace result for textarea.
RISKS: Trigger assertion text is truncated; exact expected whitespace content is unavailable.
RISKS: No alternate version or complete Tag implementation/specification is provided.
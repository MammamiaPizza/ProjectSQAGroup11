TARGETS: QueryParser.parse(String), especially contains parsing and attribute selector parsing.
TARGETS: TokenQueue.chompBalanced(char,char) quote/balance handling used by QueryParser.
ORACLES: Trigger assertions require Selector.SelectorParseException for both malformed selectors.
CASES: Single-quote-containing :contains selector from testParsesSingleQuoteInContains.
CASES: Unclosed attribute selector from exceptionOnUncloseAttribute.
RISKS: Ensure malformed input is translated to SelectorParseException, not IllegalArgumentException.
RISKS: Exact selector literals and intended valid single-quote behavior are not provided.
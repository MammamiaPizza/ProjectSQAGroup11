TARGETS: QueryParser.parse (main entry), combinator, consumeSubQuery; CombiningEvaluator.Or.add;
byAttribute for attr selectors containing commas
ORACLES: existing test assertions (e.g., element count=2, no PatternSyntaxException); Jsoup
semantic: comma is OR union of selectors
CASES: normal: "div, p" → union; boundary: "[attr=a,b]", combined combinators "+ , >"; error:
leading/trailing comma, unclosed brackets
RISKS: regex-based split may break on commas inside [attr=val]; grouping of combinators after commas
may yield wrong element count
TARGETS: Selector.select(String, Element), parsing/evaluation of :not(selector) and :has(selector).
ORACLES: Document/Element select results; :not documentation says elements do not match nested selector.
CASES: :not(p) excludes p elements; div:not(.left) retains divs lacking class left.
CASES: p:not([id=1]) excludes id=1; :has(...) returns matching ancestors (trigger expects 3).
CASES: Verify normal nested tag/class/attribute selectors and zero-match result is empty.
RISKS: Parser currently rejects :not(...) as unexpected token; nested selector token consumption is critical.
RISKS: Context omits fixture HTML and exact :has query; derive expected counts only from supplied triggers.
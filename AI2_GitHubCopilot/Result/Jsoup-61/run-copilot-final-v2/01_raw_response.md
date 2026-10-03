TARGETS: Element.hasClass(String), Element.select(".cls"), Elements.getElementsByClass(String)
ORACLES: HTML spec case-insensitivity; test assertions in ElementsTest::hasClassCaseInsensitive,
SelectorTest::testByClassCaseInsensitive
CASES: Normal exact match, uppercase/lowercase mismatch, mixed-case class, multiple classes partly
matching case-insensitively
CASES: Boundary: empty class attribute, class with only whitespace, class with leading/trailing
spaces, class in multi-class string separated by spaces
CASES: Error: null className → NPE, empty string → false, className containing spaces (as single
token?) treat as not matching unless exact
RISKS: classNames() Set retains original case; may not reflect case-insensitive containment;
select() in non-HTML (XML) contexts must remain case-sensitive
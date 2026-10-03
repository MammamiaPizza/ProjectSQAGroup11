TARGETS: Element.hasClass(String); class-token matching used by getElementsByClass/select(".class").
ORACLES: Trigger tests require case-insensitive class matching; selector expected count is 3, not 1.
CASES: Exact-case and differing-case queries against single and multiple whitespace-separated class tokens.
CASES: Verify nonmatching whole tokens remain false; avoid substring matches.
CASES: Empty/blank class attribute and absent class attribute behavior around hasClass.
RISKS: Context omits full hasClass implementation and selector/getElementsByClass delegation details.
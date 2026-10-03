TARGETS: Selector.select(query,root): parse :not(sel) and :has(sel) pseudo-classes in select()
combinator loop.
TARGETS: findElements() and combinator() methods that process tokenizer output for pseudo-classes.
ORACLES: :not(sel) excludes elements matching sel; :has(sel) selects elements with descendant
matching sel.
ORACLES: Test expectations: notAll selects all non-p; testPseudoHas expects 3 div:has(span) results.
CASES: Normal: div:not(.left), p:not([id=1]), :not(p); span:has(a), div:has(span), ul:has(li).
CASES: Boundary: :not(:not(p)) nested negation; :has(>child) child combinator; empty :not().
CASES: Error: missing ')' in ':not(p' ; invalid inner selector ':not(!!)' ; missing selector after
:has.
RISKS: Internal tokenizer details unknown; fix might affect other pseudo-classes like :contains,
:matches.
RISKS: :not() limited to simple selectors per CSS3; compound inner selectors may need recursive
parse.
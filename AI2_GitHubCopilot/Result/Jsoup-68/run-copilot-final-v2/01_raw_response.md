TARGETS: process(Token) - likely triggers deep recursion via span child-handling in HtmlTreeBuilder.
ORACLES: No explicit spec; expected behavior is no StackOverflowError (crash-free bounded
recursion).
ORACLES: Deeply nested spans should parse without exception; output DOM structure can be validated
via existing tests.
CASES: Normal: shallow spans (1-5 levels). Value: spans nested at or near MaxScopeSearchDepth limit
(100).
CASES: Boundary: exactly 100 nested spans; exactly 101; far beyond (500+ levels).
CASES: Error: empty spans, self-closing spans, spans mixed with other inline elements at depth.
RISKS: Only HtmlTreeBuilder source changes available; cannot inspect formattingElements queue logic
or inSpecificScope depth limits.
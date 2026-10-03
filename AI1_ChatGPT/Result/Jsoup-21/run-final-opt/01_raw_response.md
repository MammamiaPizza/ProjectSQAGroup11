TARGETS: QueryParser comma grouping/consumeSubQuery; CombiningEvaluator.And/Or evaluator composition.  
ORACLES: Existing SelectorTest failures: comma selector must avoid PatternSyntaxException; mixed group count is 2.  
CASES: Select comma-separated selectors containing attribute text/special regex characters; parsing must split at top-level commas.  
CASES: Mix combinators with comma groups; verify only nodes matching either complete selector group are returned.  
CASES: Normal grouped selectors and nested brackets/parentheses containing commas, if accepted by parser.  
RISKS: Parser visibility is package-private; test through public selection API used by existing SelectorTest.  
RISKS: No exact selector inputs or DOM fixture are supplied; derive expectations only from existing test context.
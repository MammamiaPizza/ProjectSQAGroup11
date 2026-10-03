TARGETS: HtmlTreeBuilder.process(Token); formatting-element and stack handling during deeply nested span parsing  
ORACLES: Trigger test expects deep spans to parse without StackOverflowError  
CASES: Parse deeply nested `<span>` elements; verify parsing completes without stack overflow  
CASES: Exercise nesting near and beyond MaxScopeSearchDepth (100) for non-recursive/bounded handling  
RISKS: Private helpers and formattingElements require behavior-level testing through parser entry points  
RISKS: No expected DOM structure or exact nesting depth is provided in the context
TARGETS: IRFactory block-comment handling during parser-to-IR transformation.  
ORACLES: Existing ParserTest suspicious block-comment warning assertions (tests 3–5).  
CASES: Suspicious block comments represented by the three failing ParserTest inputs.  
CASES: Nearby ordinary block-comment parsing must retain expected warning behavior.  
RISKS: IRFactory methods are private; exercise through parser/compiler test paths.  
RISKS: Exact warning text, source positions, and inputs are not provided in context.
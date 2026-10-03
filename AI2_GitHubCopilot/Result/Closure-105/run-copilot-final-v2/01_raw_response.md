TARGETS: FoldConstants::tryFoldAssign (ADD folding), FoldConstants::visit (Token.ADD, Token.STRING)
ORACLES: expected folded JS code string; node-tree equality after process(…)
CASES: normal "a"+"b"→"ab"; multi-literal chain; literal+variable (no fold); empty strings; order of
operands; inside assignment/function/return; non-foldable mixed types
RISKS: only simple concat covered; no error/diagnostic checks; static AST comparison only; no
runtime eval
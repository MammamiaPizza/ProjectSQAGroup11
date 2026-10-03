TARGETS: handleTry, handleBreak, computeFollowNode, connectToPossibleExceptionHandler, handleReturn
ORACLES: CFG cross edges from ControlFlowGraph.getImplicitNodes; CheckMissingReturn error count
after analysis
CASES: try-finally with return; break inside finally; deep nested finally; break with label in
finally; throw in try with finally catch
RISKS: No instrumented access to DiGraph; need existing package-private test helpers and
Node/AbstractCompiler setup
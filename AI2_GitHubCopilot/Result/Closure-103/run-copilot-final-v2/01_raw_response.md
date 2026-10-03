TARGETS: ControlFlowAnalysis: mayThrowException(Node), handleExpr(Node), computeFallThrough(Node),
handleIf/While/For to verify unreachable code
TARGETS: DisambiguateProperties: process(), handleGetProp(), getProperty(), addInvalidatingType(),
isInvalidatingType() in TypeSystem
ORACLES: CFG must create exception edges for INSTANCEOF; CheckUnreachableCode must not report
unreachable for code after throwing instanceof
ORACLES: DisambiguateProperties must correctly rename properties on types from instanceof checks
without losing supertype references
CASES: normal: x instanceof Foo with objects; edge: null/undefined LHS (throws); with
try-catch-finally; nested if-instanceof branches
CASES: boundary: instanceof in for/while condition; with break/continue; error: left side is
non-object that throws TypeError
RISKS: Cannot inspect full DisambiguateProperties bug; test must not assume internal exception
behavior of JS runtime
RISKS: May require CompilerPass and type registry setup; tests should use CompilerTestCase
infrastructure for integration
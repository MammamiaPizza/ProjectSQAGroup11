TARGETS: Normalize.process; traversal callbacks shouldTraverse, visit, enterScope, exitScope  
TARGETS: Statement normalization: labels, var splitting, named-function movement, duplicate declarations  
ORACLES: CompilerRunnerTest.testIssue115 assertion is the available expected-result source  
CASES: Compile the issue-115 input through Normalize and compare the trigger’s expected compiler result  
CASES: Exercise declarations, labels, and named functions in nested scopes  
CASES: Include duplicate declarations and constants across scope boundaries  
RISKS: Most relevant helpers are private; test via compiler/pass integration rather than direct calls  
RISKS: Bug report supplies no issue-115 source or expected output beyond the failing trigger
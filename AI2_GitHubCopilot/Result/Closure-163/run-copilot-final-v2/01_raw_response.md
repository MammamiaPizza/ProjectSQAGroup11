TARGETS: CrossModuleMethodMotion.process entry, moveMethods (module re-assignment logic)
TARGETS: AnalyzePrototypeProperties.process, ProcessProperties.visit for prototype assignments,
getNameInfoForName
ORACLES: testIssue600* assertions define expected module output after motion; correct behavior: do
not move prototype methods used across modules
ORACLES: Moved methods must leave declarations intact for cross-module prototype references;
preserved text matches test-expected source
CASES: Normal: prototype method defined & used only in own module (should move); defined in A but
called in B (must not move)
CASES: Boundary: empty prototype, only global functions, nested prototype chains, anonymous
prototype assignment, single module
CASES: Error: multiple prototype assignments of same name, prototype references in externs, several
modules with overlapping names
RISKS: No access to fixed source or exact test assertions; must infer expected behavior from bug
summary alone
RISKS: Interaction between analysis and motion phases may require integration tests; limited API
hides NameInfo internal propagation
RISKS: Bug edge cases (600b,600e) may demand tests for both property analysis accuracy and motion
decision boundary conditions
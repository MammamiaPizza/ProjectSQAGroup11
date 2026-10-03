TARGETS: NodeUtil.evaluatesToLocalValue(Node) and overload with Predicate<Node> govern local-result inference.
ORACLES: Existing NodeUtilTest::testLocalValue1 assertion is the direct local-value oracle.
ORACLES: PureFunctionIdentifier trigger expected sets require excluding listed calls/new expressions as side-effecting.
CASES: Local literals/known local names versus property access and calls with nonlocal or mutable results.
CASES: NEW String(...) and setLocation(...) must not be reported when trigger snippets expect empty sets.
CASES: Extern method/new and localized function cases must retain only expected A or externObjSEThis effects.
RISKS: Package-private APIs require tests in com.google.javascript.jscomp and AST construction conventions.
RISKS: Context omits NodeUtil implementation and exact trigger JavaScript; derive expectations only from failures.
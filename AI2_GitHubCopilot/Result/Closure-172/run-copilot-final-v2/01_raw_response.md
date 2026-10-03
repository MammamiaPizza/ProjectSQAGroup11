TARGETS: createScope for @enum/@typedef; attachLiteralTypes for enum literal check; defineName for
JSDoc type assignment
ORACLES: testIssue1024 passes; no spurious "enum initializer must be an object literal or an enum"
warning; JSType matches JSDoc
CASES: Normal: var E = {A:1} with @enum; @typedef function; Error: @enum non-literal initializer;
enum inside function scope; empty enum
RISKS: TypedScopeCreator unit tests may need full compilation; trigger test is integration; root
cause unknown
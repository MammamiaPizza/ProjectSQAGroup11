TARGETS: CodeGenerator object-literal key printing; preserve numeric lexical form and quote string getter/setter keys.
TARGETS: IRFactory object-literal transformation and JSDoc/source-position handling for object literal properties.
TARGETS: RenamePrototypes detection/renaming of prototype properties represented as object-literal keys.
ORACLES: Trigger expectations: `{[1]:1}`, `{[3E9]:1}`, and `get ["a"](){return 1}` printer output.
ORACLES: Existing CodePrinterTest, IRFactoryTest, ParserTest, RenamePropertiesTest, and FunctionNamesTest assertions.
CASES: Numeric object keys `1` and exponent `3E9`; ensure output is not quoted/canonicalized to `3000000000`.
CASES: Quoted string keys in getter/setter object literals; retain brackets and quotes.
CASES: Prototype object-literal keys and numeric definitions; verify property renaming/definition discovery behavior.
RISKS: Setter trigger text reports `get`; rely on the named existing test/oracle rather than inferring setter output.
RISKS: Context lacks full methods and truncated failures; test via public compiler/parser harnesses, not private APIs.
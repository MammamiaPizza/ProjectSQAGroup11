TARGETS: DisambiguateProperties.handleGetProp, handleObjectLit – collect property candidates; bug
misattributes inherited props.
ORACLES: After pass, Property.rootTypes map must be empty for types whose properties are all
inherited (no own prop).
CASES: Single type with only prototype-inherited property → map empty. Two types sharing inherited
prop → no entries.
CASES: Type with own + inherited props → map contains only own props. Empty object literal → no
entries.
RISKS: Test must simulate prototype chain; rely on compiler's type infrastructure (TypeInference)
for correct scoping.
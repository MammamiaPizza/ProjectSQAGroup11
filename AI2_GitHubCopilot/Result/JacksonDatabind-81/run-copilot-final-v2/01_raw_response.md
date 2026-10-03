TARGETS: JacksonAnnotationIntrospector serialization type refinement logic for annotated properties.
TARGETS: Method(s) that resolve refined serialization type from annotations (JsonSerialize,
JsonFormat, etc.).
ORACLES: @JsonSerialize(as=Integer.class) on a primitive int property must not throw
InvalidDefinitionException.
ORACLES: Refined type should be Integer; existing TypeCoercion1592Test should pass after fix.
CASES: Normal: primitive int property with no annotation → no refinement, serialization type int.
CASES: Boundary: int→Integer, long→Long, boolean→Boolean, double→Double, float→Float, short→Short,
byte→Byte, char→Character.
CASES: Boundary: other annotations that infer type (JsonFormat, JsonView) should also allow
primitive→wrapper coercion.
CASES: Error: primitive int→String (unrelated) still throws InvalidDefinitionException.
RISKS: Limited visibility into introspector internals; must ensure only primitive-to-wrapper
coercion is allowed.
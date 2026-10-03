TARGETS: _findParamName to resolve @JsonCreator param name; property-name lookup in
_addDeserializerFactoryMethods/construct creators
ORACLES: SnakeCaseStrategy: JSON "param_name0" must match creator param int paramName0 (from test;
known props [paramName0])
ORACLES: Succeed deserialization; no InvalidDefinitionException; naming strategy applies to creator
property names
CASES: single-param constructor, snake_case strategy; multi-param mixed strategies; no strategy
default; @JsonProperty override
CASES: boundary: empty name, special chars, strategy returns null/empty; error: naming strategy
ignored on creator params
RISKS: Only error trace and limited method signatures; actual fix may be in
CreatorProperty/AnnotatedParameter not shown fully
TARGETS: BasicDeserializerFactory creator-property discovery/name resolution for constructor parameters  
ORACLES: Trigger test expects snake_case `param_name0` to match creator property `paramName0`  
CASES: One-argument creator with configured snake-case naming strategy and JSON `param_name0`  
CASES: Verify deserialization succeeds instead of InvalidDefinitionException for missing creator property  
CASES: Baseline creator parameter using its declared `paramName0` name  
RISKS: Naming strategy may be applied inconsistently between creator lookup and property definition  
RISKS: Context exposes only the failing one-argument snake-case scenario; no broader contract is provided
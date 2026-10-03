TARGETS: NullifyingDeserializer.deserialize(JsonParser, DeserializationContext)
ORACLES: Trigger test testUnknownTypeIDRecovery; recovery must avoid UnrecognizedPropertyException for "location"
CASES: Unknown type-id/default-implementation input containing nested or scalar unknown property "location"
CASES: Normal nullifying path for ignorable unknown content and parser advancement across structured values
RISKS: No trigger source/body or patch details; exact returned value and token-position expectations are unavailable
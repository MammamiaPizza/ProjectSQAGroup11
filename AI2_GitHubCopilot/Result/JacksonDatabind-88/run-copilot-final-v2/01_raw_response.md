TARGETS: _typeFromId / typeFromId must validate subtype-of-baseType; throw error with «not subtype
of» substring.
ORACLES: Trigger test GenericTypeId1735Test::testNestedTypeCheck1735 expects that exact error
substring.
CASES: Type id resolving to non-subtype (e.g., HashMap for Payload1735) → error; valid subtype →
success; boundary: generics, Enum-based ids, nested types.
RISKS: Only provided resolver signatures visible; no method-body insight; regression risk in other
subtype checks if error-message change not scoped.
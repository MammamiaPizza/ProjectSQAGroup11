TARGETS: ObjectMapper polymorphic external-type-id round-trip preserving BigDecimal value/scale.
TARGETS: TokenBuffer token storage/replay used during external type-id deserialization.
ORACLES: Trigger assertion: -10000000000.0000000001 must not deserialize as -1.0E+10.
ORACLES: Compare resulting BigDecimal against the declared expected BigDecimal value.
CASES: External type-id payload containing negative BigDecimal with integer and fractional digits.
CASES: Boundary precision case matching -10000000000.0000000001 exactly.
CASES: Positive/zero BigDecimal external-type-id values if trigger fixture supports them.
RISKS: Context omits full TestExternalId fixture, JSON shape, model type, and relevant mapper configuration.
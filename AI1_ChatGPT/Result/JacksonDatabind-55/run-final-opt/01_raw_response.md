TARGETS: StdKeySerializers enum-key serializer selection and serialization behavior.
ORACLES: Trigger asserts enum map key JSON is {"aleph":"b"}, not {"A":"b"}.
CASES: Serialize a map keyed by enum constant with @JsonProperty value "aleph".
CASES: Verify key uses declared JSON property rather than enum constant name.
RISKS: Available context omits serializer lookup method bodies and enum test fixture definitions.
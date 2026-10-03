TARGETS: NumberSerializer.serialize(Number, JsonGenerator, SerializerProvider), especially BigDecimal handling.
TARGETS: NumberSerializers.addAll(Map) registration/selection of numeric serializers.
ORACLES: Trigger comparison expects {"value":"0.0000000005"}, not {"value":"5E-10"}.
CASES: Serialize BigDecimal value 5E-10 in an object property; verify plain decimal string output.
CASES: Cover ordinary integral and decimal Number values to detect serializer-selection regressions.
RISKS: Available context omits feature configuration, mapper setup, and full modified implementation.
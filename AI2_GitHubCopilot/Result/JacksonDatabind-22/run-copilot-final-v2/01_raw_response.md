TARGETS: findSerializer/buildSerializer override resolution in
BasicSerializerFactory/BeanSerializerFactory when @JsonValue present
ORACLES: Expected value "42" from custom serializer registered in test setup (bug report 848)
CASES: Normal: custom serializer wins over @JsonValue; Edge: no custom → fallback to @JsonValue;
null @JsonValue; nested beans
RISKS: Limited to bug report; full test code unavailable; only verified against
TestJsonValue::testJsonValueWithCustomOverride
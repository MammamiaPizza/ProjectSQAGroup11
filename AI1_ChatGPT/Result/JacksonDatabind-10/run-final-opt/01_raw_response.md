TARGETS: AnyGetterWriter.resolve and getAndSerialize map-backed @JsonAnyGetter serialization.
TARGETS: BeanSerializerFactory any-getter writer/property construction affecting map serializer context.
ORACLES: TestAnyGetter.testIssue705 expected JSON: {"stuff":"key/value"}.
CASES: Serialize Issue705 bean with its any-getter map entry; assert exact field name and value.
CASES: Verify map entry is emitted as "stuff":"key/value", not split into "key":"value".
RISKS: Map serializer/property contextualization may alter any-getter key/value serialization.
RISKS: Context lacks source, annotations, and additional expected empty/null/error behavior.
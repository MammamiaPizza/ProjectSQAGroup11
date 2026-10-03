TARGETS: JDK serialization roundtrip of BeanPropertyWriter (Making it implement Serializable)
ORACLES: No NotSerializableException; deserialized writer preserves getName(), getMetadata(),
getType()
ORACLES: verify _serializer, _nullSerializer survive roundtrip when non-null
CASES: default-constructed writer; writer with assigned serializer/null serializer; with non-null
_includeInViews
CASES: writer with non-null _wrapperName; with _typeSerializer set; with _suppressableValue
configured
CASES: boundary: null _declaredType (if allowed); _nonTrivialBaseType set; empty _internalSettings
RISKS: transient _format, _dynamicSerializers lost after deserialization; may affect serialization
behavior
RISKS: _member (AnnotatedMember) must be serializable; subclasses may not be serializable if member
is non-serializable
RISKS: _internalSettings (HashMap) may contain non-serializable values
TARGETS: XmlSerializerProvider._initWithRootName (root name resolution from _rootNameLookup/config)
TARGETS: XmlSerializerProvider.serializeValue(Object,JavaType) (root type path)
TARGETS: XmlSerializerProvider._startRootArray (root name application)
TARGETS: XmlSerializerProvider._rootNameFromConfig (config-driven root name)
ORACLES: @JsonRootName annotation value on serialized type
ORACLES: XmlMapper/ObjectWriter root name property (e.g., writer.withRootName("rudy"))
ORACLES: serialized XML element name after root name configuration
CASES: Normal: serialize annotated POJO; root name matches annotation
CASES: Normal: serialize unannotated POJO with writer-root-name; name matches
CASES: Boundary: after changing root name on writer, subsequent serialization uses new name (not
cached "null")
RISKS: _rootNameLookup internals not directly testable without mocking; rely on trigger test
scenario
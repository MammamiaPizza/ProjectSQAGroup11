TARGETS: XmlSerializerProvider.copy(); serializeValue() after copy; _rootNameLookup propagation.
ORACLES: After copy, serialize annotated POJO (e.g., @JacksonXmlRootElement(localName="Pojo282"))
and check XML root element equals annotation name, not class simple name.
CASES: Normal copy+serialize annotated Pojo282; copy multiple times; copy after serialization;
boundary: copy then serialize null (ROOT_NAME_FOR_NULL); error: copy with custom provider codec.
RISKS: _rootNameLookup may be shallow-copied/null after copy; annotation name lookup fails; test
relies on exact annotation definition from bug 282.
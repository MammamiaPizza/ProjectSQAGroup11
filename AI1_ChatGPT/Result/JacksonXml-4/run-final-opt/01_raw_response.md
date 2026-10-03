TARGETS: XmlSerializerProvider.serializeValue(JsonGenerator,Object) root-name initialization for dynamic values  
TARGETS: _rootNameFromConfig and _initWithRootName behavior used by root serialization  
ORACLES: RootNameTest.testDynamicRootName expects XML `<rudy/>`, not `<null/>`  
CASES: Serialize a runtime value whose configured/dynamic root name is `rudy`  
CASES: Verify root element name is retained when serializer/provider resolves typed serializer  
CASES: Null root value should use `_serializeXmlNull`/`ROOT_NAME_FOR_NULL` only if existing tests define it  
RISKS: Root-name source may be generator/config/annotations; context does not expose its setup or precedence  
RISKS: Do not infer behavior for JavaType overload, arrays, namespaces, or invalid generators
TARGETS: XmlSerializerProvider.copy() and copied mapper serialization path used by MapperCopyTest.testCopyWith.  
TARGETS: serializeValue(JsonGenerator,Object) root QName resolution for non-null POJO values.  
ORACLES: Trigger expects root `<Pojo282>` rather than annotation-derived `<AnnotatedName>` after copyWith.  
ORACLES: XML output/root element from serialization is the observable expected result.  
CASES: Copy mapper with differing root-name/annotation configuration; serialize Pojo282 with `a=3`.  
CASES: Verify copied provider honors copied configuration, not stale root-name lookup/cache state.  
CASES: Normal serialization of annotated POJO before/after copy to detect configuration isolation.  
RISKS: Context lacks full MapperCopyTest setup and exact copyWith configuration/API details.
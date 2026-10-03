TARGETS: StdDelegatingSerializer conversion and delegate serializer resolution for converted values  
TARGETS: BeanSerializerFactory/BeanSerializerBase serializer lookup after conversion type changes  
TARGETS: JavaType handler/static-typing preservation during narrowing/widening  
ORACLES: Existing TestConvertingSerializer.testIssue731 expected serialization behavior  
ORACLES: Absence of JsonMappingException for converted DummyBean with no discovered properties  
CASES: Convert a value to DummyBean and serialize through StdDelegatingSerializer  
CASES: Converted type differs from declared type; verify serializer is resolved for converted type  
CASES: Empty converted bean under default FAIL_ON_EMPTY_BEANS behavior  
RISKS: Exact JSON output and converter setup are not provided in the context  
RISKS: Do not infer behavior for unshown JavaType subclasses or custom serializer APIs
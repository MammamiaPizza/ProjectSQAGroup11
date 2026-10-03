TARGETS: BeanSerializerFactory.findBeanSerializer when origType differs from type with
typeHandler/valueHandler
TARGETS: StdDelegatingSerializer.createContextual to resolve delegate serializer from
typeHandler-converted type
TARGETS: BeanSerializerBase.resolve/convertValue propagation of type-handler–modified types
ORACLES: Trigger test testIssue731 must pass: DummyBean with delegating serializer serializes
without JsonMappingException
ORACLES: Expected JSON output matches serialization of the type returned by the converter (delegate)
CASES: Normal: converter maps DummyBean to a simple bean; verify serializer resolves and produces
correct JSON
CASES: Boundary: converter returns null; verify nullSerializer handles value
CASES: Boundary: converter wraps in a type with wildcard or generic parameter; verify resolution
succeeds
CASES: Error: converter yields type with FAIL_ON_EMPTY_BEANS disabled; assert no exception, output
{}
RISKS: Exact DummyBean converter and expected output not provided; relies on serializer resolution
consistency
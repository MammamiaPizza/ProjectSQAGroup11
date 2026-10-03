TARGETS: JdkDeserializers.find(Class<?>,String) handling of Void.class.
ORACLES: Void deserialization must not throw MismatchedInputException (from testVoidDeser failure).
CASES: Deserialize Void from JSON null, number(123), string, empty object, empty array.
ORACLES: Expected result likely null; at minimum no MismatchedInputException.
RISKS: Void expected behavior inferred from failure; test source unavailable.
TARGETS: The _classNames set missing Void entry → find() returns null → falls to default
BeanDeserializer.
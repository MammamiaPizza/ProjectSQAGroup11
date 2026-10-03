TARGETS: MultiValueMap.readObject – validate collection factory class before instantiation.
TARGETS: ReflectionFactory – used in deserialization to recreate collection; class restrictions
needed.
ORACLES: Test expects deserialization of unsafe class to throw an exception / be rejected
(AssertionFailedError).
CASES: Deserializing a MultiValueMap with a safe collection class (e.g., ArrayList) should succeed.
CASES: Deserializing a MultiValueMap with a custom, non-allowlisted class must fail (unsafe clazz
rejected).
CASES: Edge – serialized data with null factory class or missing collection-type info.
RISKS: Allowlist of safe collection types not explicit; must infer from code. Only test trigger name
available, no exact expected exception.
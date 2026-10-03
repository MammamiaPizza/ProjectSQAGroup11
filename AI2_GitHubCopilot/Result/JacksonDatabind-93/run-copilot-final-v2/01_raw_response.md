TARGETS: SubTypeValidator.validateSubType(DeserializationContext,JavaType) — null-safety in
superclass loop
ORACLES: IllegalTypesCheckTest.testJDKTypes1872 must not throw NullPointerException
CASES: raw=null from type.getRawClass(); raw is interface (getSuperclass() null); raw=Object.class
(loop skip); deep hierarchy
CASES: raw=array class; raw=final class with no parent other than Object; raw=primitive wrapper
RISKS: loop body unknown; NPE could be inside loop body not in cls assignment; test may need
specific JDK types
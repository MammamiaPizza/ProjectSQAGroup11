TARGETS: BeanDeserializerFactory#isPotentialBeanType, DEFAULT_NO_DESER_CLASS_NAMES init, factory
createBeanDeserializer using _cfgIllegalClassNames
ORACLES: Proxy type → IllegalArgumentException("Can not deserialize Proxy class"); illegal class
name → JsonMappingException; safe beans deserialize fine
CASES: deserialize valid POJO; deserialize Proxy subclass → IllegalArgumentException; deserialize
each type in DEFAULT_NO_DESER_CLASS_NAMES → MappingException
CASES: factory after withConfig() still rejects Proxy & illegal names; test class name match with
safe concrete class (boundary)
CASES: test deserialization of Throwable subclass (see snippet conditional) → likely
MappingException; test non-throwable bean succeeds
RISKS: exact DEFAULT_NO_DESER_CLASS_NAMES content unknown; reflect on static set to identify entries
for test cases
RISKS: factory creation path not fully visible; may require ObjectMapper integration; ensure test
covers issue 1737 JDK types only
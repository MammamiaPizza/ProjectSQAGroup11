TARGETS: DeserializerCache caching for Map types with custom key deserializers  
ORACLES: MapDeserializerCachingTest::testCachedSerialize assertion on custom key deserializer use  
CASES: Deserialize {"data":{"1st":"onedata","2nd":"twodata"}} with configured custom key deserializer  
CASES: Repeat deserialization to exercise cached deserializer reuse  
CASES: Check cachedDeserializersCount and flushCachedDeserializers cache-reset behavior  
RISKS: Available context omits custom key deserializer configuration and expected transformed key values  
RISKS: Do not infer behavior for unknown value/key deserializer handlers beyond their reported failures
TARGETS: ReturnsDeepStubs answer(), deep-stub mock creation/settings, and nested answer serialization behavior.  
ORACLES: Trigger expects deep-stub-created mock to serialize and deserialize without NotSerializableException.  
CASES: Serializable deep-stub mock round-trip, then verify deserialized mock remains usable for chained stubbing.  
CASES: Normal deep-stub invocation creating a nested mock and returning the same recorded nested stub.  
RISKS: Anonymous ReturnsDeepStubs$2 answer is reported non-serializable during object serialization.  
RISKS: Context lacks test source and exact post-deserialization assertions; derive expectations only from trigger/exception.
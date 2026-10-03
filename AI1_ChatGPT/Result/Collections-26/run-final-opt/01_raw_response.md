TARGETS: MultiKey.equals(Object), hashCode(), readResolve(), constructors, size(), getKey(int)  
ORACLES: Trigger expects equality after serialization of a derived MultiKey; failure reports expected 3, got 0  
CASES: Serialize/deserialize a derived MultiKey, then verify equality-related behavior against its original  
CASES: Use a derived instance with three keys; check size() and indexed keys after round trip  
CASES: Compare round-tripped derived instance with original and with a base MultiKey of same keys  
RISKS: readResolve may replace a derived instance with MultiKey, changing subclass state or equality behavior  
RISKS: Context provides no derived-class definition or explicit expected semantics beyond the trigger failure
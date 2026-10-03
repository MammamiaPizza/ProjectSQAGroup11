TARGETS: POJOPropertiesCollector._doCollect() property merging for _forSerialization flag;
POJOPropertyBuilder.anyVisible()/isExplicitlyNamed() for read/write-only
TARGETS: _addFields/_addMethods/_addInjectables collection logic; _collectIgnorals handling of
access=READ_ONLY/WRITE_ONLY
ORACLES: Expect getter with @JsonProperty(access=READ_ONLY) output on serialize, not on deserialize;
setter with WRITE_ONLY accept on deserialize only
ORACLES: No UnrecognizedPropertyException for fields legitimately read-only during deserialization
or write-only during serialization
CASES: Pojo with @JsonProperty(access=READ_ONLY) on getter, WRITE_ONLY on setter; serialize gives
read-only field, deserialize gives write-only field
CASES: Boundary: same logical property via getter+setter with opposite access; should not lose
property on both sides
CASES: Error: deserialize JSON with extra unknown field not matching any property; expect
UnrecognizedPropertyException only for truly unknown
RISKS: Cannot see diff; must deduce fix from bug label #935 and test failures; risk of
misinterpreting visibility during merging of fields/methods/ctors
RISKS: Need to ensure test uses default ObjectMapper settings; annotation-based access control might
conflict with @JsonIgnore if both present
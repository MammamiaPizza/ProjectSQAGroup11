TARGETS: BeanDeserializer._deserializeUsingPropertyBased handling of ObjectId+creator+START_ARRAY
token.
TARGETS: BeanDeserializer.deserialize, deserializeFromObject, withObjectIdReader for ObjectId
resolution path.
ORACLES: Existing testObjectIds1261 expects successful deserialization of Child with
@JsonCreator+ObjectId from array.
ORACLES: Jackson spec: @JsonIdentityInfo with @JsonCreator should support array-based creators for
ObjectId references.
CASES: START_ARRAY for ObjectId with property-based creator (trigger); START_OBJECT normal case;
missing @id property.
CASES: Array token with no ObjectId reference; duplicate ObjectId; null id; combined with unwrapping
or external type id.
RISKS: Cannot see bug fix patch; root cause may span multiple methods beyond
_deserializeUsingPropertyBased.
RISKS: Limited to supplied API signatures; dynamic proxy/managed-ref resolution behavior internal to
Jackson not visible.
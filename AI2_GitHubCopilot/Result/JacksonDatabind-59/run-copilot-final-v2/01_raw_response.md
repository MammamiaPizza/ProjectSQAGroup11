TARGETS: forcedNarrowBy/_narrow on MapLikeType,CollectionLikeType;
TypeFactory.constructMapType,constructSpecializedType
TARGETS: MapLikeType.getKeyType(),getContentType() after narrowing; isMapLikeType() flag still true
ORACLES: Narrowing a Map-type with key=CompoundKey must preserve the key type so KeyDeserializer is
found (no JsonMappingException)
ORACLES: Type handlers and value handlers set on the original type should be retained after
forcedNarrowBy
CASES: Normal: narrow Map(CompoundKey,String) to Map subclass; narrow Collection-like with element
type; boundary: narrow key as Object.class(null)
CASES: Error: narrow Map to List.class (expect IllegalArgumentException or fallback); narrow with
missing generic info
RISKS: Internal _narrow() @Deprecated; private _class/_handlers may affect identity;
key‑deserializer lookup is outside the visible scope of these classes
RISKS: Test can only verify structural type metadata (getKeyType) not actual deserialization;
TypeRefinementForMapTest source not provided here
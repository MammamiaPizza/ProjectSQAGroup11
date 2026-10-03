TARGETS: TypeFactory map-type refinement; MapLikeType key/content type preservation during refinement.
TARGETS: JavaType handler/content-type transformations used by refined map types.
ORACLES: Trigger test must not fail with missing Map key deserializer for CompoundKey.
CASES: Refine a Map with CompoundKey key type and deserialize JSON map entries.
CASES: Verify refined map retains key type; include value/content type refinement if exposed.
RISKS: Available signatures are truncated; exact refinement entry points and assertions are not fully shown.
TARGETS: DeserializationConfig.with/without(MapperFeature...) for visibility features;
SerializationConfig same; config copy constructors preserving mapper feature flags.
ORACLES: TestFeatures.testVisibilityFeatures expects 1 property with AUTO_DETECT_SETTERS disabled;
property count from introspected bean matches expected visibility exclusions.
CASES: Default all-auto-detect yields multiple properties; disable AUTO_DETECT_SETTERS → only field
property remains; disable AUTO_DETECT_FIELDS → only setter property.
CASES: Chained with/without toggling feature multiple times; combining with other with-methods
(e.g., withRootName) must not lose visibility settings.
CASES: Full exclusion (all auto-detection off) yields zero properties; test boundary where bean has
only setter, only field, or both.
RISKS: Limited to visible APIs; introspection outcome depends on MapperFeature enum not shown; risk
that fix crossed SerializationConfig.copy propagation.
RISKS: Cannot see TCls definition; test relies on specific visibility of setter and field; behavior
may differ for getter-only or constructor properties.
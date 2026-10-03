TARGETS: BeanPropertyMap.remove(SettableBeanProperty), BeanPropertyMap.withProperty,
BeanPropertyMap.withoutProperties, BeanPropertyMap.replace
ORACLES: After remove, size decrements; no exception; iterator/getPropertiesInInsertionOrder reflect
removal
CASES: Remove existing prop with case-insensitive true; remove when key case differs; remove
non-existing; empty map remove; after renameAll; after assignIndexes
RISKS: Case-insensitive _find2 inconsistency; remove may corrupt alias map; replace may impact
internal lookup table
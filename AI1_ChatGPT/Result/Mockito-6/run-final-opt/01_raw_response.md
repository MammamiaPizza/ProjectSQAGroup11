TARGETS: Matchers anyString(), anyCollection(), anyList(), anySet(), anyMap() null matching behavior.
TARGETS: Primitive-wrapper anyX matchers must reject null; default invocation return remains null.
ORACLES: Trigger assertions: stubbed calls with null must yield null, not "matched" or primitive default 0.
CASES: Stub methods with anyString/anyCollection/anyList/anySet/anyMap; invoke with null and valid non-null values.
CASES: Cover all primitive-wrapper anyX matchers with null versus corresponding non-null wrapper values.
RISKS: Matcher registration/default return values can mask matching defects for primitive-returning methods.
RISKS: Context lacks implementation and full existing test fixtures; rely only on listed APIs and trigger behavior.
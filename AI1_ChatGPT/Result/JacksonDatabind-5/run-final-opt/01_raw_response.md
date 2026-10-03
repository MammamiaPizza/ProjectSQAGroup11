TARGETS: AnnotatedClass member-method resolution and mix-in merging for disappearing mix-ins (515).
ORACLES: Trigger expects PersonImpl serialization to discover properties, not FAIL_ON_EMPTY_BEANS.
CASES: Reproduce TestMixinMerging.testDisappearingMixins515 with its mapper/mix-in configuration.
CASES: Assert serialization succeeds and output contains the property exposed through the resolved member method.
CASES: Verify resolved memberMethods/findMethod retains the applicable inherited or mix-in method.
RISKS: Exact JSON/property name and fixture setup are unavailable outside the named trigger context.
RISKS: Do not infer behavior for unrelated annotations, fields, creators, or disabled empty-bean failures.
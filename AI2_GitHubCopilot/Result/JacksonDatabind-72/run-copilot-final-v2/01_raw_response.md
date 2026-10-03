TARGETS: InnerClassProperty.assignIndex, getPropertyIndex, deserializeAndSet, withName, annotated
constructor handling
ORACLES: assignIndex(i) → getPropertyIndex()==i; no IllegalStateException during deserialization of
inner class with property a after fix
CASES: assign 0 and verify; assign multiple indices for different properties; boundary: assign -1
(should be rejected?); no assign leads to "no creator index" error
RISKS: Cannot directly construct InnerClassProperty; need ObjectMapper for inner class scenario; fix
may span BeanDeserializer; test relies on exception message stability
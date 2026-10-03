TARGETS: POJOPropertiesCollector property collection/ignorals; POJOPropertyBuilder accessor visibility/merge behavior.
ORACLES: Trigger tests require deserialization not to reject read-only JSON names as unrecognized.
CASES: ReadXWriteY: input "x" is accepted/ignored while writable "y" remains deserializable.
CASES: Pojo935: input "fullName" is accepted/ignored; "firstName"/"lastName" remain recognized.
CASES: Verify normal writable properties still bind when read-only properties coexist.
RISKS: Context omits method bodies and full annotations; assert only trigger-derived observable behavior.
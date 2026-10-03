TARGETS: StdKeyDeserializer.forType handling CharSequence; deserializeKey returning key as String.
ORACLES: forType must not return null for CharSequence.class; testcharSequenceKeyMap passes no
exception.
CASES: Normal: CharSequence-key map deserialization returns Map<String,V>.
CASES: Boundary: isAssignableFrom subclasses; Error: unsupported types still throw.
RISKS: Fix unknown (explicit check vs isAssignableFrom); key expected as String not CharSequence
object.
RISKS: Only CharSequence key issue; no info on other abstract types.
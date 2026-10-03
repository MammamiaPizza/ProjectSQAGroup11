TARGETS: Attribute.setKey(String), setValue(String)
ORACLES: setKey/setValue on Attribute with parent==null must not throw NPE (Bug#1107)
CASES: orphan setKey("new"); orphan setValue("val"); orphan setKey then setValue; same key/value
RISKS: Other Attribute methods using parent may NPE (html, isBooleanAttribute); verify orphan safety
ORACLES: setKey/setValue should update local fields; parent (if present) should reflect changes
CASES: non-orphan: setKey updates parent internal map; setValue updates parent without NPE
RISKS: Attribute clone() and equals() correctness with parent==null may be untested; cover boundary
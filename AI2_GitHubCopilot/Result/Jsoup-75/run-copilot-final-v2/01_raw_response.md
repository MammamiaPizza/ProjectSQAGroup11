TARGETS:  Attributes.html() for boolean-attribute rendering; put(String,boolean) storage logic.
ORACLES:  Boolean attrs (noshade,async) must appear without ="" in html(); get() returns "".
CASES:   Put noshade=true → html() contains noshade; put checked=false → key absent; mixed with
src="foo"; data- attr preserved with value.
RISKS:   Only unit-testing Attributes; cannot verify parser integration or BooleanAttribute output
path.
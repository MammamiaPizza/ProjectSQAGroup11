TARGETS: Attribute.getValue for boolean attrs, setValue, isBooleanAttribute,
shouldCollapseAttribute, html(), clone, hashCode/equals
ORACLES: HTML spec (empty string value for boolean attrs); test name
"booleanAttributesAreEmptyStringValues" implies expectation
ORACLES: Attribute constructors (key+value, key+val+parent); setValue must not be null per doc;
getKey returns key
CASES: Boolean attr (no value provided) → getValue()=="" ; same with explicit empty string;
non-empty string preserved
CASES: Boolean attr with value equal to key (e.g., hidden=hidden); non-boolean attr with null?
(should throw or reject)
CASES: html() output for boolean attr with OutputSettings.syntax=html (collapse when true); other
settings preserve explicit value
CASES: shouldCollapseAttribute with different key/val combos and OutputSettings; isBooleanAttribute
with various key strings
CASES: equals/hashCode ignore parent; clone (no parent); toString returns key="val" (empty string vs
null)
RISKS: No test body; expected null vs empty string might depend on version; missing spec on
setValue(null) behavior
RISKS: Unknown interaction with Attributes parent when key/value changed; may need to test parent
update after setKey/setValue
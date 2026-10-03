TARGETS: getSlot, getPropertyType, hasProperty, getPropertiesCount, getOwnPropertyNames,
matchRecordTypeConstraint
O​RACLES: after inferred‑property addition, hasProperty==true, getPropertyType returns expected type
isPropertyTypeInferred true
O​RACLES: bug‑fix expectations from testRecordInference/testIssue785: record types must not be empty
{[]}
C​ASES: add 2 inferred props to a PrototypeObjectType; verify count==2, names in getOwnPropertyNames
each retrievable
C​ASES: bound‑empty: zero props → getPropertiesCount==0, getOwnPropertyNames empty, hasProperty fals
for any name
C​ASES: after matchRecordTypeConstraint on object with existing props, validate all props still
present and types unchanged
C​ASES: add prop, call removeProperty, verify hasProperty false, count decremented; re‑add and verif
again
C​ASES: add many properties, then verify isPropertyTypeInferred true for each and false for
nonexistent names
R​ISKS: limited to public API; bug may only surface through full type‑inference pipeline; need to
create RecordType instances
R​ISKS: cannot inspect internal state; must rely on getter methods to detect property loss after
constrained operations
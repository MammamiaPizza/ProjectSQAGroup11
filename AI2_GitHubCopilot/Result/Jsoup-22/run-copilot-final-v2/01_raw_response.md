TARGETS: siblingElements() on Node/Element — returns sibling element list excluding self; must not
throw NPE when parent is null.
ORACLES: elementIsNotASiblingOfItself expects count=2 for element with 2 siblings;
orphanNodeReturnsNullForSiblingElements expects no NPE.
CASES: Normal: 3-element parent → siblingElements().size()==2 for each. Boundary: only child (0
siblings). Error: parent==null should return empty/null.
RISKS: siblingElements() signature absent from truncated API; cannot confirm exact return type or
related sibling-nav methods.
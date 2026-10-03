TARGETS: PrototypeObjectType.toString() handling of recursive references and long-property-list
truncation.
ORACLES: Test failures expect recursive property as "[?]", overflow properties as "[a5: number, a6:
number]}" not empty "[...]".
CASES: Recursive PrototypeObjectType with self-referencing property; toString must show "[?]" not
"{...}".
CASES: Long property list (≥10 props) must abbreviate with non-empty bracket listing remaining
properties (e.g., "[a7: number, a8: number, ...]").
CASES: Boundary: 9 properties => full listing; 10 properties => trigger truncation, not empty
brackets.
CASES: Mixed: PrototypeObjectType with 10+ props but one is another non-recursive ObjectType; inner
type shown fully, outer abbreviated.
RISKS: Must create recursive reference correctly; tests rely on internal type-registry state; exact
truncation format may depend on limit constants not given.
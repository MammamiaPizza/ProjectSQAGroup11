TARGETS: FormElement.addElement, elements, formData, and removal of registered child elements.
ORACLES: Trigger expects elements().size() to decrease from 3 to 2 after child removal.
CASES: Add multiple elements, remove one child, verify elements() excludes it and reports size 2.
CASES: Remove a registered form control, then verify formData() excludes the removed control.
CASES: Check remaining registered controls persist after removing a different control.
RISKS: Context does not specify expected behavior for removing unregistered elements or repeated removal.
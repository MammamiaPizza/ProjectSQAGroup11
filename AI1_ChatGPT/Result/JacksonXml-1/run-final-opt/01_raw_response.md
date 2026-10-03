TARGETS: FromXmlParser.nextToken() token sequencing for nested unwrapped XML lists, including empty elements.
TARGETS: addVirtualWrapping(Set<String>) and isExpectedStartArrayToken() array-wrapper behavior.
ORACLES: Trigger tests expect nested empty-list handling; two failures assert expected size 1, actual 0.
CASES: Nested unwrapped lists containing an empty nested list followed by/populated with sibling content.
CASES: Empty nested-list variants from NestedUnwrappedListsTest and NestedUnwrappedLists180Test.
RISKS: Verify START_ARRAY/END_ARRAY and field-name token boundaries; avoid losing a nested list entry.
RISKS: Exact XML fixtures and full intended token sequence are not provided in this context.
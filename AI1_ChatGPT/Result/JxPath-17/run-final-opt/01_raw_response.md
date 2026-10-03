TARGETS: DOMAttributeIterator and JDOMAttributeIterator attribute-axis iteration for QName @* selection.
ORACLES: Existing DOMModelTest/JDOMModelTest testAxisAttribute expected values [10%, 20%].
CASES: Iterate vendor/product/price:amount/@* and verify both attributes, including first-position traversal.
CASES: Verify iterator position and node pointer across sequential setPosition calls for matching attributes.
RISKS: Bug likely skips an attribute during wildcard/name matching; test both DOM and JDOM implementations.
RISKS: Context exposes no source/body semantics or additional expected behavior beyond trigger assertion.
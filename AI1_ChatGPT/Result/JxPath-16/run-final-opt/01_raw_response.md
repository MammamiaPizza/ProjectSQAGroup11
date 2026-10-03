TARGETS: DOMNodePointer and JDOMNodePointer node ordering/path behavior used by following and preceding axes.
ORACLES: Existing DOMModelTest/JDOMModelTest expected pointer paths for XPath axis evaluations.
CASES: //location[2]/following::node()[2] must resolve to vendor/product, not product/name.
CASES: //location[2]/preceding::node()[3] must resolve to employeeCount text, not address.
CASES: Run both XPath cases against DOM and JDOM models.
RISKS: Context exposes pointer APIs but not fixture XML or axis-iterator implementation details.
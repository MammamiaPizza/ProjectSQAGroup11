TARGETS: UnionContext.getDocumentOrder() must compare merged node sets by document order.
TARGETS: UnionContext.setPosition(pos) must iterate union results in document order.
ORACLES: DOMModelTest/JDOMModelTest testUnion expect /vendor[1]/contact[1] "John" before contact[4]
"Jack Black".
ORACLES: Iteration order should follow NodePointer document position, not input contexts[] array
order.
CASES: Two non-empty contexts supplied out of order; reversed inputs still yield contact[1] first.
CASES: Overlapping contexts and shared/duplicate node identity.
CASES: Single context, empty context, and position=0/-1/out-of-range boundary behavior.
RISKS: JXPATH-100 bug keeps input context order; assert sorted order, not just set membership.
RISKS: Only UnionContext signatures available; model fixtures and expected values limited to trigger
messages.
RISKS: Avoid inventing APIs or node-order semantics beyond documented axpath expected results.
TARGETS: Element.elementSiblingIndex(), siblingElements(), and sibling navigation/index lookup with equal-content siblings.  
ORACLES: Existing trigger assertions; duplicate-content siblings must remain distinct by tree position.  
CASES: Two sibling elements with identical tag/content: verify each elementSiblingIndex matches its child order.  
CASES: Duplicate-content siblings: verify siblingElements excludes only the receiver, not an equal sibling.  
CASES: Verify next/previous/first/last element sibling behavior around equal-content adjacent siblings.  
CASES: Boundary siblings at first/last positions and a sole element sibling.  
RISKS: Element.equals may treat structurally equal nodes as equal, unsuitable for sibling identity/index lookup.  
RISKS: Context omits full ElementTest assertions and implementation details; derive expectations only from triggers.
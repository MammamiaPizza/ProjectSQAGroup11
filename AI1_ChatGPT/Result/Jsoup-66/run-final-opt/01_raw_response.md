TARGETS: Element.clone(), nextElementSibling(), siblingElements(), parent/child-node relationships  
ORACLES: Existing trigger test and Element sibling-navigation results; avoid alternate-version behavior  
CASES: Clone an element with following element sibling; call nextElementSibling() on clone  
CASES: Verify clone sibling lookup does not throw NullPointerException and returns expected sibling  
CASES: Clone with no next element sibling; clone among text/non-element nodes  
RISKS: Clone may retain parent but lack initialized child-node/list state used by sibling traversal  
RISKS: Context omits full Node.clone() implementation and exact expected assertion from trigger
TARGETS: Element.siblingElements(), Node.siblingNodes(), and orphan sibling lookup behavior.  
ORACLES: Trigger expectations: sibling collections exclude receiver; two actual siblings yield size 2.  
ORACLES: Orphan node siblingElements() returns null, not NullPointerException.  
CASES: Element with parent and three children: receiver's siblingElements() excludes itself.  
CASES: Non-Element Node with parent and three child nodes: siblingNodes() excludes itself.  
CASES: Orphan node: siblingElements() null behavior.  
RISKS: Full sibling method signatures/ordering and Node subtype construction are truncated.
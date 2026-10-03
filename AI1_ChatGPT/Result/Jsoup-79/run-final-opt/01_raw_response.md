TARGETS: LeafNode.childNodeSize() and ensureChildNodes() child-access behavior for TextNode/leaf nodes.  
ORACLES: Trigger expects lead/leaf nodes to report no children, not throw UnsupportedOperationException.  
CASES: TextNode childNodeSize() returns 0; childNodes-related access yields an empty child list.  
CASES: Repeated child access on a leaf remains empty and does not create observable children.  
RISKS: LeafNode is abstract; test through available concrete leaf nodes such as TextNode.  
RISKS: No separate fixed-version behavior is available beyond the trigger and bug summary.
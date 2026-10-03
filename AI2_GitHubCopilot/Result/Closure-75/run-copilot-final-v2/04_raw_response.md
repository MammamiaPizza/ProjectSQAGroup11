@Test
    public void testArrayToStringWithStringElements() {
        com.google.javascript.rhino.Node array = new
com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.ARRAYLIT);
        array.addChildToBack(com.google.javascript.rhino.Node.newString("a"));
        array.addChildToBack(com.google.javascript.rhino.Node.newString("b"));
        assertEquals("a,b", NodeUtil.arrayToString(array));
    }

 @Test
 public void testArrayToStringNullOnNonStringElement() {
     com.google.javascript.rhino.Node array = new
com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.ARRAYLIT);
     array.addChildToBack(com.google.javascript.rhino.Node.newNumber(3.0));
     assertNull(NodeUtil.arrayToString(array));
 }

 @Test
 public void testCanBeSideEffectedNameNotConstant() {
     com.google.javascript.rhino.Node nameNode =
com.google.javascript.rhino.Node.newString(com.google.javascript.rhino.Token.NAME, "x");
     java.util.Set<String> emptySet = java.util.Collections.emptySet();
     assertTrue(NodeUtil.canBeSideEffected(nameNode, emptySet));
 }

 @Test
 public void testCanBeSideEffectedBlockWithSideEffectableChild() {
     com.google.javascript.rhino.Node block = new
com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.BLOCK);
     com.google.javascript.rhino.Node nameNode =
com.google.javascript.rhino.Node.newString(com.google.javascript.rhino.Token.NAME, "y");
     block.addChildToBack(nameNode);
     java.util.Set<String> emptySet = java.util.Collections.emptySet();
     assertTrue(NodeUtil.canBeSideEffected(block, emptySet));
 }
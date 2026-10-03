public void testRelationalOperationsWithNodeSetOnRight() {
    org.apache.commons.jxpath.JXPathContext context =
        org.apache.commons.jxpath.JXPathContext.newContext(null);
    context.getVariables().declareVariable("array", new Integer[] { new Integer(1) });

    assertEquals(Boolean.TRUE, context.getValue("2 > $array"));
    assertEquals(Boolean.TRUE, context.getValue("2 >= $array"));
    assertEquals(Boolean.TRUE, context.getValue("0 < $array"));
    assertEquals(Boolean.TRUE, context.getValue("0 <= $array"));

    assertEquals(Boolean.FALSE, context.getValue("0 > $array"));
    assertEquals(Boolean.FALSE, context.getValue("0 >= $array"));
    assertEquals(Boolean.FALSE, context.getValue("2 < $array"));
    assertEquals(Boolean.FALSE, context.getValue("2 <= $array"));
}

public void testRelationalOperationsWithTwoNodeSets() {
    org.apache.commons.jxpath.JXPathContext context =
        org.apache.commons.jxpath.JXPathContext.newContext(null);
    context.getVariables().declareVariable("left", new Integer[] { new Integer(1) });
    context.getVariables().declareVariable("right", new Integer[] { new Integer(2) });

    assertEquals(Boolean.TRUE, context.getValue("$left < $right"));
    assertEquals(Boolean.TRUE, context.getValue("$left <= $right"));
    assertEquals(Boolean.FALSE, context.getValue("$left > $right"));
    assertEquals(Boolean.FALSE, context.getValue("$left >= $right"));
}
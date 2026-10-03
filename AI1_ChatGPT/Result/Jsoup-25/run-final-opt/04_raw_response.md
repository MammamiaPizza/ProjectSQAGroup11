@Test
public void emptyKnownTagsAreSelfClosingAndNotDataTags() {
    Tag br = Tag.valueOf("br");

    assertTrue(br.isKnownTag());
    assertTrue(br.isEmpty());
    assertTrue(br.isSelfClosing());
    assertTrue(br.isInline());
    assertFalse(br.isData());
}

@Test
public void scriptIsRecognizedAsANonEmptyDataTag() {
    Tag script = Tag.valueOf("script");

    assertTrue(script.isKnownTag());
    assertTrue(script.isData());
    assertFalse(script.isEmpty());
    assertFalse(script.isSelfClosing());
}

@Test
public void blockTagsCanContainBlocksAndFormatAsBlocks() {
    Tag div = Tag.valueOf("div");

    assertTrue(div.canContainBlock());
    assertTrue(div.formatAsBlock());
    assertFalse(div.isInline());
}

@Test
public void equivalentUnknownTagsHaveEqualHashCodesAndDoNotEqualOtherObjects() {
    Tag first = Tag.valueOf("unregistered-equality-tag");
    Tag second = Tag.valueOf("unregistered-equality-tag");

    assertTrue(first.equals(first));
    assertTrue(first.equals(second));
    assertEquals(first.hashCode(), second.hashCode());
    assertFalse(first.equals(null));
    assertFalse(first.equals("unregistered-equality-tag"));
}
@Test
public void textNodeCoreValueCanBeReadAndUpdatedThroughItsNodeNameAttribute() {
    TextNode textNode = new TextNode("original", "");

    assertEquals("original", textNode.attr("#text"));
    assertTrue(textNode == textNode.attr("#text", "updated"));
    assertEquals("updated", textNode.attr("#text"));
    assertTrue(textNode.hasAttr("#text"));
}

@Test
public void textNodeAttributesPreserveCoreValueAndCanBeRemoved() {
    TextNode textNode = new TextNode("content", "");

    textNode.attributes();
    assertEquals("content", textNode.attr("#text"));

    textNode.attr("class", "lead");
    assertEquals("content", textNode.attr("#text"));
    assertEquals("lead", textNode.attr("class"));
    assertTrue(textNode.hasAttr("class"));

    textNode.removeAttr("class");
    assertTrue(!textNode.hasAttr("class"));
    assertEquals("content", textNode.attr("#text"));
}

@Test
public void detachedTextNodeHasNoBaseUri() {
    TextNode textNode = new TextNode("content", "http://example.com/base/");

    assertEquals("", textNode.baseUri());
}
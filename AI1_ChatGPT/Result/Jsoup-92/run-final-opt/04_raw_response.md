@org.junit.Test
public void attributesAddAllReplacesMatchingKeysAndCopiesNewKeys() {
    org.jsoup.nodes.Attributes attributes = new org.jsoup.nodes.Attributes();
    attributes.put("one", "original");

    org.jsoup.nodes.Attributes incoming = new org.jsoup.nodes.Attributes();
    incoming.put("one", "replacement");
    incoming.put("Two", "second");

    attributes.addAll(incoming);

    org.junit.Assert.assertEquals(2, attributes.size());
    org.junit.Assert.assertEquals("replacement", attributes.get("one"));
    org.junit.Assert.assertEquals("second", attributes.get("Two"));
}

@org.junit.Test
public void clonedAttributesCanBeChangedWithoutChangingOriginal() {
    org.jsoup.nodes.Attributes original = new org.jsoup.nodes.Attributes();
    original.put("first", "one");
    original.put("second", "two");

    org.jsoup.nodes.Attributes clone = original.clone();
    clone.put("first", "changed");
    clone.put("third", "three");

    org.junit.Assert.assertEquals(2, original.size());
    org.junit.Assert.assertEquals("one", original.get("first"));
    org.junit.Assert.assertEquals("", original.get("third"));
    org.junit.Assert.assertEquals(3, clone.size());
    org.junit.Assert.assertEquals("changed", clone.get("first"));
    org.junit.Assert.assertEquals("three", clone.get("third"));
}

@org.junit.Test
public void datasetViewExposesAndUpdatesOnlyDataAttributes() {
    org.jsoup.nodes.Attributes attributes = new org.jsoup.nodes.Attributes();
    attributes.put("data-one", "1");
    attributes.put("title", "plain");

    java.util.Map<String, String> dataset = attributes.dataset();

    org.junit.Assert.assertEquals(1, dataset.size());
    org.junit.Assert.assertEquals("1", dataset.get("one"));
    org.junit.Assert.assertNull(dataset.put("two", "2"));
    org.junit.Assert.assertEquals("2", attributes.get("data-two"));
    org.junit.Assert.assertEquals("1", dataset.put("one", "updated"));
    org.junit.Assert.assertEquals("updated", attributes.get("data-one"));
}
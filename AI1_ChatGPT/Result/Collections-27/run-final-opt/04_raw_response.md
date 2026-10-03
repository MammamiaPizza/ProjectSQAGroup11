@Test
@SuppressWarnings({ "rawtypes", "unchecked" })
public void testUnsafeCollectionClassIsRejectedDuringDeserialization() throws Exception {
    final MultiValueMap unsafe = MultiValueMap.multiValueMap(
            new java.util.HashMap(), (java.lang.Class) java.lang.String.class);

    final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    final java.io.ObjectOutputStream output = new java.io.ObjectOutputStream(bytes);
    output.writeObject(unsafe);
    output.close();

    final java.io.ObjectInputStream input = new java.io.ObjectInputStream(
            new java.io.ByteArrayInputStream(bytes.toByteArray()));
    try {
        input.readObject();
        org.junit.Assert.fail("unsafe collection class accepted when de-serializing MultiValueMap");
    } catch (final Exception expected) {
        // expected
    } finally {
        input.close();
    }
}

@Test
public void testClearRemovesAllMappingsAndValues() {
    final MultiValueMap<String, String> map = new MultiValueMap<String, String>();
    map.put("letters", "a");
    map.put("numbers", "1");

    assertTrue(map.containsValue("a"));
    map.clear();

    assertEquals(0, map.totalSize());
    assertFalse(map.containsValue("a"));
    assertFalse(map.containsValue("letters", "a"));
}

@Test
public void testPutAllCollectionHandlesEmptyAndAddsValues() {
    final MultiValueMap<String, String> map = new MultiValueMap<String, String>();

    assertFalse(map.putAll("letters", (java.util.Collection<String>) null));
    assertFalse(map.putAll("letters", java.util.Collections.<String>emptyList()));
    assertTrue(map.putAll("letters", java.util.Arrays.asList("a", "b")));

    assertEquals(2, map.size("letters"));
    assertTrue(map.containsValue("letters", "a"));
    assertTrue(map.containsValue("letters", "b"));
}

@Test
public void testIteratorReturnsEachKeyValueMapping() {
    final MultiValueMap<String, String> map = MultiValueMap.multiValueMap(
            new java.util.LinkedHashMap<String, java.util.Collection<String>>());
    map.put("first", "a");
    map.put("first", "b");
    map.put("second", "c");

    final java.util.Iterator<java.util.Map.Entry<String, String>> iterator = map.iterator();

    java.util.Map.Entry<String, String> entry = iterator.next();
    assertEquals("first", entry.getKey());
    assertEquals("a", entry.getValue());

    entry = iterator.next();
    assertEquals("first", entry.getKey());
    assertEquals("b", entry.getValue());

    entry = iterator.next();
    assertEquals("second", entry.getKey());
    assertEquals("c", entry.getValue());
    assertFalse(iterator.hasNext());
}
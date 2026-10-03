@org.junit.Test
public void constructorCopiesEntriesWhenSourceRequiresDelegateMap() {
    java.util.Map source = new java.util.HashMap();
    source.put("one", Integer.valueOf(1));
    source.put("two", Integer.valueOf(2));
    source.put("three", Integer.valueOf(3));
    source.put("four", Integer.valueOf(4));

    org.apache.commons.collections.map.Flat3Map map =
            new org.apache.commons.collections.map.Flat3Map(source);

    org.junit.Assert.assertEquals(Integer.valueOf(4), Integer.valueOf(map.size()));
    org.junit.Assert.assertEquals(Integer.valueOf(1), map.get("one"));
    org.junit.Assert.assertEquals(Integer.valueOf(4), map.get("four"));
    org.junit.Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(map.containsKey("three")));

    source.put("one", Integer.valueOf(99));
    org.junit.Assert.assertEquals(Integer.valueOf(1), map.get("one"));
}

@org.junit.Test
public void clearRemovesEntriesInFlatAndDelegateModes() {
    org.apache.commons.collections.map.Flat3Map map =
            new org.apache.commons.collections.map.Flat3Map();
    map.put(null, "null");
    map.put("one", Integer.valueOf(1));
    map.put("two", Integer.valueOf(2));

    org.junit.Assert.assertEquals(Boolean.TRUE, Boolean.valueOf(map.containsKey(null)));
    map.clear();

    org.junit.Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(map.size()));
    org.junit.Assert.assertEquals(Boolean.FALSE, Boolean.valueOf(map.containsKey(null)));

    map.put("one", Integer.valueOf(1));
    map.put("two", Integer.valueOf(2));
    map.put("three", Integer.valueOf(3));
    map.put("four", Integer.valueOf(4));
    map.clear();

    org.junit.Assert.assertEquals(Integer.valueOf(0), Integer.valueOf(map.size()));
    org.junit.Assert.assertEquals(null, map.get("one"));
    org.junit.Assert.assertEquals(Boolean.FALSE, Boolean.valueOf(map.containsKey("four")));
}

@org.junit.Test
public void cloneOfDelegateMapCanBeModifiedIndependently() {
    org.apache.commons.collections.map.Flat3Map original =
            new org.apache.commons.collections.map.Flat3Map();
    original.put("one", Integer.valueOf(1));
    original.put("two", Integer.valueOf(2));
    original.put("three", Integer.valueOf(3));
    original.put("four", Integer.valueOf(4));

    org.apache.commons.collections.map.Flat3Map copy =
            (org.apache.commons.collections.map.Flat3Map) original.clone();
    copy.put("copy-only", "value");

    org.junit.Assert.assertEquals(Integer.valueOf(4), Integer.valueOf(original.size()));
    org.junit.Assert.assertEquals(Integer.valueOf(5), Integer.valueOf(copy.size()));
    org.junit.Assert.assertEquals(null, original.get("copy-only"));
    org.junit.Assert.assertEquals("value", copy.get("copy-only"));
}
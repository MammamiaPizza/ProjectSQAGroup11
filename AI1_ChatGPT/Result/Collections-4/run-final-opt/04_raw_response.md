    @org.junit.Test
    public void testDefaultConstructorContainsValuesAndClear() {
        org.apache.commons.collections.map.MultiValueMap map =
                new org.apache.commons.collections.map.MultiValueMap();

        org.junit.Assert.assertFalse(map.containsValue("missing"));
        map.put("first", "a");
        map.put("second", "b");

        org.junit.Assert.assertTrue(map.containsValue("a"));
        org.junit.Assert.assertTrue(map.containsValue("b"));
        org.junit.Assert.assertFalse(map.containsValue("missing"));

        map.clear();

        org.junit.Assert.assertEquals(0, map.totalSize());
        org.junit.Assert.assertFalse(map.containsValue("a"));
    }

    @org.junit.Test
    public void testPutAllForExistingKeyAndEmptyCollection() {
        org.apache.commons.collections.map.MultiValueMap map =
                new org.apache.commons.collections.map.MultiValueMap();
        map.put("key", "a");

        org.junit.Assert.assertTrue(map.putAll("key",
                java.util.Arrays.asList(new String[] { "b", "c" })));
        org.junit.Assert.assertEquals(3, map.size("key"));
        org.junit.Assert.assertEquals(3, map.totalSize());
        org.junit.Assert.assertTrue(map.containsValue("key", "a"));
        org.junit.Assert.assertTrue(map.containsValue("key", "b"));
        org.junit.Assert.assertTrue(map.containsValue("key", "c"));

        org.junit.Assert.assertFalse(map.putAll("key", java.util.Collections.EMPTY_LIST));
        org.junit.Assert.assertEquals(3, map.size("key"));
    }

    @org.junit.Test
    public void testIteratorForAbsentAndPresentKey() {
        org.apache.commons.collections.map.MultiValueMap map =
                new org.apache.commons.collections.map.MultiValueMap();

        org.junit.Assert.assertFalse(map.iterator("missing").hasNext());

        map.put("key", "a");
        java.util.Iterator iterator = map.iterator("key");
        org.junit.Assert.assertTrue(iterator.hasNext());
        org.junit.Assert.assertEquals("a", iterator.next());
        org.junit.Assert.assertFalse(iterator.hasNext());
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testDecorateRejectsNullCollectionFactory() {
        org.apache.commons.collections.map.MultiValueMap.decorate(
                new java.util.HashMap(),
                (org.apache.commons.collections.Factory) null);
    }
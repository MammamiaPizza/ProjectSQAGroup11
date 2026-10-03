@Test
 public void testDefaultConstructor() {
     MultiValueMap map = new MultiValueMap();
     map.put("key", "value");
     assertEquals(1, map.totalSize());
     assertTrue(map.containsValue("value"));
 }

 @Test
 public void testContainsValueObject() {
     MultiValueMap map = MultiValueMap.decorate(new java.util.HashMap());
     map.put("key1", "a");
     map.put("key2", "b");
     assertTrue(map.containsValue("a"));
     assertTrue(map.containsValue("b"));
     assertFalse(map.containsValue("c"));
 }

 @Test
 public void testClear() {
     MultiValueMap map = MultiValueMap.decorate(new java.util.HashMap());
     map.put("key1", "a");
     map.put("key2", "b");
     assertEquals(2, map.size());
     map.clear();
     assertEquals(0, map.size());
     assertTrue(map.isEmpty());
 }

 @Test(expected = IllegalArgumentException.class)
 public void testNullFactoryThrows() {
     MultiValueMap.decorate(new java.util.HashMap(), (org.apache.commons.collections.Factory) null);
 }
@Test
public void testConstructorWithMap() {
    java.util.Map<String, String> source = new java.util.HashMap<String, String>();
    source.put("A", "1");
    source.put("B", "2");
    Flat3Map map = new Flat3Map(source);
    assertEquals(2, map.size());
    assertEquals("1", map.get("A"));
    assertEquals("2", map.get("B"));
    // verify independence from source
    source.put("C", "3");
    assertEquals(2, map.size());
    assertNull(map.get("C"));
}

@Test
public void testClone() {
    // clone flat map (no delegate)
    Flat3Map original = new Flat3Map();
    original.put("A", "1");
    original.put("B", "2");
    Flat3Map cloned = (Flat3Map) original.clone();
    assertEquals(original.size(), cloned.size());
    assertEquals(original.get("A"), cloned.get("A"));
    assertEquals(original.get("B"), cloned.get("B"));
    cloned.put("C", "3");
    assertEquals(2, original.size()); // original unchanged

 // clone map with delegate (size >= 4 triggers convertToMap)
 original.put("C", "3");
 original.put("D", "4");
 cloned = (Flat3Map) original.clone();
 assertEquals(4, cloned.size());
 assertEquals(original.get("A"), cloned.get("A"));
 assertEquals(original.get("D"), cloned.get("D"));
 cloned.put("E", "5");
 assertEquals(4, original.size()); // original unchanged

}

@Test
public void testClearFlatMap() {
    Flat3Map map = new Flat3Map();
    map.put("A", "1");
    map.put("B", "2");
    assertEquals(2, map.size());
    map.clear();
    assertEquals(0, map.size());
    assertFalse(map.containsKey("A"));
    assertNull(map.get("A"));
    assertNull(map.get("B"));
}

@Test
public void testContainsKeyAndValueWithDelegateAndFlatNullValues() {
    // flat map: exercise null-value and non-null-value branches of containsValue
    Flat3Map flatMap = new Flat3Map();
    flatMap.put("X", null);
    flatMap.put("Y", "value");
    flatMap.put("Z", null);
    assertTrue(flatMap.containsValue(null));
    assertTrue(flatMap.containsValue("value"));
    assertFalse(flatMap.containsValue("missing"));

 // delegate map: exercise containsKey/containsValue delegation (lines 189,224)
 Flat3Map delegateMap = new Flat3Map();
 delegateMap.put("A", "1");
 delegateMap.put("B", "2");
 delegateMap.put("C", "3");
 delegateMap.put("D", "4"); // triggers convertToMap
 assertTrue(delegateMap.containsKey("A"));
 assertTrue(delegateMap.containsValue("3"));
 assertFalse(delegateMap.containsKey("Z"));
 assertFalse(delegateMap.containsValue("99"));
 // null value via delegate
 delegateMap.put("E", null);
 assertTrue(delegateMap.containsValue(null));

}
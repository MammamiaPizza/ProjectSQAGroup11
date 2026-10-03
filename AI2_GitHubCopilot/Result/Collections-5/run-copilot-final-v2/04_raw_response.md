@Test
    public void testAddAllAtIndexWithPartialDuplicates() {
        List base = new ArrayList();
        base.add("a");
        base.add("b");
        SetUniqueList list = SetUniqueList.decorate(base);
        java.util.List toAdd = new ArrayList();
        toAdd.add("x");
        toAdd.add("a");
        toAdd.add("y");
        list.addAll(0, toAdd);
        assertEquals(4, list.size());
        assertEquals("x", list.get(0));
        assertEquals("y", list.get(1));
        assertEquals("a", list.get(2));
        assertEquals("b", list.get(3));
    }

 @Test
 public void testDecorateNonEmptyList() {
     List base = new ArrayList();
     base.add("a");
     base.add("b");
     base.add("a");
     SetUniqueList list = SetUniqueList.decorate(base);
     assertEquals(2, list.size());
     assertEquals("a", list.get(0));
     assertEquals("b", list.get(1));
     assertFalse(list.contains("c"));
 }

 @Test
 public void testRemoveAllUpdatesSet() {
     List base = new ArrayList();
     base.add("a");
     base.add("b");
     base.add("c");
     SetUniqueList list = SetUniqueList.decorate(base);
     java.util.List toRemove = new ArrayList();
     toRemove.add("a");
     toRemove.add("c");
     assertTrue(list.removeAll(toRemove));
     assertEquals(1, list.size());
     assertEquals("b", list.get(0));
     assertTrue(list.contains("b"));
     assertFalse(list.contains("a"));
     assertFalse(list.contains("c"));
 }

 @Test
 public void testRetainAllUpdatesSet() {
     List base = new ArrayList();
     base.add("a");
     base.add("b");
     base.add("c");
     SetUniqueList list = SetUniqueList.decorate(base);
     java.util.List toRetain = new ArrayList();
     toRetain.add("b");
     toRetain.add("d");
     assertTrue(list.retainAll(toRetain));
     assertEquals(1, list.size());
     assertEquals("b", list.get(0));
     assertTrue(list.contains("b"));
     assertFalse(list.contains("a"));
     assertFalse(list.contains("c"));
 }
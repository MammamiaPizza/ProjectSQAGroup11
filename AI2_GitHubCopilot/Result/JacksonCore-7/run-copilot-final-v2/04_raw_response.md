@Test
 public void testCreateChildArrayContextReusesExistingChild() {
     JsonWriteContext root = JsonWriteContext.createRootContext();
     JsonWriteContext child1 = root.createChildArrayContext();
     JsonWriteContext child2 = root.createChildArrayContext();
     assertSame(child1, child2);
     assertEquals(com.fasterxml.jackson.core.JsonStreamContext.TYPE_ARRAY, child2.getType());
 }

 @Test
 public void testCreateChildObjectContextReusesExistingChild() {
     JsonWriteContext root = JsonWriteContext.createRootContext();
     JsonWriteContext child1 = root.createChildObjectContext();
     JsonWriteContext child2 = root.createChildObjectContext();
     assertSame(child1, child2);
     assertEquals(com.fasterxml.jackson.core.JsonStreamContext.TYPE_OBJECT, child2.getType());
 }

 @Test
 public void testAppendDescObjectWithCurrentName() {
     JsonWriteContext obj = JsonWriteContext.createRootContext().createChildObjectContext();
     obj.writeFieldName("testName");
     StringBuilder sb = new StringBuilder();
     obj.appendDesc(sb);
     assertEquals("{\"testName\"}", sb.toString());
 }
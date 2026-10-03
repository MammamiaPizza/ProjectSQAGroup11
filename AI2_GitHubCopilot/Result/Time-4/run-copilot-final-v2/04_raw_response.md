public void testConstructor_emptyArray() {
     Partial p = new Partial(new DateTimeFieldType[0], new int[0]);
     assertEquals(0, p.size());
 }

 public void testConstructor_duplicateTypes() {
     try {
         new Partial(new DateTimeFieldType[]{DateTimeFieldType.year(),
DateTimeFieldType.monthOfYear(), DateTimeFieldType.monthOfYear()},
                     new int[]{2010, 6, 12});
         fail("Should have thrown IllegalArgumentException");
     } catch (IllegalArgumentException ex) {
         // expected
     }
 }

 public void testWith_AddNewField() {
     Partial p = new Partial(new DateTimeFieldType[]{DateTimeFieldType.year(),
DateTimeFieldType.monthOfYear()}, new int[]{2010, 6});
     Partial result = p.with(DateTimeFieldType.dayOfMonth(), 15);
     assertEquals(3, result.size());
     assertEquals(DateTimeFieldType.year(), result.getFieldType(0));
     assertEquals(2010, result.getValue(0));
     assertEquals(DateTimeFieldType.monthOfYear(), result.getFieldType(1));
     assertEquals(6, result.getValue(1));
     assertEquals(DateTimeFieldType.dayOfMonth(), result.getFieldType(2));
     assertEquals(15, result.getValue(2));
 }

 public void testWithout_RemoveField() {
     Partial p = new Partial(new DateTimeFieldType[]{DateTimeFieldType.year(),
DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()}, new int[]{2010, 6, 15});
     Partial result = p.without(DateTimeFieldType.monthOfYear());
     assertEquals(2, result.size());
     assertEquals(DateTimeFieldType.year(), result.getFieldType(0));
     assertEquals(2010, result.getValue(0));
     assertEquals(DateTimeFieldType.dayOfMonth(), result.getFieldType(1));
     assertEquals(15, result.getValue(1));
 }
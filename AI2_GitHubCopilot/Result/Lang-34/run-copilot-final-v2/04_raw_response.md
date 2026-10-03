@Test
 public void testAppendInt() {
     StringBuffer buffer = new StringBuffer();
     ToStringStyle.DEFAULT_STYLE.append(buffer, "intField", 123);
     assertTrue("Buffer should contain field name", buffer.toString().contains("intField"));
 }

 @Test
 public void testAppendLong() {
     StringBuffer buffer = new StringBuffer();
     ToStringStyle.DEFAULT_STYLE.append(buffer, "longField", 456L);
     assertTrue("Buffer should contain field name", buffer.toString().contains("longField"));
 }

 @Test
 public void testAppendByte() {
     StringBuffer buffer = new StringBuffer();
     ToStringStyle.DEFAULT_STYLE.append(buffer, "byteField", (byte) 7);
     assertTrue("Buffer should contain field name", buffer.toString().contains("byteField"));
 }

 @Test
 public void testAppendChar() {
     StringBuffer buffer = new StringBuffer();
     ToStringStyle.DEFAULT_STYLE.append(buffer, "charField", 'x');
     assertTrue("Buffer should contain field name", buffer.toString().contains("charField"));
 }
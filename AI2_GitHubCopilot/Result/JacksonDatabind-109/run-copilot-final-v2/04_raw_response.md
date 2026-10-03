@Test
    public void testAcceptJsonFormatVisitorForBigIntegerSerializer() throws Exception {
        NumberSerializer ser = new NumberSerializer(java.math.BigInteger.class);
        ser.acceptJsonFormatVisitor(
                new
com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base() {},
                null);
    }

 @Test
 public void testAcceptJsonFormatVisitorForBigDecimalSerializer() throws Exception {
     NumberSerializer ser = new NumberSerializer(java.math.BigDecimal.class);
     ser.acceptJsonFormatVisitor(
             new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base()
{},
             null);
 }

 @Test
 public void testAcceptJsonFormatVisitorDefault() throws Exception {
     NumberSerializer ser = NumberSerializer.instance;
     ser.acceptJsonFormatVisitor(
             new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base()
{},
             null);
 }
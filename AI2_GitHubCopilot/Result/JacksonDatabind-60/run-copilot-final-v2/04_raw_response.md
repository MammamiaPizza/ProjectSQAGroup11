static class BooleanBean {
        @com.fasterxml.jackson.annotation.JsonValue
        public boolean getValue() { return true; }
    }

 static class DoubleBean {
     @com.fasterxml.jackson.annotation.JsonValue
     public double getValue() { return 3.14; }
 }

 static enum ExceptionEnum {
     V1,
     V2;
     @com.fasterxml.jackson.annotation.JsonValue
     public String getValue() {
         throw new RuntimeException("test");
     }
 }

 @com.fasterxml.jackson.annotation.JsonTypeInfo(use =
com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS)
 static class BeanWithTypeInfo {
     @com.fasterxml.jackson.annotation.JsonValue
     public int getValue() { return 42; }
 }

 @Test
 public void testJsonValueWithBooleanAndDefaultTyping() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();

mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL);
     String json = mapper.writeValueAsString(new BooleanBean());
     assertTrue("Type id should be the bean class", json.contains("\"@class\":\"" +
BooleanBean.class.getName() + "\""));
     assertFalse("Type id must not be 'boolean'", json.contains("\"@class\":\"boolean\""));
 }

 @Test
 public void testJsonValueWithDoubleAndDefaultTyping() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();

mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL);
     String json = mapper.writeValueAsString(new DoubleBean());
     assertTrue("Type id should be the bean class", json.contains("\"@class\":\"" +
DoubleBean.class.getName() + "\""));
     assertFalse("Type id must not be 'double'", json.contains("\"@class\":\"double\""));
 }

 @Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
 public void testEnumJsonValueWithExceptionInFormatVisitor() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base visitor =
         new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base(mapper)
{
         @Override
         public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor
expectStringFormat(com.fasterxml.jackson.databind.JavaType type) {
             return null;
         }
     };
     mapper.acceptJsonFormatVisitor(ExceptionEnum.class, visitor);
 }

 @Test
 public void testJsonValueWithTypeInfoAnnotation() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     String json = mapper.writeValueAsString(new BeanWithTypeInfo());
     assertTrue("Type id should be bean class", json.contains("\"@class\":\"" +
BeanWithTypeInfo.class.getName() + "\""));
 }
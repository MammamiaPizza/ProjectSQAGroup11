@Test
    public void jsonAppendWithoutWriterClass_shouldThrowInvalidDefinitionException() throws
Exception {
        @com.fasterxml.jackson.databind.annotation.JsonAppend(
            attrs = {@com.fasterxml.jackson.databind.annotation.JsonAppend.Attr("virtual")}
        )
        class BeanWithVirtual {
            public int x = 42;
        }
        ObjectMapper mapper = new ObjectMapper();
        thrown.expect(com.fasterxml.jackson.databind.exc.InvalidDefinitionException.class);
        mapper.writeValueAsString(new BeanWithVirtual());
    }

 @Test
 public void testNoTypeResolverBuilder_returnsNoTypeInfoBuilder() {
     JacksonAnnotationIntrospector introsp = new JacksonAnnotationIntrospector() {
         public com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder
callConstructNoTypeResolverBuilder() {
             return _constructNoTypeResolverBuilder();
         }
     };
     com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder builder =
introsp.callConstructNoTypeResolverBuilder();
     assertNotNull(builder);
 }

 @Test
 public void testStandardTypeResolverBuilder_withJsonTypeInfo() throws Exception {
     @com.fasterxml.jackson.annotation.JsonTypeInfo(
         use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
         include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
     )
     class BeanWithTypeInfo {
         public int value = 10;
     }
     ObjectMapper mapper = new ObjectMapper();
     String json = mapper.writeValueAsString(new BeanWithTypeInfo());
     assertNotNull(json);
     assertTrue(json.contains("@type"));
 }

 @Test
 public void testClassIfExplicit_returnsNullWhenEqualsImplicit() {
     JacksonAnnotationIntrospector introsp = new JacksonAnnotationIntrospector() {
         public Class<?> callClassIfExplicit(Class<?> cls, Class<?> implicit) {
             return _classIfExplicit(cls, implicit);
         }
     };
     assertNull(introsp.callClassIfExplicit(Integer.class, Integer.class));
 }
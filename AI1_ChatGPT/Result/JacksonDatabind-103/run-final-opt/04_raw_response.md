@Test
public void databindContextConstructTypeHandlesNullAndMatchingSpecialization() {
    com.fasterxml.jackson.databind.DeserializationContext context =
            new com.fasterxml.jackson.databind.ObjectMapper().getDeserializationContext();

    assertTrue(context.constructType(null) == null);

    com.fasterxml.jackson.databind.JavaType stringType = context.constructType(String.class);
    assertTrue(stringType == context.constructSpecializedType(stringType, String.class));
}

@Test
public void classUtilRecognizesContainerAndBogusClasses() {
    assertTrue(com.fasterxml.jackson.databind.util.ClassUtil.isCollectionMapOrArray(String[].class));
    assertTrue(com.fasterxml.jackson.databind.util.ClassUtil.isCollectionMapOrArray(java.util.ArrayList.class));
    assertTrue(com.fasterxml.jackson.databind.util.ClassUtil.isCollectionMapOrArray(java.util.LinkedHashMap.class));
    assertTrue(!com.fasterxml.jackson.databind.util.ClassUtil.isCollectionMapOrArray(String.class));

    assertTrue(com.fasterxml.jackson.databind.util.ClassUtil.isBogusClass(Void.class));
    assertTrue(com.fasterxml.jackson.databind.util.ClassUtil.isBogusClass(Void.TYPE));
    assertTrue(com.fasterxml.jackson.databind.util.ClassUtil.isBogusClass(
            com.fasterxml.jackson.databind.annotation.NoClass.class));
    assertTrue(!com.fasterxml.jackson.databind.util.ClassUtil.isBogusClass(String.class));
}
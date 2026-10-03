@Test
public void referenceTypeExposesReferencedTypeMetadata()
{
    com.fasterxml.jackson.databind.JavaType referenced = com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
            .constructType(String.class);
    com.fasterxml.jackson.databind.type.ReferenceType type = com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance()
            .constructReferenceType(java.util.concurrent.atomic.AtomicReference.class, referenced);

    org.junit.Assert.assertSame(referenced, type.getReferencedType());
    org.junit.Assert.assertTrue(type.isReferenceType());
    org.junit.Assert.assertEquals(1, type.containedTypeCount());
    org.junit.Assert.assertSame(referenced, type.containedType(0));
    org.junit.Assert.assertNull(type.containedType(1));
    org.junit.Assert.assertEquals("T", type.containedTypeName(0));
    org.junit.Assert.assertNull(type.containedTypeName(1));
    org.junit.Assert.assertSame(java.util.concurrent.atomic.AtomicReference.class, type.getParameterSource());
}

@Test
public void referenceTypeEqualityUsesRawAndReferencedTypes()
{
    com.fasterxml.jackson.databind.type.TypeFactory factory = com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.type.ReferenceType stringReference = factory.constructReferenceType(
            java.util.concurrent.atomic.AtomicReference.class, factory.constructType(String.class));
    com.fasterxml.jackson.databind.type.ReferenceType equalStringReference = factory.constructReferenceType(
            java.util.concurrent.atomic.AtomicReference.class, factory.constructType(String.class));
    com.fasterxml.jackson.databind.type.ReferenceType integerReference = factory.constructReferenceType(
            java.util.concurrent.atomic.AtomicReference.class, factory.constructType(Integer.class));
    com.fasterxml.jackson.databind.type.ReferenceType differentRawReference = factory.constructReferenceType(
            java.util.concurrent.atomic.AtomicReferenceArray.class, factory.constructType(String.class));

    org.junit.Assert.assertTrue(stringReference.equals(stringReference));
    org.junit.Assert.assertTrue(stringReference.equals(equalStringReference));
    org.junit.Assert.assertFalse(stringReference.equals(null));
    org.junit.Assert.assertFalse(stringReference.equals("not a reference type"));
    org.junit.Assert.assertFalse(stringReference.equals(integerReference));
    org.junit.Assert.assertFalse(stringReference.equals(differentRawReference));
}
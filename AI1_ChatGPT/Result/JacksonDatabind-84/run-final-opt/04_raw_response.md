@Test
public void delegatesGenericAndErasedSignaturesToReference() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType recursive =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());
    com.fasterxml.jackson.databind.JavaType reference =
            new com.fasterxml.jackson.databind.ObjectMapper().constructType(String.class);
    recursive.setReference(reference);

    org.junit.Assert.assertEquals(
            reference.getGenericSignature(new StringBuilder()).toString(),
            recursive.getGenericSignature(new StringBuilder()).toString());
    org.junit.Assert.assertEquals(
            reference.getErasedSignature(new StringBuilder()).toString(),
            recursive.getErasedSignature(new StringBuilder()).toString());
}

@Test
public void toStringDescribesUnresolvedAndResolvedReferences() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType recursive =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());

    org.junit.Assert.assertTrue(recursive.toString().contains("UNRESOLVED"));

    recursive.setReference(new com.fasterxml.jackson.databind.ObjectMapper().constructType(String.class));
    org.junit.Assert.assertTrue(recursive.toString().contains(String.class.getName()));
}

@Test
public void equalsUsesReferencedTypeAndDoesNotMatchUnresolvedTypes() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType unresolved =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType anotherUnresolved =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());

    org.junit.Assert.assertTrue(unresolved.equals(unresolved));
    org.junit.Assert.assertFalse(unresolved.equals(anotherUnresolved));
    org.junit.Assert.assertFalse(unresolved.equals(null));

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    unresolved.setReference(mapper.constructType(String.class));
    anotherUnresolved.setReference(mapper.constructType(String.class));
    org.junit.Assert.assertTrue(unresolved.equals(anotherUnresolved));
    org.junit.Assert.assertTrue(anotherUnresolved.equals(unresolved));

    com.fasterxml.jackson.databind.type.ResolvedRecursiveType different =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());
    different.setReference(mapper.constructType(Integer.class));
    org.junit.Assert.assertFalse(unresolved.equals(different));
}

@Test
public void recursiveTypeIsNotReportedAsContainer() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType recursive =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(
                    Object.class, com.fasterxml.jackson.databind.type.TypeBindings.emptyBindings());

    org.junit.Assert.assertFalse(recursive.isContainerType());
}
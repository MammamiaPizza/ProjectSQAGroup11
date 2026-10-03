@org.junit.Test
public void handlerAndStaticTypingVariantsLeaveRecursiveTypeUnchanged() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType recursive =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(String.class, null);
    com.fasterxml.jackson.databind.JavaType contentType =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(Integer.class, null);
    Object handler = new Object();

    org.junit.Assert.assertSame(recursive, recursive.withContentType(contentType));
    org.junit.Assert.assertSame(recursive, recursive.withTypeHandler(handler));
    org.junit.Assert.assertSame(recursive, recursive.withContentTypeHandler(handler));
    org.junit.Assert.assertSame(recursive, recursive.withValueHandler(handler));
    org.junit.Assert.assertSame(recursive, recursive.withContentValueHandler(handler));
    org.junit.Assert.assertSame(recursive, recursive.withStaticTyping());
    org.junit.Assert.assertFalse(recursive.isContainerType());
}

@org.junit.Test
public void toStringDescribesUnresolvedAndResolvedRecursiveTypes() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType recursive =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(String.class, null);

    org.junit.Assert.assertEquals("[recursive type; UNRESOLVED]", recursive.toString());

    recursive.setReference(new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(Integer.class, null));

    org.junit.Assert.assertEquals("[recursive type; java.lang.Integer]", recursive.toString());
}

@org.junit.Test
public void equalsOnlyMatchesResolvedRecursiveTypesWithEqualReferences() {
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType unresolved =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(String.class, null);
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType otherUnresolved =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(String.class, null);
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType sharedReference =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(Integer.class, null);
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType differentReference =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(Long.class, null);
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType first =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(String.class, null);
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType second =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(String.class, null);
    com.fasterxml.jackson.databind.type.ResolvedRecursiveType different =
            new com.fasterxml.jackson.databind.type.ResolvedRecursiveType(String.class, null);

    org.junit.Assert.assertTrue(unresolved.equals(unresolved));
    org.junit.Assert.assertFalse(unresolved.equals(null));
    org.junit.Assert.assertFalse(unresolved.equals(otherUnresolved));

    first.setReference(sharedReference);
    second.setReference(sharedReference);
    different.setReference(differentReference);

    org.junit.Assert.assertTrue(first.equals(second));
    org.junit.Assert.assertFalse(first.equals(different));
    org.junit.Assert.assertFalse(first.equals(new Object()));
}
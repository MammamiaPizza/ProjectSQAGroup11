@org.junit.Test
public void referenceTypeConstructProducesJvmSignatures() {
    com.fasterxml.jackson.databind.JavaType longType =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance().constructType(java.lang.Long.class);
    ReferenceType referenceType = ReferenceType.construct(
            java.util.concurrent.atomic.AtomicReference.class, longType);

    org.junit.Assert.assertEquals(
            "Ljava/util/concurrent/atomic/AtomicReference;",
            referenceType.getErasedSignature(new StringBuilder()).toString());
    org.junit.Assert.assertEquals(
            "Ljava/util/concurrent/atomic/AtomicReference<Ljava/lang/Long;>;",
            referenceType.getGenericSignature(new StringBuilder()).toString());
}

@org.junit.Test
public void referenceTypeEqualityUsesRawAndReferencedTypes() {
    com.fasterxml.jackson.databind.type.TypeFactory typeFactory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    ReferenceType longReference = ReferenceType.construct(
            java.util.concurrent.atomic.AtomicReference.class,
            typeFactory.constructType(java.lang.Long.class));
    ReferenceType equivalentLongReference = ReferenceType.construct(
            java.util.concurrent.atomic.AtomicReference.class,
            typeFactory.constructType(java.lang.Long.class));
    ReferenceType stringReference = ReferenceType.construct(
            java.util.concurrent.atomic.AtomicReference.class,
            typeFactory.constructType(java.lang.String.class));
    ReferenceType differentRawReference = ReferenceType.construct(
            java.lang.ref.WeakReference.class,
            typeFactory.constructType(java.lang.Long.class));

    org.junit.Assert.assertTrue(longReference.equals(longReference));
    org.junit.Assert.assertTrue(longReference.equals(equivalentLongReference));
    org.junit.Assert.assertFalse(longReference.equals(null));
    org.junit.Assert.assertFalse(longReference.equals("not a reference type"));
    org.junit.Assert.assertFalse(longReference.equals(stringReference));
    org.junit.Assert.assertFalse(longReference.equals(differentRawReference));
}

@org.junit.Test
public void upgradedReferenceTypeIsItsOwnAnchorAndRetainsReferencedType() {
    com.fasterxml.jackson.databind.type.TypeFactory typeFactory =
            com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance();
    com.fasterxml.jackson.databind.JavaType baseType = typeFactory.constructType(
            java.util.concurrent.atomic.AtomicReference.class);
    com.fasterxml.jackson.databind.JavaType referencedType = typeFactory.constructType(java.lang.Long.class);

    ReferenceType upgraded = ReferenceType.upgradeFrom(baseType, referencedType);

    org.junit.Assert.assertTrue(upgraded.isAnchorType());
    org.junit.Assert.assertSame(upgraded, upgraded.getAnchorType());
    org.junit.Assert.assertSame(referencedType, upgraded.getReferencedType());
}
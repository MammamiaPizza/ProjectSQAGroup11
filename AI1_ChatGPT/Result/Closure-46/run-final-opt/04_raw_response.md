@org.junit.Test
public void recordIsNotEquivalentToANonRecordType() {
  assertFalse(recordWithProperty("a",
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE))
      .isEquivalentTo(
          registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE)));
}

@org.junit.Test
public void recordIsSubtypeOfObjectType() {
  assertTrue(recordWithProperty("a",
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE))
      .isSubtype(
          registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.OBJECT_TYPE)));
}

@org.junit.Test
public void greatestSubtypeOfRecordsWithConflictingPropertiesIsNoType() {
  com.google.javascript.rhino.jstype.RecordType numberRecord =
      recordWithProperty("a",
          registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE));
  com.google.javascript.rhino.jstype.RecordType stringRecord =
      recordWithProperty("a",
          registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.STRING_TYPE));

  assertTrue(registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NO_TYPE)
      .isEquivalentTo(numberRecord.getGreatestSubtype(stringRecord)));
}

@org.junit.Test
public void leastSupertypeOfRecordAndObjectTypeIsObjectType() {
  com.google.javascript.rhino.jstype.RecordType record =
      recordWithProperty("a",
          registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE));
  com.google.javascript.rhino.jstype.JSType objectType =
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.OBJECT_TYPE);

  assertTrue(objectType.isEquivalentTo(record.getLeastSupertype(objectType)));
}

private com.google.javascript.rhino.jstype.RecordType recordWithProperty(
    String property, com.google.javascript.rhino.jstype.JSType type) {
  com.google.javascript.rhino.jstype.RecordTypeBuilder builder =
      new com.google.javascript.rhino.jstype.RecordTypeBuilder(registry);
  builder.addProperty(property, type, null);
  return builder.build();
}
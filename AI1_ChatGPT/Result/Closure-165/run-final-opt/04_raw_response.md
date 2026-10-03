@Test
public void testRecordPropertyRetainsDeclaredMetadata() {
  com.google.javascript.rhino.jstype.JSType numberType =
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE);
  com.google.javascript.rhino.Node propertyNode =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.NAME);

  com.google.javascript.rhino.jstype.RecordTypeBuilder builder =
      new com.google.javascript.rhino.jstype.RecordTypeBuilder(registry);
  builder.addProperty("answer", numberType, propertyNode);
  com.google.javascript.rhino.jstype.ObjectType record = builder.build().toObjectType();

  assertTrue(record.hasOwnProperty("answer"));
  assertSame(numberType, record.getPropertyType("answer"));
  assertFalse(record.isPropertyTypeInferred("answer"));
  assertSame(propertyNode, record.getPropertyNode("answer"));
  assertNull(record.getImplicitPrototype());
}
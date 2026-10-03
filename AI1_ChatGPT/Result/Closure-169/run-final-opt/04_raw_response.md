@Test
public void testRecordWithUnknownPropertyIsSubtypeOfRecordWithUnionProperty() {
  com.google.javascript.rhino.jstype.JSTypeRegistry registry =
      new com.google.javascript.rhino.jstype.JSTypeRegistry(null);
  com.google.javascript.rhino.jstype.JSType numberType =
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE);
  com.google.javascript.rhino.jstype.JSType stringType =
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.STRING_TYPE);
  com.google.javascript.rhino.jstype.JSType unknownType =
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.UNKNOWN_TYPE);

  com.google.javascript.rhino.jstype.UnionTypeBuilder unionBuilder =
      new com.google.javascript.rhino.jstype.UnionTypeBuilder(registry);
  unionBuilder.addAlternate(numberType);
  unionBuilder.addAlternate(stringType);

  com.google.javascript.rhino.jstype.RecordTypeBuilder sourceBuilder =
      new com.google.javascript.rhino.jstype.RecordTypeBuilder(registry);
  sourceBuilder.addProperty("value", unknownType, null);

  com.google.javascript.rhino.jstype.RecordTypeBuilder targetBuilder =
      new com.google.javascript.rhino.jstype.RecordTypeBuilder(registry);
  targetBuilder.addProperty("value", unionBuilder.build(), null);

  assertTrue(sourceBuilder.build().isSubtype(targetBuilder.build()));
}

@Test
public void testUnionCanAssignOnlyWhenEveryAlternateCanAssign() {
  com.google.javascript.rhino.jstype.JSTypeRegistry registry =
      new com.google.javascript.rhino.jstype.JSTypeRegistry(null);
  com.google.javascript.rhino.jstype.JSType numberType =
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE);
  com.google.javascript.rhino.jstype.JSType stringType =
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.STRING_TYPE);

  com.google.javascript.rhino.jstype.UnionTypeBuilder builder =
      new com.google.javascript.rhino.jstype.UnionTypeBuilder(registry);
  builder.addAlternate(numberType);
  builder.addAlternate(stringType);

  assertFalse(builder.build().canAssignTo(numberType));
}

@Test
public void testArrowTypeIsAlwaysTruthy() {
  com.google.javascript.rhino.jstype.JSTypeRegistry registry =
      new com.google.javascript.rhino.jstype.JSTypeRegistry(null);
  com.google.javascript.rhino.jstype.JSType numberType =
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE);

  com.google.javascript.rhino.jstype.FunctionType function =
      registry.createFunctionType(numberType);

  assertEquals(
      com.google.javascript.rhino.jstype.BooleanLiteralSet.TRUE,
      function.getInternalArrowType().getPossibleToBooleanOutcomes());
}

@Test
public void testArrowTypeDoesNotSupportLeastSupertype() {
  com.google.javascript.rhino.jstype.JSTypeRegistry registry =
      new com.google.javascript.rhino.jstype.JSTypeRegistry(null);
  com.google.javascript.rhino.jstype.JSType numberType =
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE);
  com.google.javascript.rhino.jstype.FunctionType function =
      registry.createFunctionType(numberType);

  boolean threw = false;
  try {
    function.getInternalArrowType().getLeastSupertype(numberType);
  } catch (UnsupportedOperationException expected) {
    threw = true;
  }
  assertTrue(threw);
}
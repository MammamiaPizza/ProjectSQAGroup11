package com.google.javascript.rhino.jstype;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UnionTypeGreatestSubtypeRegressionTest {

  @Test
  public void disjointObjectUnionsHaveNoObjectGreatestSubtype() {
    JSTypeRegistry registry = new JSTypeRegistry(null);

    UnionTypeBuilder leftBuilder = new UnionTypeBuilder(registry);
    leftBuilder.addAlternate(registry.getNativeType(JSTypeNative.ARRAY_TYPE));
    leftBuilder.addAlternate(registry.getNativeType(JSTypeNative.DATE_TYPE));
    JSType left = leftBuilder.build();

    UnionTypeBuilder rightBuilder = new UnionTypeBuilder(registry);
    rightBuilder.addAlternate(registry.getNativeType(JSTypeNative.REGEXP_TYPE));
    rightBuilder.addAlternate(registry.getNativeType(JSTypeNative.NUMBER_OBJECT_TYPE));
    JSType right = rightBuilder.build();

    assertEquals(
        registry.getNativeType(JSTypeNative.NO_OBJECT_TYPE),
        left.getGreatestSubtype(right));
  }
}

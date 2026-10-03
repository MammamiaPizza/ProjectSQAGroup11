package com.google.gson.internal.bind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.junit.Test;

public class TypeAdaptersNumericStringTest {

  private final Gson gson = new Gson();

  @Test
  public void numberDeserializesQuotedIntegralValue() {
    Number value = gson.fromJson("\"42\"", Number.class);

    assertEquals(42, value.intValue());
  }

  @Test
  public void numberDeserializesQuotedDecimalValue() {
    Number value = gson.fromJson("\"12.5\"", Number.class);

    assertEquals(12.5d, value.doubleValue(), 0.0d);
  }

  @Test
  public void integralWrapperTypesDeserializeQuotedBoundaryValues() {
    Integer integer = gson.fromJson("\"2147483647\"", Integer.class);
    Short shortValue = gson.fromJson("\"-32768\"", Short.class);
    Byte byteValue = gson.fromJson("\"127\"", Byte.class);

    assertEquals(Integer.valueOf(Integer.MAX_VALUE), integer);
    assertEquals(Short.valueOf(Short.MIN_VALUE), shortValue);
    assertEquals(Byte.valueOf(Byte.MAX_VALUE), byteValue);
  }

  @Test
  public void integralWrapperTypesDeserializeUnquotedValues() {
    Integer integer = gson.fromJson("-123", Integer.class);
    Short shortValue = gson.fromJson("1234", Short.class);
    Byte byteValue = gson.fromJson("-12", Byte.class);

    assertEquals(Integer.valueOf(-123), integer);
    assertEquals(Short.valueOf((short) 1234), shortValue);
    assertEquals(Byte.valueOf((byte) -12), byteValue);
  }

  @Test
  public void numericTypesDeserializeJsonNullAsNull() {
    assertNull(gson.fromJson("null", Number.class));
    assertNull(gson.fromJson("null", Integer.class));
    assertNull(gson.fromJson("null", Short.class));
    assertNull(gson.fromJson("null", Byte.class));
  }

  @Test(expected = JsonSyntaxException.class)
  public void integerRejectsInvalidQuotedNumericString() {
    gson.fromJson("\"not-a-number\"", Integer.class);
  }

  @Test(expected = JsonSyntaxException.class)
  public void integerRejectsQuotedValueOutsideIntegerRange() {
    gson.fromJson("\"2147483648\"", Integer.class);
  }

@org.junit.Test
public void typeAdapterFactoryCreatedForTypeTokenMatchesOnlyThatExactType() {
  com.google.gson.reflect.TypeToken<java.lang.String> stringType =
      new com.google.gson.reflect.TypeToken<java.lang.String>() {};
  com.google.gson.TypeAdapterFactory factory =
      com.google.gson.internal.bind.TypeAdapters.newFactory(
          stringType, com.google.gson.internal.bind.TypeAdapters.STRING);

  org.junit.Assert.assertSame(
      com.google.gson.internal.bind.TypeAdapters.STRING,
      factory.create(new com.google.gson.Gson(), stringType));
  org.junit.Assert.assertNull(
      factory.create(
          new com.google.gson.Gson(),
          new com.google.gson.reflect.TypeToken<java.lang.Integer>() {}));
}

@org.junit.Test
public void typeAdaptersConstructorRejectsInstantiation() throws java.lang.Exception {
  java.lang.reflect.Constructor<com.google.gson.internal.bind.TypeAdapters> constructor =
      com.google.gson.internal.bind.TypeAdapters.class.getDeclaredConstructor();
  constructor.setAccessible(true);

  try {
    constructor.newInstance();
    org.junit.Assert.fail("Expected constructor to reject instantiation");
  } catch (java.lang.reflect.InvocationTargetException expected) {
    org.junit.Assert.assertTrue(
        expected.getCause() instanceof java.lang.UnsupportedOperationException);
  }
}
}

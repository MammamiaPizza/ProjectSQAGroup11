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
}

package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.IR;
import org.junit.Test;

public class NodeUtilIssue821Test {

  @Test
  public void testStringValueOfLargeIntegerUsesJavaScriptDecimalNotation() {
    assertEquals("100000000000000000000", NodeUtil.getStringValue(1e20));
  }

  @Test
  public void testStringValueAtScientificNotationBoundaryUsesJavaScriptExponentFormat() {
    assertEquals("1e+21", NodeUtil.getStringValue(1e21));
  }

  @Test
  public void testStringValueOfSmallDecimalUsesJavaScriptFixedNotation() {
    assertEquals("0.000001", NodeUtil.getStringValue(1e-6));
  }

  @Test
  public void testStringValueOfSmallScientificNumberOmitsJavaFormattingArtifacts() {
    assertEquals("1e-7", NodeUtil.getStringValue(1e-7));
  }

  @Test
  public void testStringValueOfNegativeZeroIsZero() {
    assertEquals("0", NodeUtil.getStringValue(-0.0d));
  }

  @Test
  public void testStringValuePreservesJavaScriptSpecialNumberSpellings() {
    assertEquals("Infinity", NodeUtil.getStringValue(Double.POSITIVE_INFINITY));
    assertEquals("-Infinity", NodeUtil.getStringValue(Double.NEGATIVE_INFINITY));
    assertEquals("NaN", NodeUtil.getStringValue(Double.NaN));
  }

  @Test
  public void testStringNumberValueHandlesWhitespaceAndHexadecimal() {
    assertEquals(Double.valueOf(16.0), NodeUtil.getStringNumberValue(" \t0x10\n"));
  }

  @Test
  public void testStringNumberValueOfEmptyWhitespaceIsZero() {
    assertEquals(Double.valueOf(0.0), NodeUtil.getStringNumberValue("\u00a0 \t\r\n"));
  }

  @Test
  public void testStringNumberValueOfInvalidTextIsNaN() {
    Double value = NodeUtil.getStringNumberValue("not a number");
    assertTrue(value != null && value.isNaN());
  }

  @Test
  public void testStringNodeNumberValueUsesJavaScriptNumericConversion() {
    assertEquals(Double.valueOf(100000000000000000000d),
        NodeUtil.getNumberValue(IR.string("100000000000000000000")));
  }
}

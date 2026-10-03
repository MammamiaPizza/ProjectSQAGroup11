package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.google.javascript.rhino.jstype.TernaryValue;
import org.junit.Test;

public class NodeUtilStringNumberValueTest {

  @Test
  public void testConvertsNumberSurroundedByJavaScriptWhitespace() {
    Double value = NodeUtil.getStringNumberValue(" \t\n\r\f42.5\u00a0");

    assertEquals(42.5, value.doubleValue(), 0.0);
  }

  @Test
  public void testConvertsEmptyAndWhitespaceOnlyStringsToZero() {
    assertEquals(0.0, NodeUtil.getStringNumberValue("").doubleValue(), 0.0);
    assertEquals(0.0, NodeUtil.getStringNumberValue(" \t\n\r\f\u00a0").doubleValue(), 0.0);
  }

  @Test
  public void testTrimsByteOrderMarkAsJavaScriptWhitespace() {
    assertEquals("17", NodeUtil.trimJsWhiteSpace("\uFEFF17\uFEFF"));
    assertEquals(17.0, NodeUtil.getStringNumberValue("\uFEFF17\uFEFF").doubleValue(), 0.0);
  }

  @Test
  public void testRejectsNonNumericAndMixedStrings() {
    assertEquals(Double.NaN, NodeUtil.getStringNumberValue("not a number"));
    assertEquals(Double.NaN, NodeUtil.getStringNumberValue("12px"));
  }

  @Test
  public void testVerticalTabIsUnknownBecauseOfIeWhitespaceDifference() {
    assertSame(TernaryValue.UNKNOWN, NodeUtil.isStrWhiteSpaceChar('\u000B'));
  }

  @Test
  public void testDoesNotFoldNumberContainingIeSpecificVerticalTabWhitespace() {
    assertEquals(Double.NaN, NodeUtil.getStringNumberValue("\u000B1\u000B"));
  }
}
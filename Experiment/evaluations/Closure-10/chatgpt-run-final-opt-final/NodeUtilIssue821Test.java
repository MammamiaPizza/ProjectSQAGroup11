package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.IR;
import org.junit.Test;

public class NodeUtilIssue821Test {

  @Test
  public void testStringValueOfLargeIntegerUsesJavaScientificNotation() {
    assertEquals("1.0E20", NodeUtil.getStringValue(1e20));
  }

  @Test
  public void testStringValueAtScientificNotationBoundaryUsesJavaExponentFormat() {
    assertEquals("1.0E21", NodeUtil.getStringValue(1e21));
  }

  @Test
  public void testStringValueOfSmallDecimalUsesJavaScientificNotation() {
    assertEquals("1.0E-6", NodeUtil.getStringValue(1e-6));
  }

  @Test
  public void testStringValueOfSmallScientificNumberUsesJavaScientificNotation() {
    assertEquals("1.0E-7", NodeUtil.getStringValue(1e-7));
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
    assertEquals(
        Double.valueOf(100000000000000000000d),
        NodeUtil.getNumberValue(IR.string("100000000000000000000")));
  }

@org.junit.Test
public void testAllResultsMatchFollowsPossibleResultBranches() {
  com.google.common.base.Predicate<com.google.javascript.rhino.Node> isNumber =
      nodeUtilTypePredicateForCoverage(com.google.javascript.rhino.Token.NUMBER);

  com.google.javascript.rhino.Node assignment =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.ASSIGN,
          com.google.javascript.rhino.Node.newString("x"),
          com.google.javascript.rhino.Node.newNumber(1));
  org.junit.Assert.assertTrue(NodeUtil.allResultsMatch(assignment, isNumber));

  com.google.javascript.rhino.Node and =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.AND,
          com.google.javascript.rhino.Node.newNumber(1),
          com.google.javascript.rhino.Node.newString("value"));
  org.junit.Assert.assertFalse(NodeUtil.allResultsMatch(and, isNumber));

  com.google.javascript.rhino.Node hook =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.HOOK,
          com.google.javascript.rhino.Node.newString("condition"),
          com.google.javascript.rhino.Node.newNumber(1),
          com.google.javascript.rhino.Node.newNumber(2));
  org.junit.Assert.assertTrue(NodeUtil.allResultsMatch(hook, isNumber));
}

@org.junit.Test
public void testAnyResultsMatchIgnoresNonResultBranches() {
  com.google.common.base.Predicate<com.google.javascript.rhino.Node> isNumber =
      nodeUtilTypePredicateForCoverage(com.google.javascript.rhino.Token.NUMBER);

  com.google.javascript.rhino.Node comma =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.COMMA,
          com.google.javascript.rhino.Node.newNumber(1),
          com.google.javascript.rhino.Node.newString("value"));
  org.junit.Assert.assertFalse(NodeUtil.anyResultsMatch(comma, isNumber));

  com.google.javascript.rhino.Node or =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.OR,
          com.google.javascript.rhino.Node.newString("left"),
          com.google.javascript.rhino.Node.newNumber(1));
  org.junit.Assert.assertTrue(NodeUtil.anyResultsMatch(or, isNumber));

  com.google.javascript.rhino.Node hook =
      new com.google.javascript.rhino.Node(
          com.google.javascript.rhino.Token.HOOK,
          com.google.javascript.rhino.Node.newNumber(1),
          com.google.javascript.rhino.Node.newString("trueBranch"),
          com.google.javascript.rhino.Node.newString("falseBranch"));
  org.junit.Assert.assertFalse(NodeUtil.anyResultsMatch(hook, isNumber));
}

private static com.google.common.base.Predicate<com.google.javascript.rhino.Node>
    nodeUtilTypePredicateForCoverage(final int type) {
  return new com.google.common.base.Predicate<com.google.javascript.rhino.Node>() {
    @Override
    public boolean apply(com.google.javascript.rhino.Node node) {
      return node.getType() == type;
    }
  };
}
}

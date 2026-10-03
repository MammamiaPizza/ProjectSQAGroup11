package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CodeConsumerNegativeZeroTest {

  private static final class RecordingConsumer extends CodeConsumer {
    private final StringBuilder output = new StringBuilder();

    @Override
    char getLastChar() {
      return output.length() == 0 ? '\0' : output.charAt(output.length() - 1);
    }

    @Override
    void append(String str) {
      output.append(str);
    }

    String getOutput() {
      return output.toString();
    }
  }

  @Test
  public void testIsNegativeZeroRecognizesNegativeZero() {
    assertTrue(CodeConsumer.isNegativeZero(-0.0));
  }

  @Test
  public void testIsNegativeZeroRejectsPositiveZeroAndOtherNumbers() {
    assertFalse(CodeConsumer.isNegativeZero(0.0));
    assertFalse(CodeConsumer.isNegativeZero(-1.0));
    assertFalse(CodeConsumer.isNegativeZero(Double.NaN));
  }

  @Test
  public void testMinusBeforeNegativeZeroIsSeparated() {
    RecordingConsumer consumer = new RecordingConsumer();

    consumer.add("x");
    consumer.addOp("-", true);
    consumer.addNumber(-0.0);

    assertEquals("x- -0.0", consumer.getOutput());
  }

  @Test
  public void testMinusBeforeNegativeNumberIsSeparated() {
    RecordingConsumer consumer = new RecordingConsumer();

    consumer.add("x");
    consumer.addOp("-", true);
    consumer.addNumber(-4.0);

    assertEquals("x- -4", consumer.getOutput());
  }

  @Test
  public void testMinusBeforePositiveZeroDoesNotNeedSeparator() {
    RecordingConsumer consumer = new RecordingConsumer();

    consumer.add("x");
    consumer.addOp("-", true);
    consumer.addNumber(0.0);

    assertEquals("x-0", consumer.getOutput());
  }

  @Test
  public void testNegativeZeroRetainsItsLiteralRepresentation() {
    RecordingConsumer consumer = new RecordingConsumer();

    consumer.addNumber(-0.0);

    assertEquals("-0.0", consumer.getOutput());
  }

@Test
public void testAddIdentifierSeparatesAdjacentWordTokens() {
  assertEquals("return value", outputForIdentifiers("return", "value"));
}

@Test
public void testAddIdentifierIgnoresEmptyToken() {
  assertEquals("value", outputForIdentifiers("value", ""));
}

@Test
public void testAppendBlockDelimiters() {
  final java.lang.StringBuilder output = new java.lang.StringBuilder();
  CodeConsumer consumer = new CodeConsumer() {
    @Override
    char getLastChar() {
      return output.length() == 0 ? '\0' : output.charAt(output.length() - 1);
    }

    @Override
    void append(String str) {
      output.append(str);
    }
  };

  consumer.appendBlockStart();
  consumer.appendBlockEnd();

  assertEquals("{}", output.toString());
}

private static String outputForIdentifiers(String... identifiers) {
  final java.lang.StringBuilder output = new java.lang.StringBuilder();
  CodeConsumer consumer = new CodeConsumer() {
    @Override
    char getLastChar() {
      return output.length() == 0 ? '\0' : output.charAt(output.length() - 1);
    }

    @Override
    void append(String str) {
      output.append(str);
    }
  };

  for (String identifier : identifiers) {
    consumer.addIdentifier(identifier);
  }
  return output.toString();
}
}

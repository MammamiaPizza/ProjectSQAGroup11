package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CodeConsumerTest {

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
  public void testAddNumberPreservesNegativeZeroDecimalLiteral() {
    RecordingConsumer consumer = new RecordingConsumer();

    consumer.addNumber(-0.0);

    assertEquals("-0.0", consumer.getOutput());
  }

  @Test
  public void testAddNumberPrintsPositiveZeroAsIntegerZero() {
    RecordingConsumer consumer = new RecordingConsumer();

    consumer.addNumber(0.0);

    assertEquals("0", consumer.getOutput());
  }

  @Test
  public void testAddNumberPrintsOrdinaryNegativeInteger() {
    RecordingConsumer consumer = new RecordingConsumer();

    consumer.addNumber(-4.0);

    assertEquals("-4", consumer.getOutput());
  }

  @Test
  public void testAddNumberSeparatesNegativeNumberAfterMinusOperator() {
    RecordingConsumer consumer = new RecordingConsumer();

    consumer.add("-");
    consumer.addNumber(-4.0);

    assertEquals("- -4", consumer.getOutput());
  }

  @Test
  public void testAddSeparatesAdjacentWordTokens() {
    RecordingConsumer consumer = new RecordingConsumer();

    consumer.addIdentifier("return");
    consumer.addIdentifier("value");

    assertEquals("return value", consumer.getOutput());
  }

  @Test
  public void testIsWordCharRecognizesOnlyIdentifierAndDigitCharacters() {
    assertTrue(CodeConsumer.isWordChar('_'));
    assertTrue(CodeConsumer.isWordChar('$'));
    assertTrue(CodeConsumer.isWordChar('a'));
    assertTrue(CodeConsumer.isWordChar('7'));
    assertFalse(CodeConsumer.isWordChar('-'));
    assertFalse(CodeConsumer.isWordChar(' '));
  }
}

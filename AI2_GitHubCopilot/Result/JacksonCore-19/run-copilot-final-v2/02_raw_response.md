package com.fasterxml.jackson.core.json;

import static org.junit.Assert.;
import org.junit.Test;
import java.io.;
import java.math.BigDecimal;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.util.BufferRecycler;

/**

 - Tests for bug #19: ArrayIndexOutOfBoundsException on very long floating-point numbers.
 - Validates both ReaderBasedJsonParser and UTF8StreamJsonParser.
  */
 public class TestLongFloatBug19 {
  private static final int LONG_LENGTH = 250;
  private String makeLongFloat(int totalDigits) {
  StringBuilder sb = new StringBuilder(totalDigits + 10);
  sb.append("1");
  for (int i = 1; i < totalDigits - 2; i++) {
      sb.append("0");
  }
  sb.append(".5");
  return sb.toString();
  }
  private String makeLongFloatWithExponent(int totalDigits) {
  StringBuilder sb = new StringBuilder(totalDigits + 10);
  sb.append("1");
  for (int i = 1; i < totalDigits - 2; i++) {
      sb.append("0");
  }
  sb.append("e10");
  return sb.toString();
  }
  private String makeLongIntWithTrailingZeros(int totalDigits) {
  StringBuilder sb = new StringBuilder(totalDigits);
  sb.append("9");
  for (int i = 1; i < totalDigits; i++) {
      sb.append("0");
  }
  return sb.toString();
  }
  @Test
  public void testReaderBasedParserLongFloatNoException() throws Exception {
  String number = makeLongFloat(LONG_LENGTH);
  String json = number;
  ReaderBasedJsonParser parser = createReaderBasedParser(json);
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
  BigDecimal bd = parser.getBigDecimalValue();
  assertNotNull("BigDecimal should not be null", bd);
  assertTrue("Number parsed should be > 1", bd.compareTo(BigDecimal.ONE) > 0);
  parser.close();
  }
  @Test
  public void testUTF8StreamParserLongFloatNoException() throws Exception {
  String number = makeLongFloat(LONG_LENGTH);
  String json = number;
  UTF8StreamJsonParser parser = createUTF8StreamParser(json);
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
  BigDecimal bd = parser.getBigDecimalValue();
  assertNotNull("BigDecimal should not be null", bd);
  assertTrue("Number parsed should be > 1", bd.compareTo(BigDecimal.ONE) > 0);
  parser.close();
  }
  @Test
  public void testReaderBasedParserBoundary200Digits() throws Exception {
  String number = makeLongFloat(200);
  String json = number;
  ReaderBasedJsonParser parser = createReaderBasedParser(json);
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
  BigDecimal bd = parser.getBigDecimalValue();
  assertNotNull(bd);
  parser.close();
  }
  @Test
  public void testUTF8StreamParserBoundary200Digits() throws Exception {
  String number = makeLongFloat(200);
  String json = number;
  UTF8StreamJsonParser parser = createUTF8StreamParser(json);
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
  BigDecimal bd = parser.getBigDecimalValue();
  assertNotNull(bd);
  parser.close();
  }
  @Test
  public void testReaderBasedParserTrailingZerosLong() throws Exception {
  String number = makeLongIntWithTrailingZeros(250);
  String json = number + ".0";
  ReaderBasedJsonParser parser = createReaderBasedParser(json);
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
  BigDecimal bd = parser.getBigDecimalValue();
  assertNotNull(bd);
  assertTrue(bd.compareTo(BigDecimal.ZERO) > 0);
  parser.close();
  }
  @Test
  public void testUTF8StreamParserTrailingZerosLong() throws Exception {
  String number = makeLongIntWithTrailingZeros(250);
  String json = number + ".0";
  UTF8StreamJsonParser parser = createUTF8StreamParser(json);
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
  BigDecimal bd = parser.getBigDecimalValue();
  assertNotNull(bd);
  assertTrue(bd.compareTo(BigDecimal.ZERO) > 0);
  parser.close();
  }
  @Test
  public void testReaderBasedParserScientificLong() throws Exception {
  String number = makeLongFloatWithExponent(250);
  String json = number;
  ReaderBasedJsonParser parser = createReaderBasedParser(json);
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
  BigDecimal bd = parser.getBigDecimalValue();
  assertNotNull(bd);
  assertTrue(bd.compareTo(BigDecimal.ONE) > 0);
  parser.close();
  }
  @Test
  public void testUTF8StreamParserScientificLong() throws Exception {
  String number = makeLongFloatWithExponent(250);
  String json = number;
  UTF8StreamJsonParser parser = createUTF8StreamParser(json);
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
  BigDecimal bd = parser.getBigDecimalValue();
  assertNotNull(bd);
  assertTrue(bd.compareTo(BigDecimal.ONE) > 0);
  parser.close();
  }
  @Test
  public void testReaderBasedParserNegativeLongFloat() throws Exception {
  String number = "-" + makeLongFloat(250);
  String json = number;
  ReaderBasedJsonParser parser = createReaderBasedParser(json);
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
  BigDecimal bd = parser.getBigDecimalValue();
  assertNotNull(bd);
  assertTrue(bd.compareTo(BigDecimal.ZERO) < 0);
  parser.close();
  }
  @Test
  public void testUTF8StreamParserNegativeLongFloat() throws Exception {
  String number = "-" + makeLongFloat(250);
  String json = number;
  UTF8StreamJsonParser parser = createUTF8StreamParser(json);
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
  BigDecimal bd = parser.getBigDecimalValue();
  assertNotNull(bd);
  assertTrue(bd.compareTo(BigDecimal.ZERO) < 0);
  parser.close();
  }
  @Test
  public void testReaderBasedParserMultipleLongNumbers() throws Exception {
  String longNum = makeLongFloat(250);
  String json = String.format("[%s, %s, %s]", longNum, longNum, longNum);
  ReaderBasedJsonParser parser = createReaderBasedParser(json);
  assertToken(JsonToken.START_ARRAY, parser.nextToken());
  for (int i = 0; i < 3; i++) {
      assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
      assertNotNull(parser.getBigDecimalValue());
  }
  assertToken(JsonToken.END_ARRAY, parser.nextToken());
  parser.close();
  }
  @Test
  public void testUTF8StreamParserMultipleLongNumbers() throws Exception {
  String longNum = makeLongFloat(250);
  String json = String.format("[%s, %s, %s]", longNum, longNum, longNum);
  UTF8StreamJsonParser parser = createUTF8StreamParser(json);
  assertToken(JsonToken.START_ARRAY, parser.nextToken());
  for (int i = 0; i < 3; i++) {
      assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
      assertNotNull(parser.getBigDecimalValue());
  }
  assertToken(JsonToken.END_ARRAY, parser.nextToken());
  parser.close();
  }
  // --- Helpers ---
  private ReaderBasedJsonParser createReaderBasedParser(String content) throws IOException {
  Reader reader = new StringReader(content);
  IOContext ioContext = new IOContext(new BufferRecycler(), null, false);
  CharsToNameCanonicalizer symbols = CharsToNameCanonicalizer.createRoot();
  return new ReaderBasedJsonParser(ioContext, 0, reader, null, symbols);
  }
  private UTF8StreamJsonParser createUTF8StreamParser(String content) throws IOException {
  byte[] bytes = content.getBytes("UTF-8");
  return new UTF8StreamJsonParser(
          new IOContext(new BufferRecycler(), null, false),
          0,
          new ByteArrayInputStream(bytes),
          null,
          ByteQuadsCanonicalizer.createRoot(),
          new byte[0], 0, 0, // dummy empty buffer, real data comes from stream
          false
  );
  }
  private void assertToken(JsonToken expected, JsonToken actual) {
  assertEquals("Token mismatch", expected, actual);
  }

}
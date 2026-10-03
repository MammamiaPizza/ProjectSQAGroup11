package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CSVFormatQuotingTest {

 @Test
 public void testEuroFirstCharNoQuote() {
     assertEquals("€test", CSVFormat.RFC4180.format("€test"));
 }

 @Test
 public void testEuroFirstCharMultiValue() {
     assertEquals("€,Deux", CSVFormat.RFC4180.format("€", "Deux"));
 }

 @Test
 public void testDelimiterFirstCharQuoted() {
     assertEquals("\",test\"", CSVFormat.RFC4180.format(",test"));
 }

 @Test
 public void testQuoteCharFirstCharQuoted() {
     assertEquals("\"\"\"\"", CSVFormat.RFC4180.format("\""));
 }

 @Test
 public void testEmptyValue() {
     assertEquals("\"\"", CSVFormat.RFC4180.format(""));
 }

 @Test
 public void testNullValue() {
     assertEquals("", CSVFormat.RFC4180.format((Object) null));
 }

 @Test
 public void testSingleSpecialCharDelimiter() {
     assertEquals("\",\"", CSVFormat.RFC4180.format(","));
 }

 @Test
 public void testNormalValueNoQuote() {
     assertEquals("abc", CSVFormat.RFC4180.format("abc"));
 }

 @Test
 public void testMixedSpecialAndNonSpecialFirstChar() {
     assertEquals("€,\",b\",c", CSVFormat.RFC4180.format("€", ",b", "c"));
 }

 @Test
 public void testEuroWithQuoteModeAll() {
     assertEquals("\"€\"", CSVFormat.RFC4180.withQuoteMode(QuoteMode.ALL).format("€"));
 }

 @Test
 public void testValueWithEscapeCharNoQuote() {
     CSVFormat custom = CSVFormat.RFC4180.withEscape('\\');
     assertEquals("\\test", custom.format("\\test"));
 }

 @Test
 public void testEuroFirstCharCustomDelimiterQuoted() {
     CSVFormat custom = CSVFormat.newFormat(';').withQuote('"');
     assertEquals("\"€;test\"", custom.format("€;test"));
 }

}

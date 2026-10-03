package org.jsoup.select;

import org.junit.Test;
import static org.junit.Assert.*;
import org.jsoup.select.Selector;
import org.jsoup.select.Selector.SelectorParseException;

public class QueryParserTest {

 @Test
 public void testParsesSingleQuoteInContains() {
     try {
         QueryParser.parse(":contains(')");
         fail("Expected SelectorParseException");
     } catch (SelectorParseException expected) {
         // expected
     }
 }

 @Test(expected = SelectorParseException.class)
 public void exceptionOnUncloseAttribute() {
     QueryParser.parse("[attr=value");
 }

 @Test
 public void testContainsTextNormal() {
     assertNotNull(QueryParser.parse(":contains(foo)"));
 }

 @Test
 public void testAttributeKeyOnly() {
     assertNotNull(QueryParser.parse("[attr]"));
 }

 @Test
 public void testAttributeEqualsValue() {
     assertNotNull(QueryParser.parse("[attr=val]"));
 }

 @Test
 public void testAttributeSingleQuotedValue() {
     assertNotNull(QueryParser.parse("[attr='val']"));
 }

 @Test
 public void testAttributeDoubleQuotedValue() {
     assertNotNull(QueryParser.parse("[attr=\"val\"]"));
 }

 @Test
 public void testAttributeEmptyValue() {
     assertNotNull(QueryParser.parse("[attr='']"));
 }

 @Test(expected = SelectorParseException.class)
 public void testContainsUnclosedQuote() {
     QueryParser.parse(":contains('text)");
 }

 @Test(expected = SelectorParseException.class)
 public void testContainsWithSingleQuoteInside() {
     QueryParser.parse(":contains('text')");
 }

 @Test
 public void testContainsEmpty() {
     assertNotNull(QueryParser.parse(":contains()"));
 }

 @Test(expected = SelectorParseException.class)
 public void testEmptyAttributeBrackets() {
     QueryParser.parse("[]");
 }

}

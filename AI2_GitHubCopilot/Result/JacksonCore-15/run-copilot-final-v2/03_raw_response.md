package com.fasterxml.jackson.core.filter;

 import com.fasterxml.jackson.core.*;
 import org.junit.Before;
 import org.junit.Test;

 import java.io.IOException;

 import static org.junit.Assert.*;

 public class FilteringParserDelegateTest {

     private JsonFactory factory;

     @Before
     public void setUp() {
         factory = new JsonFactory();
     }

     static class SingleMatchFilter extends TokenFilter {
         boolean matched = false;

         @Override
         public TokenFilter includeProperty(String name) {
             if ("value".equals(name) && !matched) {
                 matched = true;
                 return TokenFilter.INCLUDE_ALL;
             }
             return null;
         }

         @Override
         public boolean includeValue(JsonParser p) throws IOException {
             return p.isExpectedStartArrayToken();
         }
     }

     static class ArrayValueFilter extends TokenFilter {
         @Override
         public boolean includeValue(JsonParser p) throws IOException {
             return p.isExpectedStartArrayToken();
         }

         @Override
         public TokenFilter includeProperty(String name) {
             return TokenFilter.INCLUDE_ALL;
         }
     }

     @Test
     public void testNotAllowMultipleMatchesSingleMatchInArray() throws IOException {
         String json = "[{\"value\":3},{\"value\":4}]";
         JsonParser p = factory.createParser(json);
         FilteringParserDelegate parser = new FilteringParserDelegate(p, new SingleMatchFilter(),
 false, false);

         assertEquals(JsonToken.START_ARRAY, parser.nextToken());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("value", parser.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
         assertEquals(3, parser.getIntValue());
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());
         assertEquals(JsonToken.END_ARRAY, parser.nextToken());
         assertNull(parser.nextToken());

         assertEquals(1, parser.getMatchCount());
         p.close();
     }

     @Test
     public void testAllowMultipleMatchesBothValuesReturned() throws IOException {
         String json = "[{\"value\":3},{\"value\":4}]";
         JsonParser p = factory.createParser(json);
         FilteringParserDelegate parser = new FilteringParserDelegate(p, new SingleMatchFilter(),
 false, true);

         assertEquals(JsonToken.START_ARRAY, parser.nextToken());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("value", parser.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
         assertEquals(3, parser.getIntValue());
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("value", parser.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
         assertEquals(4, parser.getIntValue());
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());
         assertEquals(JsonToken.END_ARRAY, parser.nextToken());
         assertNull(parser.nextToken());

         assertEquals(2, parser.getMatchCount());
         p.close();
     }

     @Test
     public void testNotAllowMultipleMatchesStopAfterFirstMatch() throws IOException {
         String json = "{\"a\":1,\"b\":2,\"c\":3}";
         JsonFactory f = new JsonFactory();
         JsonParser p = f.createParser(json);

         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 return this;
             }

             @Override
             public boolean includeValue(JsonParser p) throws IOException {
                 return p.getIntValue() == 1 || p.getIntValue() == 2;
             }
         };

         FilteringParserDelegate parser = new FilteringParserDelegate(p, filter, false, false);
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("a", parser.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
         assertEquals(1, parser.getIntValue());
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());
         assertNull(parser.nextToken());

         assertEquals(1, parser.getMatchCount());
         p.close();
     }

     @Test
     public void testNotAllowMultipleMatchesNoMatchReturnsNothing() throws IOException {
         String json = "{\"x\":99}";
         JsonParser p = factory.createParser(json);

         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 return "y".equals(name) ? TokenFilter.INCLUDE_ALL : null;
             }
         };

         FilteringParserDelegate parser = new FilteringParserDelegate(p, filter, false, false);
         assertNull(parser.nextToken());

         assertEquals(0, parser.getMatchCount());
         p.close();
     }

     @Test
     public void testIncludePathWithNotAllowMultipleMatches() throws IOException {
         String json = "{\"wrapper\":{\"value\":42,\"other\":99}}";
         JsonParser p = factory.createParser(json);

         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 if ("wrapper".equals(name)) return this;
                 if ("value".equals(name)) return TokenFilter.INCLUDE_ALL;
                 return null;
             }
         };

         FilteringParserDelegate parser = new FilteringParserDelegate(p, filter, true, false);
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("wrapper", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("value", parser.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
         assertEquals(42, parser.getIntValue());
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());
         assertNull(parser.nextToken());

         assertEquals(1, parser.getMatchCount());
         p.close();
     }

     @Test
     public void testEmptyArrayWithNotAllowMultipleMatches() throws IOException {
         String json = "[]";
         JsonParser p = factory.createParser(json);
         FilteringParserDelegate parser = new FilteringParserDelegate(p, new ArrayValueFilter(),
 false, false);

         assertEquals(JsonToken.START_ARRAY, parser.nextToken());
         assertEquals(JsonToken.END_ARRAY, parser.nextToken());
         assertNull(parser.nextToken());

         assertEquals(0, parser.getMatchCount());
         p.close();
     }

     @Test
     public void testNestedArraysNotAllowMultipleMatches() throws IOException {
         String json = "[[1,2],[3,4]]";
         JsonParser p = factory.createParser(json);

         TokenFilter filter = new TokenFilter() {
             @Override
             public boolean includeValue(JsonParser p) throws IOException {
                 if (p.isExpectedStartArrayToken()) {
                     return true;
                 }
                 if (p.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
                     int val = p.getIntValue();
                     return val == 1 || val == 3;
                 }
                 return false;
             }

             @Override
             public TokenFilter filterStartArray() {
                 return this;
             }
         };

         FilteringParserDelegate parser = new FilteringParserDelegate(p, filter, false, false);
         assertEquals(JsonToken.START_ARRAY, parser.nextToken());
         assertEquals(JsonToken.START_ARRAY, parser.nextToken());
         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
         assertEquals(1, parser.getIntValue());
         assertEquals(JsonToken.END_ARRAY, parser.nextToken());
         assertEquals(JsonToken.END_ARRAY, parser.nextToken());
         assertNull(parser.nextToken());

         assertEquals(1, parser.getMatchCount());
         p.close();
     }

     @Test
     public void testMatchCountResetOnFailedSecondMatch() throws IOException {
         String json = "[{\"value\":10,\"skip\":20},{\"value\":30}]";
         JsonParser p = factory.createParser(json);

         final int[] matchCalls = {0};
         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 return "value".equals(name) ? TokenFilter.INCLUDE_ALL : null;
             }

             @Override
             public boolean includeValue(JsonParser p) throws IOException {
                 matchCalls[0]++;
                 return p.getIntValue() == 10;
             }
         };

         FilteringParserDelegate parser = new FilteringParserDelegate(p, filter, false, false);
         assertEquals(JsonToken.START_ARRAY, parser.nextToken());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("value", parser.getCurrentName());
         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
         assertEquals(10, parser.getIntValue());
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());
         assertEquals(JsonToken.END_ARRAY, parser.nextToken());
         assertNull(parser.nextToken());

         assertEquals(1, parser.getMatchCount());
         p.close();
     }

     @Test
     public void testGetCurrentTokenStateAcrossParsing() throws IOException {
         String json = "[1,2,3]";
         JsonParser p = factory.createParser(json);

         TokenFilter filter = new TokenFilter() {
             @Override
             public boolean includeValue(JsonParser p) throws IOException {
                 int val = p.getIntValue();
                 return val == 1 || val == 3;
             }
         };

         FilteringParserDelegate parser = new FilteringParserDelegate(p, filter, false, true);

         assertNull(parser.getCurrentToken());
         assertEquals(0, parser.getMatchCount());

         assertEquals(JsonToken.START_ARRAY, parser.nextToken());
         assertEquals(JsonToken.START_ARRAY, parser.getCurrentToken());

         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
         assertEquals(1, parser.getIntValue());
         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.getCurrentToken());
         assertEquals(1, parser.getMatchCount());

         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
         assertEquals(3, parser.getIntValue());
         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.getCurrentToken());
         assertEquals(2, parser.getMatchCount());

         assertEquals(JsonToken.END_ARRAY, parser.nextToken());
         assertEquals(JsonToken.END_ARRAY, parser.getCurrentToken());

         assertNull(parser.nextToken());
         assertNull(parser.getCurrentToken());

         p.close();
     }

     @Test
     public void testExposedContextClearedWhenNotAllowMultipleMatches() throws IOException {
         String json = "{\"first\":{\"inner\":\"yes\"},\"second\":{\"inner\":\"no\"}}";
         JsonParser p = factory.createParser(json);

         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 if ("first".equals(name)) return this;
                 return null;
             }

             @Override
             public TokenFilter includeProperty(String name, int idx) {
                 if ("inner".equals(name)) return TokenFilter.INCLUDE_ALL;
                 return null;
             }
         };

         FilteringParserDelegate parser = new FilteringParserDelegate(p, filter, false, false);
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("first", parser.getCurrentName());
         assertEquals(JsonToken.START_OBJECT, parser.nextToken());
         assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
         assertEquals("inner", parser.getCurrentName());
         assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
         assertEquals("yes", parser.getText());
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());
         assertEquals(JsonToken.END_OBJECT, parser.nextToken());
         assertNull(parser.nextToken());

         assertEquals(1, parser.getMatchCount());
         p.close();
     }

     @Test
     public void testScalarArrayValuesWithNotAllowMultipleMatches() throws IOException {
         String json = "[10,20,30]";
         JsonParser p = factory.createParser(json);

         TokenFilter filter = new TokenFilter() {
             @Override
             public boolean includeValue(JsonParser p) throws IOException {
                 return p.getIntValue() <= 20;
             }
         };

         FilteringParserDelegate parser = new FilteringParserDelegate(p, filter, false, false);
         assertEquals(JsonToken.START_ARRAY, parser.nextToken());
         assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
         assertEquals(10, parser.getIntValue());
         assertEquals(JsonToken.END_ARRAY, parser.nextToken());
         assertNull(parser.nextToken());

         assertEquals(1, parser.getMatchCount());
         p.close();
     }
 }
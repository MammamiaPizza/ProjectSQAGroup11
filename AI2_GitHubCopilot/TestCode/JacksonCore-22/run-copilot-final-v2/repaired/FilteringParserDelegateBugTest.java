package com.fasterxml.jackson.core.filter;

 import static org.junit.Assert.assertEquals;

 import java.io.IOException;

 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonFactory;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.TokenFilter;

 public class FilteringParserDelegateBugTest {

     // Helper to create FilteringParserDelegate from a JSON string
     private FilteringParserDelegate createParser(String json, TokenFilter filter,
             boolean includePath, boolean allowMultipleMatches) throws IOException {
         JsonFactory factory = new JsonFactory();
         JsonParser parser = factory.createParser(json);
         return new FilteringParserDelegate(parser, filter, allowMultipleMatches, includePath);
     }

     // Consume all tokens and return the match count
     private int countMatches(FilteringParserDelegate parser) throws IOException {
         while (parser.nextToken() != null) {
             // just consume tokens
         }
         return parser.getMatchCount();
     }

     // ---- Custom TokenFilter implementations ----

     /** Filters a single property by name */
     static class SinglePropertyFilter extends TokenFilter {
         private final String name;

         SinglePropertyFilter(String name) {
             this.name = name;
         }

         @Override
         public TokenFilter includeProperty(String name) {
             if (this.name.equals(name)) {
                 return TokenFilter.INCLUDE_ALL;
             }
             return null;
         }

         @Override
         public TokenFilter filterStartObject() {
             return this;
         }

         @Override
         public TokenFilter filterStartArray() {
             return this;
         }
     }

     /** Filters several properties by name */
     static class MultiPropertyFilter extends TokenFilter {
         private final String[] names;

         MultiPropertyFilter(String... names) {
             this.names = names;
         }

         @Override
         public TokenFilter includeProperty(String name) {
             for (String n : names) {
                 if (n.equals(name)) {
                     return TokenFilter.INCLUDE_ALL;
                 }
             }
             return null;
         }

         @Override
         public TokenFilter filterStartObject() {
             return this;
         }

         @Override
         public TokenFilter filterStartArray() {
             return this;
         }
     }

     /** Filters array elements by index */
     static class IndexFilter extends TokenFilter {
         private final int targetIndex;

         IndexFilter(int idx) {
             this.targetIndex = idx;
         }

         @Override
         public TokenFilter includeIndex(int index) {
             if (index == targetIndex) {
                 return TokenFilter.INCLUDE_ALL;
             }
             return null;
         }

         @Override
         public TokenFilter filterStartArray() {
             return this;
         }

         @Override
         public TokenFilter filterStartObject() {
             return this;
         }
     }

     /** Matches every array element (INCLUDE_ALL for any index) */
     static class ArrayAllFilter extends TokenFilter {
         @Override
         public TokenFilter includeIndex(int index) {
             return TokenFilter.INCLUDE_ALL;
         }

         @Override
         public TokenFilter filterStartArray() {
             return this;
         }

         @Override
         public TokenFilter filterStartObject() {
             return this;
         }
     }

     // ---- Test methods ----

     @Test
     public void testSingleMatchFilteringWithPath() throws IOException {
         FilteringParserDelegate parser = createParser("{\"a\":1}",
                 new SinglePropertyFilter("a"), true, false);
         assertEquals("Expected one match for single property with path", 1, countMatches(parser));
     }

     @Test
     public void testAllowMultipleMatchesWithPath1() throws IOException {
         // Array with three elements, all matched, includePath=true, allowMultipleMatches=true
         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 if ("arr".equals(name)) {
                     return new ArrayAllFilter();
                 }
                 return null;
             }

             @Override
             public TokenFilter filterStartObject() {
                 return this;
             }

             @Override
             public TokenFilter filterStartArray() {
                 return this;
             }
         };
         FilteringParserDelegate parser = createParser("{\"arr\":[1,2,3]}", filter, true, true);
         assertEquals("Expected three matches for three array elements", 3, countMatches(parser));
     }

     @Test
     public void testAllowMultipleMatchesWithPath2() throws IOException {
         // Object with two matching properties, includePath=true, allowMultipleMatches=true
         FilteringParserDelegate parser = createParser("{\"a\":1,\"b\":2}",
                 new MultiPropertyFilter("a", "b"), true, true);
         assertEquals("Expected two matches for two properties", 2, countMatches(parser));
     }

     @Test
     public void testMultipleMatchFilteringWithPath1() throws IOException {
         // Array with two elements, includePath=true, allowMultipleMatches=false - still both
included via path buffering
         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 if ("x".equals(name)) {
                     return new ArrayAllFilter();
                 }
                 return null;
             }

             @Override
             public TokenFilter filterStartObject() {
                 return this;
             }

             @Override
             public TokenFilter filterStartArray() {
                 return this;
             }
         };
         FilteringParserDelegate parser = createParser("{\"x\":[10,20]}", filter, true, false);
         assertEquals(2, countMatches(parser));
     }

     @Test
     public void testMultipleMatchFilteringWithPath2() throws IOException {
         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 if ("y".equals(name)) {
                     return new ArrayAllFilter();
                 }
                 return null;
             }

             @Override
             public TokenFilter filterStartObject() {
                 return this;
             }

             @Override
             public TokenFilter filterStartArray() {
                 return this;
             }
         };
         FilteringParserDelegate parser = createParser("{\"y\":[100,200]}", filter, true, false);
         assertEquals(2, countMatches(parser));
     }

     @Test
     public void testMultipleMatchFilteringWithPath3() throws IOException {
         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 if ("z".equals(name)) {
                     return new ArrayAllFilter();
                 }
                 return null;
             }

             @Override
             public TokenFilter filterStartObject() {
                 return this;
             }

             @Override
             public TokenFilter filterStartArray() {
                 return this;
             }
         };
         FilteringParserDelegate parser = createParser("{\"z\":[5,6]}", filter, true, false);
         assertEquals(2, countMatches(parser));
     }

     @Test
     public void testSingleMatchFilteringWithoutPath() throws IOException {
         FilteringParserDelegate parser = createParser("{\"a\":1}",
                 new SinglePropertyFilter("a"), false, false);
         assertEquals(1, countMatches(parser));
     }

     @Test
     public void testNotAllowMultipleMatchesWithoutPath1() throws IOException {
         // Multiple potential matches but allowMultipleMatches=false - only first is counted
         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 if ("arr".equals(name)) {
                     return new ArrayAllFilter();
                 }
                 return null;
             }

             @Override
             public TokenFilter filterStartObject() {
                 return this;
             }

             @Override
             public TokenFilter filterStartArray() {
                 return this;
             }
         };
         FilteringParserDelegate parser = createParser("{\"arr\":[1,2,3]}", filter, false, false);
         assertEquals(1, countMatches(parser));
     }

     @Test
     public void testNotAllowMultipleMatchesWithoutPath2() throws IOException {
         FilteringParserDelegate parser = createParser("{\"a\":1,\"b\":2}",
                 new MultiPropertyFilter("a", "b"), false, false);
         assertEquals(1, countMatches(parser));
     }

     @Test
     public void testIndexMatchWithPath1() throws IOException {
         // Match only element at index 1 in a top-level array
         FilteringParserDelegate parser = createParser("[10,20,30]", new IndexFilter(1), true,
false);
         assertEquals(1, countMatches(parser));
     }

     @Test
     public void testIndexMatchWithPath2() throws IOException {
         // Nested: {"data":[1,2,3]} - match indices 1 and 2 under "data"
         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeProperty(String name) {
                 if ("data".equals(name)) {
                     return new TokenFilter() {
                         @Override
                         public TokenFilter includeIndex(int index) {
                             if (index == 1 || index == 2) {
                                 return TokenFilter.INCLUDE_ALL;
                             }
                             return null;
                         }

                         @Override
                         public TokenFilter filterStartArray() {
                             return this;
                         }

                         @Override
                         public TokenFilter filterStartObject() {
                             return this;
                         }
                     };
                 }
                 return null;
             }

             @Override
             public TokenFilter filterStartObject() {
                 return this;
             }

             @Override
             public TokenFilter filterStartArray() {
                 return this;
             }
         };
         FilteringParserDelegate parser = createParser("{\"data\":[1,2,3]}", filter, true, false);
         assertEquals(2, countMatches(parser));
     }

     @Test
     public void testAllowMultipleMatchesWithoutPath() throws IOException {
         // Array with three elements, includePath=false, allowMultipleMatches=true
         TokenFilter filter = new TokenFilter() {
             @Override
             public TokenFilter includeIndex(int index) {
                 return TokenFilter.INCLUDE_ALL; // match every element
             }

             @Override
             public TokenFilter filterStartArray() {
                 return this;
             }

             @Override
             public TokenFilter filterStartObject() {
                 return this;
             }
         };
         FilteringParserDelegate parser = createParser("[1,2,3]", filter, false, true);
         assertEquals(3, countMatches(parser));
     }
 }

package org.apache.commons.lang;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;

 import java.io.IOException;
 import java.io.StringWriter;

 import org.junit.Test;

 public class StringEscapeUtilsBug52Test {

     @Test
     public void testEscapeJavaScriptEscapesClosingTag() {
         // Bug: escapeJavaScript did not escape forward slash before </script>
         // Fixed: must produce <\/ to avoid prematurely closing HTML script tag
         assertEquals("foo<\\/script>bar",
                 StringEscapeUtils.escapeJavaScript("foo</script>bar"));
     }

     @Test
     public void testEscapeJavaScriptMultipleClosingTags() {
         assertEquals("a<\\/b<\\/c",
                 StringEscapeUtils.escapeJavaScript("a</b</c"));
     }

     @Test
     public void testEscapeJavaScriptUnrelatedSlashIsNotEscaped() {
         // All forward slashes are escaped by the current implementation
         assertEquals("a\\/b",
                 StringEscapeUtils.escapeJavaScript("a/b"));
     }

     @Test
     public void testEscapeJavaScriptEmptyString() {
         assertEquals("", StringEscapeUtils.escapeJavaScript(""));
     }

     @Test
     public void testEscapeJavaScriptNullReturnsNull() {
         assertNull(StringEscapeUtils.escapeJavaScript(null));
     }

     @Test
     public void testEscapeJavaScriptNoSpecialChars() {
         assertEquals("hello",
                 StringEscapeUtils.escapeJavaScript("hello"));
     }

     @Test
     public void testEscapeJavaScriptEscapesSingleQuotes() {
         assertEquals("it\\' s",
                 StringEscapeUtils.escapeJavaScript("it's"));
     }

     @Test
     public void testEscapeJavaDoesNotEscapeSlash() {
         // The fix also affects escapeJava, which now escapes forward slashes
         assertEquals("a<\\/b",
                 StringEscapeUtils.escapeJava("a</b"));
     }

     @Test
     public void testEscapeJavaScriptWriterEscapesClosingTag() throws IOException {
         StringWriter writer = new StringWriter();
         StringEscapeUtils.escapeJavaScript(writer, "foo</script>bar");
         assertEquals("foo<\\/script>bar", writer.toString());
     }

     @Test
     public void testEscapeJavaScriptWithConsecutiveClosingTags() {
         assertEquals("a<\\/<\\/b",
                 StringEscapeUtils.escapeJavaScript("a</</b"));
     }

     @Test
     public void testEscapeJavaScriptWithAngleBracketButNoSlash() {
         // Angle bracket without following slash should stay unchanged
         assertEquals("a<b",
                 StringEscapeUtils.escapeJavaScript("a<b"));
     }
 }
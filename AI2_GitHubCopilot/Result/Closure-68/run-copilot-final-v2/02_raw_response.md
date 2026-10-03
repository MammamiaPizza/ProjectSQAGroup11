import com.google.javascript.jscomp.mozilla.rhino.ErrorReporter;
 import com.google.javascript.jscomp.parsing.Config;
 import com.google.javascript.jscomp.parsing.JsDocInfoParser;
 import com.google.javascript.jscomp.parsing.JsDocTokenStream;
 import com.google.javascript.rhino.Node;
 import java.util.ArrayList;
 import java.util.List;
 import junit.framework.TestCase;

 /**
  * Tests that malformed type strings no longer produce an
  * "Unexpected end of file" warning after the fix for issue 477.
  */
 public class JsDocInfoParserTest extends TestCase {

   // A simple error reporter that captures all warnings.
   private static final class CapturingErrorReporter implements ErrorReporter {
     private final List<String> warnings = new ArrayList<String>();

     @Override
     public void warning(String message, String sourceName, int line,
                         String lineSource, int lineOffset) {
       warnings.add(message);
     }

     @Override
     public void error(String message, String sourceName, int line,
                       String lineSource, int lineOffset) {
       // not needed for this test
     }

     @Override
     public ErrorReporter runtimeError(String message, String sourceName,
                                       int line, String lineSource,
                                       int lineOffset) {
       return this;
     }

     public List<String> getWarnings() {
       return warnings;
     }
   }

   /**
    * Creates a parser for a JSDoc comment and parses it, returning the
    * captured warning messages.
    */
   private List<String> parseCommentAndGetWarnings(String comment) throws Exception {
     JsDocTokenStream stream = new JsDocTokenStream(comment);
     Config config = new Config(
         new java.util.HashSet<String>(),
         new java.util.HashSet<String>(),
         false,
         Config.LanguageMode.ECMASCRIPT3,
         false);
     CapturingErrorReporter reporter = new CapturingErrorReporter();
     JsDocInfoParser parser = new JsDocInfoParser(stream, null, "test", config, reporter);
     parser.parse();
     return reporter.getWarnings();
   }

   private void assertNoUnexpectedEofWarning(List<String> warnings) {
     for (String w : warnings) {
       if (w.contains("Unexpected end of file")) {
         fail("Found unexpected 'Unexpected end of file' warning: " + w);
       }
     }
   }

   public void testValidTypeDoesNotProduceWarning() throws Exception {
     List<String> warnings = parseCommentAndGetWarnings("/** @type {number} */");
     assertNoUnexpectedEofWarning(warnings);
     assertTrue("There should be no warnings for a valid type", warnings.isEmpty());
   }

   public void testMalformedFunctionType() throws Exception {
     List<String> warnings = parseCommentAndGetWarnings("/** @type {function(} */");
     assertNoUnexpectedEofWarning(warnings);
   }

   public void testMalformedRecordType() throws Exception {
     List<String> warnings = parseCommentAndGetWarnings("/** @type {{prop:}} */");
     assertNoUnexpectedEofWarning(warnings);
   }

   public void testMalformedArrayType() throws Exception {
     List<String> warnings = parseCommentAndGetWarnings("/** @type {Array<} */");
     assertNoUnexpectedEofWarning(warnings);
   }

   public void testTrailingPipeInUnionType() throws Exception {
     List<String> warnings = parseCommentAndGetWarnings("/** @type {number|} */");
     assertNoUnexpectedEofWarning(warnings);
   }

   public void testEmptyType() throws Exception {
     // An empty type string is syntactically invalid but must not produce an EOF warning.
     List<String> warnings = parseCommentAndGetWarnings("/** @type {} */");
     assertNoUnexpectedEofWarning(warnings);
   }

   public void testMultipleMalformedTypesInOneComment() throws Exception {
     List<String> warnings = parseCommentAndGetWarnings(
         "/** @type {Array<} @param {function(} @return {{prop:}} */");
     assertNoUnexpectedEofWarning(warnings);
   }

   public void testParseTypeStringStaticMethod() throws Exception {
     // The static helper should never throw; it returns a Node (or null) and swallows warnings.
     String[] inputs = { "function(", "{prop:", "Array<", "|", "" };
     for (String s : inputs) {
       try {
         Node n = JsDocInfoParser.parseTypeString(s);
         // simply ensure no exception
       } catch (Exception e) {
         fail("parseTypeString threw for " + s + ": " + e.getMessage());
       }
     }
   }

   public void testEmptyComment() throws Exception {
     List<String> warnings = parseCommentAndGetWarnings("/** */");
     assertNoUnexpectedEofWarning(warnings);
   }

   public void testCommentWithOnlyAnnotation() throws Exception {
     List<String> warnings = parseCommentAndGetWarnings("/** @type */");
     assertNoUnexpectedEofWarning(warnings);
   }

   public void testValidComplexUnionType() throws Exception {
     List<String> warnings = parseCommentAndGetWarnings("/** @type {number|string|null} */");
     assertNoUnexpectedEofWarning(warnings);
     // Complex but valid types should produce no warnings at all.
     assertTrue("Expected no warnings for valid complex type", warnings.isEmpty());
   }
 }
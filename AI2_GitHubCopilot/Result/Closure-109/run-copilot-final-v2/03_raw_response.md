package com.google.javascript.jscomp.parsing;

 import com.google.common.collect.Sets;
 import com.google.javascript.jscomp.parsing.Config.LanguageMode;
 import com.google.javascript.rhino.head.ErrorReporter;
 import com.google.javascript.rhino.head.EvaluatorException;

 import junit.framework.TestCase;

 import java.util.ArrayList;
 import java.util.List;

 public class JsDocInfoParserTest extends TestCase {

  private static final Config CONFIG = new Config(
          Sets.<String>newHashSet(),
          Sets.<String>newHashSet(),
          false,
          LanguageMode.ECMASCRIPT3,
          false);

  private static class RecordingErrorReporter implements ErrorReporter {
      private final List<String> warnings = new ArrayList<String>();
      private final List<String> errors = new ArrayList<String>();

      @Override
      public void warning(String message, String sourceName, int line,
                          String lineSource, int lineOffset) {
          warnings.add(message);
      }

      @Override
      public void error(String message, String sourceName, int line,
                        String lineSource, int lineOffset) {
          errors.add(message);
      }

      @Override
      public EvaluatorException runtimeError(String message, String sourceName,
                                             int line, String lineSource,
                                             int lineOffset) {
          return new EvaluatorException(null, null);
      }

      public List<String> getWarnings() {
          return warnings;
      }

      public List<String> getErrors() {
          return errors;
      }

      public int getWarningCount() {
          return warnings.size();
      }

      public int getErrorCount() {
          return errors.size();
      }
  }

  private JsDocInfoParser createParser(String typeString, RecordingErrorReporter reporter) {
      JsDocTokenStream stream = new JsDocTokenStream(typeString);
      return new JsDocInfoParser(stream, null, null, CONFIG, reporter);
  }

  private void assertNoWarnings(String typeString) {
      RecordingErrorReporter reporter = new RecordingErrorReporter();
      JsDocInfoParser parser = createParser(typeString, reporter);
      parser.parseTopLevelTypeExpression(parser.next());
      assertEquals("Warnings produced for: " + typeString,
              0, reporter.getWarningCount());
      assertEquals("Errors produced for: " + typeString,
              0, reporter.getErrorCount());
  }

  private void assertWarningCount(String typeString, int expectedWarnings) {
      RecordingErrorReporter reporter = new RecordingErrorReporter();
      JsDocInfoParser parser = createParser(typeString, reporter);
      parser.parseTopLevelTypeExpression(parser.next());
      assertEquals("Unexpected number of warnings for: " + typeString,
              expectedWarnings, reporter.getWarningCount());
  }

  public void testFunctionNewTakesSimpleType() {
      assertNoWarnings("function(new:Object)");
  }

  public void testFunctionNewWithReturnType() {
      assertNoWarnings("function(new:Object):string");
  }

  public void testFunctionMultipleNew() {
      assertNoWarnings("function(new:Object, new:String):number");
  }

  public void testFunctionNewMixedWithNamedParams() {
      assertNoWarnings("function(new:Object, x:number):void");
  }

  public void testFunctionNewWithUnionType() {
      assertNoWarnings("function(new:(Object|String)):boolean");
  }

  public void testFunctionNewWithRecordParam() {
      assertNoWarnings("function(new:Object, opts:{x:number}):void");
  }

  public void testFunctionNewNested() {
      assertNoWarnings("function(function(new:Object):void):void");
  }

  public void testFunctionNewWithOptionalParam() {
      assertNoWarnings("function(new:Object, ...args:number):void");
  }

  public void testFunctionNewWithWhitespace() {
      assertNoWarnings("function( new : Object )");
  }

  public void testFunctionPlainNoNewProducesNoWarnings() {
      assertNoWarnings("function():void");
  }

  public void testSyntaxErrorGeneratesWarning() {
      // Truly malformed syntax should still produce warnings
      asswertWarningCount("function(new:)", 1);
  }

  public void testFunctionNewMissingColonGeneratesWarning() {
      // Missing colon after new is a syntax error
      assertWarningCount("function(new Object)", 1);
  }

 }
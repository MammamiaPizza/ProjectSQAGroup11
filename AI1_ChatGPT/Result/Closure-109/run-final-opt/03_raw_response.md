package com.google.javascript.jscomp.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.parsing.Config.LanguageMode;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.head.ErrorReporter;
import com.google.javascript.rhino.head.EvaluatorException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;

public class JsDocInfoParserStructuralConstructorTest {

  @Test
  public void parsesStructuralConstructorParameterWithoutTypeWarning() {
    RecordingErrorReporter reporter = new RecordingErrorReporter();

    JSDocInfo info =
        parse(
            "/**\n"
                + " * @constructor\n"
                + " * @param {function(new: {name: string})} factory\n"
                + " */",
            reporter);

    assertNotNull(info);
    assertTrue(info.isConstructor());
    assertNotNull(info.getParameterType("factory"));
    assertEquals(0, reporter.warnings.size());
  }

  @Test
  public void parsesStructuralConstructorWithMultipleRecordFieldsWithoutTypeWarning() {
    RecordingErrorReporter reporter = new RecordingErrorReporter();

    JSDocInfo info =
        parse(
            "/**\n"
                + " * @constructor\n"
                + " * @param {function(new: {name: string, count: number})} factory\n"
                + " */",
            reporter);

    assertNotNull(info);
    assertTrue(info.isConstructor());
    assertNotNull(info.getParameterType("factory"));
    assertEquals(0, reporter.warnings.size());
  }

  @Test
  public void parsesStructuralConstructorThatAlsoHasFunctionArgumentsAndReturnType() {
    RecordingErrorReporter reporter = new RecordingErrorReporter();

    JSDocInfo info =
        parse(
            "/**\n"
                + " * @constructor\n"
                + " * @param {function(new: {name: string}, number): boolean} factory\n"
                + " */",
            reporter);

    assertNotNull(info);
    assertTrue(info.isConstructor());
    assertNotNull(info.getParameterType("factory"));
    assertEquals(0, reporter.warnings.size());
  }

  @Test
  public void parsesNamedConstructorFunctionTypeWithoutWarnings() {
    RecordingErrorReporter reporter = new RecordingErrorReporter();

    JSDocInfo info =
        parse(
            "/**\n"
                + " * @constructor\n"
                + " * @param {function(new: Widget, string): number} factory\n"
                + " */",
            reporter);

    assertNotNull(info);
    assertTrue(info.isConstructor());
    assertNotNull(info.getParameterType("factory"));
    assertEquals(0, reporter.warnings.size());
  }

  @Test
  public void reportsWarningForMalformedStructuralConstructorType() {
    RecordingErrorReporter reporter = new RecordingErrorReporter();

    JSDocInfo info =
        parse(
            "/**\n"
                + " * @constructor\n"
                + " * @param {function(new: {name: })} factory\n"
                + " */",
            reporter);

    assertNotNull(info);
    assertTrue(info.isConstructor());
    assertTrue(reporter.warnings.size() > 0);
  }

  private static JSDocInfo parse(String comment, RecordingErrorReporter reporter) {
    Config config =
        new Config(
            new HashSet<String>(),
            new HashSet<String>(),
            false,
            LanguageMode.ECMASCRIPT3,
            false);

    JsDocInfoParser parser =
        new JsDocInfoParser(
            new JsDocTokenStream(comment), null, null, config, reporter);

    parser.parse();
    return parser.retrieveAndResetParsedJSDocInfo();
  }

  private static final class RecordingErrorReporter implements ErrorReporter {
    final List<String> warnings = new ArrayList<String>();

    @Override
    public void warning(
        String message, String sourceName, int line, String lineSource, int lineOffset) {
      warnings.add(message);
    }

    @Override
    public void error(
        String message, String sourceName, int line, String lineSource, int lineOffset) {
      warnings.add(message);
    }

    @Override
    public EvaluatorException runtimeError(
        String message, String sourceName, int line, String lineSource, int lineOffset) {
      return new EvaluatorException(message);
    }
  }
}
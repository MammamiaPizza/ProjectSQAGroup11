package com.google.javascript.jscomp.parsing;

import com.google.javascript.jscomp.mozilla.rhino.ErrorReporter;
import com.google.javascript.jscomp.mozilla.rhino.EvaluatorException;
import com.google.javascript.jscomp.parsing.Config.LanguageMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

public class JsDocInfoParserIssue477RegressionTest extends TestCase {

  public void testDeprecatedTextEndingAtCommentTerminatorDoesNotWarn() {
    RecordingErrorReporter reporter = parse(
        " * @deprecated Use the replacement API.\n"
        + " */");

    assertNoDiagnostics(reporter);
  }

  public void testMultilineDeprecatedTextEndingAtCommentTerminatorDoesNotWarn() {
    RecordingErrorReporter reporter = parse(
        " * @deprecated\n"
        + " * This API is obsolete.\n"
        + " * Use the replacement API instead.\n"
        + " */");

    assertNoDiagnostics(reporter);
  }

  public void testParamTypeAtCommentTerminatorDoesNotWarn() {
    RecordingErrorReporter reporter = parse(
        " * @param {string} name The name to use.\n"
        + " */");

    assertNoDiagnostics(reporter);
  }

  public void testReturnTypeAtCommentTerminatorDoesNotWarn() {
    RecordingErrorReporter reporter = parse(
        " * @return {number} The computed value.\n"
        + " */");

    assertNoDiagnostics(reporter);
  }

  public void testMultilineBlockDescriptionEndingAtCommentTerminatorDoesNotWarn() {
    RecordingErrorReporter reporter = parse(
        " * A normal block description.\n"
        + " * It has more than one line.\n"
        + " */");

    assertNoDiagnostics(reporter);
  }

  public void testMultipleAnnotationsBeforeCommentTerminatorDoNotWarn() {
    RecordingErrorReporter reporter = parse(
        " * @param {string} name The name to use.\n"
        + " * @return {string} A greeting for the name.\n"
        + " */");

    assertNoDiagnostics(reporter);
  }

  private RecordingErrorReporter parse(String commentContents) {
    Set<String> annotationNames = new HashSet<String>();
    Set<String> suppressionNames = new HashSet<String>();
    Config config = new Config(
        annotationNames,
        suppressionNames,
        true,
        LanguageMode.ECMASCRIPT3,
        false);
    RecordingErrorReporter reporter = new RecordingErrorReporter();
    JsDocInfoParser parser = new JsDocInfoParser(
        new JsDocTokenStream(commentContents),
        null,
        "Issue477.js",
        config,
        reporter);

    assertTrue(parser.parse());
    return reporter;
  }

  private void assertNoDiagnostics(RecordingErrorReporter reporter) {
    assertEquals("Unexpected parser warnings: " + reporter.warnings,
        0, reporter.warnings.size());
    assertEquals("Unexpected parser errors: " + reporter.errors,
        0, reporter.errors.size());
  }

  private static final class RecordingErrorReporter implements ErrorReporter {
    final List<String> warnings = new ArrayList<String>();
    final List<String> errors = new ArrayList<String>();

    public void warning(
        String message, String sourceName, int line, String lineSource, int lineOffset) {
      warnings.add(message);
    }

    public void error(
        String message, String sourceName, int line, String lineSource, int lineOffset) {
      errors.add(message);
    }

    public EvaluatorException runtimeError(
        String message, String sourceName, int line, String lineSource, int lineOffset) {
      errors.add(message);
      return new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
    }
  }
}
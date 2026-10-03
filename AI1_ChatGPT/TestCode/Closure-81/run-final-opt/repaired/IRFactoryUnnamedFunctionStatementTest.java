package com.google.javascript.jscomp.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.mozilla.rhino.ErrorReporter;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class IRFactoryUnnamedFunctionStatementTest {

  @Test
  public void namedFunctionStatementTransformsToNamedFunctionWithoutErrors() {
    RecordingErrorReporter reporter = new RecordingErrorReporter();

    Node script = parse("function named() {}", reporter);

    assertEquals(0, reporter.errorCount);
    assertEquals(Token.SCRIPT, script.getType());
    Node function = script.getFirstChild();
    assertNotNull(function);
    assertEquals(Token.FUNCTION, function.getType());
    assertNotNull(function.getFirstChild());
    assertEquals(Token.NAME, function.getFirstChild().getType());
    assertEquals("named", function.getFirstChild().getString());
  }

  @Test
  public void anonymousFunctionExpressionIsAcceptedWithoutErrors() {
    RecordingErrorReporter reporter = new RecordingErrorReporter();

    Node script = parse("(function () {})", reporter);

    assertEquals(0, reporter.errorCount);
    Node expressionStatement = script.getFirstChild();
    assertNotNull(expressionStatement);
    assertEquals(Token.EXPR_RESULT, expressionStatement.getType());
    Node function = expressionStatement.getFirstChild();
    assertNotNull(function);
    assertEquals(Token.FUNCTION, function.getType());
    assertNotNull(function.getFirstChild());
    assertEquals(Token.NAME, function.getFirstChild().getType());
    assertEquals("", function.getFirstChild().getString());
  }

  @Test
  public void unnamedFunctionStatementReportsAnErrorRatherThanSilentlyBecomingDeclaration() {
    RecordingErrorReporter reporter = new RecordingErrorReporter();

    Node script = parse("function () {}", reporter);

    assertNotNull(script);
    assertTrue(
        "An unnamed function statement is invalid and must be reported",
        reporter.errorCount > 0);
  }

  private static Node parse(String source, RecordingErrorReporter reporter) {
    Config config =
        ParserRunner.createConfig(ParserRunner.LanguageMode.ECMASCRIPT3, false, false);
    return ParserRunner.parse(source, "test.js", config, reporter).ast;
  }

  private static final class RecordingErrorReporter implements ErrorReporter {
    int errorCount;

    @Override
    public void warning(
        String message, String sourceName, int line, String lineSource, int lineOffset) {
    }

    @Override
    public void error(
        String message, String sourceName, int line, String lineSource, int lineOffset) {
      errorCount++;
    }
  }
}

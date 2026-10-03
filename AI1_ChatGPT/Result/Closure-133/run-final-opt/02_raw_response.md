package com.google.javascript.jscomp.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.parsing.Config.LanguageMode;
import com.google.javascript.rhino.JSDocInfo;
import java.util.HashSet;
import org.junit.Test;

public class JsDocInfoParserTextExtentsTest {

  private JSDocInfo parse(String comment) {
    Config config = new Config(
        new HashSet<String>(),
        new HashSet<String>(),
        true,
        LanguageMode.ECMASCRIPT3,
        false);
    JsDocInfoParser parser = new JsDocInfoParser(
        new JsDocTokenStream(comment),
        null,
        null,
        config,
        NullErrorReporter.forNewRhino());

    assertTrue(parser.parse());
    JSDocInfo info = parser.retrieveAndResetParsedJSDocInfo();
    assertNotNull(info);
    return info;
  }

  @Test
  public void testMultilineBlockDescriptionHasStableTextExtent() {
    JSDocInfo info = parse(
        "/**\n"
            + " * First line of documentation.\n"
            + " * Second line of documentation.\n"
            + " */");

    assertEquals(
        "First line of documentation.\nSecond line of documentation.",
        info.getBlockDescription());
  }

  @Test
  public void testSingleLineBlockDescription() {
    JSDocInfo info = parse("/** A concise description. */");

    assertEquals("A concise description.", info.getBlockDescription());
  }

  @Test
  public void testMultilineParameterDescriptionBeforeClosingComment() {
    JSDocInfo info = parse(
        "/**\n"
            + " * @param {string} value First part of the description.\n"
            + " *     Second part of the description.\n"
            + " */");

    assertEquals(
        "First part of the description.\nSecond part of the description.",
        info.getDescriptionForParameter("value"));
  }

  @Test
  public void testMultilineReturnDescriptionWithWhitespaceOnlyLine() {
    JSDocInfo info = parse(
        "/**\n"
            + " * @return {number} The computed value.\n"
            + " *     It is always finite.\n"
            + " *\n"
            + " */");

    assertEquals(
        "The computed value.\nIt is always finite.",
        info.getReturnDescription());
  }

  @Test
  public void testDescriptionDoesNotIncludeTrailingWhitespaceOnlyLines() {
    JSDocInfo info = parse(
        "/**\n"
            + " * Text immediately before the end of the comment.\n"
            + " *    \n"
            + " *\n"
            + " */");

    assertEquals(
        "Text immediately before the end of the comment.",
        info.getBlockDescription());
  }

  @Test
  public void testDeprecatedReasonCanSpanMultipleLines() {
    JSDocInfo info = parse(
        "/**\n"
            + " * @deprecated Use the replacement API.\n"
            + " *     This API will be removed later.\n"
            + " */");

    assertEquals(
        "Use the replacement API.\nThis API will be removed later.",
        info.getDeprecationReason());
  }
}
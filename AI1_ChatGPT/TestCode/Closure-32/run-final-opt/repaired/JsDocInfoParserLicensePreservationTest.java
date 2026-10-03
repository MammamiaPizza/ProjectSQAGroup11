package com.google.javascript.jscomp.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.javascript.jscomp.parsing.Config.LanguageMode;
import com.google.javascript.rhino.JSDocInfo;
import java.util.HashSet;
import org.junit.Test;

public class JsDocInfoParserLicensePreservationTest {

  private JSDocInfo parse(String comment) {
    Config config =
        new Config(
            new HashSet<String>(),
            new HashSet<String>(),
            true,
            LanguageMode.ECMASCRIPT3,
            false);
    JsDocInfoParser parser =
        new JsDocInfoParser(
            new JsDocTokenStream(comment),
            null,
            null,
            config,
            NullErrorReporter.forNewRhino());
    parser.parse();
    return parser.retrieveAndResetParsedJSDocInfo();
  }

  @Test
  public void licenseKeepsLeadingSpaceOnSingleLine() {
    assertEquals(" Foo", parse("@license Foo").getLicense());
  }

  @Test
  public void preserveKeepsLeadingSpaceOnSingleLine() {
    assertEquals(" Foo", parse("@preserve Foo").getLicense());
  }

  @Test
  public void licenseKeepsAsciiTextAndSurroundingWhitespace() {
    assertEquals(
        "   Copyright 2011, Example Corp.  ",
        parse("@license   Copyright 2011, Example Corp.  ").getLicense());
  }

  @Test
  public void preserveKeepsMultilineTextualBlockFormatting() {
    assertEquals(
        " Foo\n Bar\n Baz",
        parse("@preserve Foo\n * Bar\n * Baz").getLicense());
  }

  @Test
  public void licenseKeepsMultilineTextualBlockFormatting() {
    assertEquals(
        " Foo\n Bar\n Baz",
        parse("@license Foo\n * Bar\n * Baz").getLicense());
  }

  @Test
  public void commentWithoutPreservationAnnotationHasNoLicense() {
    assertNull(parse("@author Foo").getLicense());
  }
}

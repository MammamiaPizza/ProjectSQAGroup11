package com.google.javascript.jscomp.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.Sets;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import org.junit.Test;

public class Bug142JsDocInfoParserTest {

  private JSDocInfo parse(String comment) {
    JsDocInfoParser.Config config =
        new JsDocInfoParser.Config(
            new JSTypeRegistry(NullErrorReporter.forOldRhino()),
            Sets.<String>newHashSet(),
            false);
    JsDocInfoParser parser =
        new JsDocInfoParser(
            new JsDocTokenStream(comment),
            "Bug142JsDocInfoParserTest",
            config,
            NullErrorReporter.forNewRhino());
    return parser.parse();
  }

  @Test
  public void testLicenseTextIsPreservedBeforeFollowingAnnotation() {
    JSDocInfo info = parse("@license Foo\n * @param {string} bar");

    assertEquals(" Foo", info.getLicense());
    assertNotNull(info.getParameterType("bar"));
  }

  @Test
  public void testSingleLineLicensePreservesItsText() {
    JSDocInfo info = parse("@license Foo");

    assertEquals(" Foo", info.getLicense());
  }
}
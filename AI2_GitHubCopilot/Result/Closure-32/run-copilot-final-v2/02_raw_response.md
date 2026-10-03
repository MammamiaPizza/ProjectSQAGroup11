package com.google.javascript.jscomp.parsing;

import junit.framework.TestCase;
import com.google.common.collect.Sets;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.Config.LanguageMode;
import com.google.javascript.jscomp.parsing.JsDocInfoParser;
import com.google.javascript.jscomp.parsing.JsDocTokenStream;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.head.NullErrorReporter;
import com.google.javascript.rhino.head.Token;
import com.google.javascript.rhino.head.ast.Comment;

public class JsDocInfoParserTest extends TestCase {

 private JSDocInfo parse(String comment) {
     Comment commentNode = new Comment(0, comment.length(), Token.CommentType.JSDOC, comment);
     Config config = new Config(Sets.<String>newHashSet(), Sets.<String>newHashSet(), false,
LanguageMode.ECMASCRIPT3, false);
     JsDocInfoParser parser = new JsDocInfoParser(new JsDocTokenStream(comment), commentNode, null,
config, NullErrorReporter.forNewRhino());
     return parser.parse();
 }

 public void testParseLicenseMultiline() throws Exception {
     String comment = "/**\n * @license\n * Hello\n * World\n */";
     JSDocInfo info = parse(comment);
     assertNotNull(info);
     String license = info.getLicense();
     assertNotNull(license);
     String[] lines = license.split("\n");
     for (String line : lines) {
         assertFalse("Line starts with space: '" + line + "'", line.startsWith(" "));
         assertFalse("Line starts with tab: '" + line + "'", line.startsWith("\t"));
         assertFalse("Line starts with star: '" + line + "'", line.startsWith("*"));
     }
     assertEquals("Hello\nWorld", license);
 }

 public void testParsePreserveMultiline() throws Exception {
     String comment = "/**\n * @preserve\n * Foo\n * Bar\n */";
     JSDocInfo info = parse(comment);
     assertNotNull(info);
     String license = info.getLicense();
     assertNotNull(license);
     assertEquals("Foo\nBar", license);
 }

 public void testParseLicenseSingleLineSameLine() throws Exception {
     String comment = "/** @license Foo */";
     JSDocInfo info = parse(comment);
     assertNotNull(info);
     String license = info.getLicense();
     assertNotNull(license);
     assertEquals("Foo", license.trim());
 }

 public void testParseLicenseLeadingWhitespaceStripped() throws Exception {
     String comment = "/**\n * @license\n *   Indented\n */";
     JSDocInfo info = parse(comment);
     assertNotNull(info);
     String license = info.getLicense();
     assertNotNull(license);
     String[] lines = license.split("\n");
     for (String line : lines) {
         assertFalse("Line should not have leading spaces or tabs: '" + line + "'",
line.startsWith(" ") || line.startsWith("\t"));
     }
     assertEquals("Indented", license.trim());
 }

 public void testParseLicenseEmptyContent() throws Exception {
     String comment = "/** @license */";
     JSDocInfo info = parse(comment);
     assertNotNull(info);
     String license = info.getLicense();
     assertNotNull(license);
     assertEquals("", license.trim());
 }

 public void testParseLicenseWhitespaceOnlyContent() throws Exception {
     String comment = "/** @license   */";
     JSDocInfo info = parse(comment);
     assertNotNull(info);
     String license = info.getLicense();
     assertNotNull(license);
     assertTrue("License should be empty or whitespace only already trimmed",
license.trim().isEmpty());
 }

 public void testParsePreserveNonAscii() throws Exception {
     String comment = "/**\n * @preserve\n * \u00A9 2023 Copyright\n */";
     JSDocInfo info = parse(comment);
     assertNotNull(info);
     String license = info.getLicense();
     assertNotNull(license);
     assertEquals("\u00A9 2023 Copyright", license.trim());
 }

 public void testParseLicenseMixedIndentation() throws Exception {
     String comment = "/**\n * @license\n\tTabbed\n  Spaces\n */";
     JSDocInfo info = parse(comment);
     assertNotNull(info);
     String license = info.getLicense();
     assertNotNull(license);
     String[] lines = license.split("\n");
     for (String line : lines) {
         assertFalse("Line should not have leading whitespace: '" + line + "'", line.startsWith(" ")
|| line.startsWith("\t"));
     }
     assertEquals("Tabbed\nSpaces", license);
 }

 public void testParseLicenseTrailingNewline() throws Exception {
     String comment = "/**\n * @license\n * Text\n */";
     JSDocInfo info = parse(comment);
     assertNotNull(info);
     String license = info.getLicense();
     assertNotNull(license);
     // No trailing newline
     assertFalse(license.endsWith("\n"));
     assertEquals("Text", license);
 }

 public void testParseLicenseStarInsideText() throws Exception {
     String comment = "/**\n * @license\n * Some * text\n */";
     JSDocInfo info = parse(comment);
     assertNotNull(info);
     String license = info.getLicense();
     assertNotNull(license);
     assertEquals("Some * text", license);
 }

}
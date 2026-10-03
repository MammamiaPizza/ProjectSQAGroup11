package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import java.io.IOException;
import java.io.StringWriter;

public class Bug148Test extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeFoldConstants(compiler); }

  public void testFoldTypeofNumber() {
    test("typeof 1", ""number""); }

  public void testFoldTypeofString() {
    test("typeof 'foo'", ""string""); }

  public void testFoldTypeofBoolean() {
    test("typeof true", ""boolean"");
    test("typeof false", ""boolean""); }

  public void testFoldTypeofNullAndVoid() {
    test("typeof null", ""undefined"");
    test("typeof void 0", ""undefined""); }

  public void testFoldTypeofFunctionExpression() {
    test("typeof function() {}", ""function""); }

  public void testDoNotFoldTypeofUnknownIdentifier() {
    testSame("typeof abc"); }

  public void testDoNotFoldTypeofCallExpression() {
    testSame("typeof abc()"); }

  private Node newMappingNode(String sourceFile, int originalLine,
      int originalCol, String originalName) {
    Node node = new Node(Token.NAME);
    node.putProp(Node.SOURCEFILE_PROP, sourceFile);
    node.setLineno(originalLine);
    node.setCharno(originalCol);
    if (originalName != null) {
      node.putProp(Node.ORIGINALNAME_PROP, originalName);
    }
    return node; }

  public void testAppendToSingleLineMap() throws IOException {
    SourceMap map = new SourceMap();
    map.addMapping(newMappingNode("test.js", 1, 1, null),
        new SourceMap.Position(0, 0), new SourceMap.Position(0, 5));
    StringWriter out = new StringWriter();
    map.appendTo(out, "test.js");

 String expected =
     "/** Begin line maps. **/{ \"file\" : \"test.js\", \"count\": 1 }\n" +
     "[0,0,0,0,0]\n" +
     "/** Begin file information. **/\n" +
     "[]\n" +
     "/** Begin mapping definitions. **/\n" +
     "[\"test.js\", 1, 1]\n";
 assertEquals(expected, out.toString()); }

  public void testAppendToMultiLineMap() throws IOException {
    SourceMap map = new SourceMap();
    map.addMapping(newMappingNode("a.js", 1, 34, null),
        new SourceMap.Position(0, 0), new SourceMap.Position(0, 6));
    map.addMapping(newMappingNode("a.js", 5, 2, null),
        new SourceMap.Position(0, 6), new SourceMap.Position(0, 10));
    map.addMapping(newMappingNode("b.js", 1, 3, "event"),
        new SourceMap.Position(0, 10), new SourceMap.Position(1, 6));
    map.addMapping(newMappingNode("c.js", 1, 4, null),
        new SourceMap.Position(1, 6), new SourceMap.Position(1, 7));
    map.addMapping(newMappingNode("d.js", 3, 78, "foo"),
        new SourceMap.Position(1, 7), new SourceMap.Position(1, 12));

 StringWriter out = new StringWriter();
 map.appendTo(out, "test.js");
 String actual = out.toString();

 assertTrue(actual.startsWith(
     "/** Begin line maps. **/{ \"file\" : \"test.js\", \"count\": 2 }\n"));
 assertTrue(actual.contains(
     "/** Begin file information. **/\n[]\n[]\n"));
 assertTrue(actual.contains("[\"a.js\", 1, 34]\n"));
 assertTrue(actual.contains("[\"a.js\", 5, 2]\n"));
 assertTrue(actual.contains("[\"b.js\", 1, 3, \"event\"]\n"));
 assertTrue(actual.contains("[\"c.js\", 1, 4]\n"));
 assertTrue(actual.contains("[\"d.js\", 3, 78, \"foo\"]\n")); }

  public void testAppendToEmptyMapThrowsIllegalStateException() {
    SourceMap map = new SourceMap();
    try {
      map.appendTo(new StringWriter(), "test.js");
      fail("Expected IllegalStateException for an empty source map");
    } catch (IllegalStateException expected) {
      assertNotNull(expected);
    } catch (IOException unexpected) {
      fail("Unexpected IOException: " + unexpected);
    } }
}

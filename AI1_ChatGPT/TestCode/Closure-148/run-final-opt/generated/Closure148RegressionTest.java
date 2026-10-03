package com.google.javascript.jscomp;

import com.google.javascript.jscomp.SourceMap.Position;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import java.io.StringWriter;
import org.junit.Before;
import org.junit.Test;

public class Closure148RegressionTest extends CompilerTestCase {

  public Closure148RegressionTest() {
    super("", "");
  }

  @Before
  public void initializeCompilerTestCase() throws Exception {
    super.setUp();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeFoldConstants());
  }

  @Test
  public void testTypeofPrimitiveLiteralsAreFolded() {
    test("typeof 1", "'number'");
    test("typeof 'text'", "'string'");
    test("typeof true", "'boolean'");
  }

  @Test
  public void testTypeofNullIsObject() {
    test("typeof null", "'object'");
  }

  @Test
  public void testTypeofVoidExpressionIsUndefined() {
    test("typeof void 0", "'undefined'");
  }

  @Test
  public void testTypeofObjectLiteralsAreObjects() {
    test("typeof []", "'object'");
    test("typeof ({a: 1})", "'object'");
    test("typeof /x/", "'object'");
  }

  @Test
  public void testTypeofFunctionExpressionIsFunction() {
    test("typeof function() {}", "'function'");
  }

  @Test
  public void testTypeofUnknownNameIsNotFolded() {
    testSame("typeof value");
    testSame("typeof value()");
  }

  @Test
  public void testSourceMapSerializesSingleMappingWithSourceAndName() throws Exception {
    SourceMap map = new SourceMap();
    Node node = new Node(Token.NAME, "generated");
    node.putProp(Node.SOURCEFILE_PROP, "input.js");
    node.putProp(Node.ORIGINALNAME_PROP, "original");
    node.setLineno(1);
    node.setCharno(2);

    map.addMapping(node, new Position(0, 0), new Position(0, 3));

    StringWriter out = new StringWriter();
    map.appendTo(out, "output.js");

    assertEquals(
        "/** Begin line maps. **/{ \"file\" : \"output.js\", \"count\": 1 }\n"
            + "[0,0,0]\n"
            + "/** Begin file information. **/\n"
            + "[]\n"
            + "/** Begin mapping definitions. **/\n"
            + "[\"input.js\", 1, 2, \"original\"]\n",
        out.toString());
  }
}

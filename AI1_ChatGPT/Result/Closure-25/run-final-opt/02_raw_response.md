package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import junit.framework.TestCase;

public class TypeInferenceNewRegressionTest extends TestCase {

  public void testNewCallBackwardsInfersMissingObjectProperty() {
    String source =
        "/** @constructor @param {{foo: (number|undefined)}} x */\n"
            + "function Foo(x) {}\n"
            + "new Foo({});\n";

    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    options.inferTypes = true;

    Compiler compiler = new Compiler();
    compiler.compile(
        SourceFile.fromCode("externs.js", ""),
        SourceFile.fromCode("test.js", source),
        options);

    Node objectLiteral = findObjectLiteral(compiler.getRoot());
    assertNotNull(objectLiteral);
    assertNotNull(objectLiteral.getJSType());
    assertEquals("{foo: (number|undefined)}", objectLiteral.getJSType().toString());
  }

  private Node findObjectLiteral(Node node) {
    if (node == null) {
      return null;
    }
    if (node.isObjectLit()) {
      return node;
    }
    for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
      Node result = findObjectLiteral(child);
      if (result != null) {
        return result;
      }
    }
    return null;
  }
}
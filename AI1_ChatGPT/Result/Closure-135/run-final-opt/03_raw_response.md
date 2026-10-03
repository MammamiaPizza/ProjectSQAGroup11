package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Test;

public class DevirtualizePrototypeMethodsRegressionTest extends TestCase {

  @Test
  public void testRewrittenPrototypeMethodsKeepInstanceTypedSelfParameter() {
    assertTransformation(
        "/** @constructor */ function a() {}\n"
            + "/** @return {number} */ a.prototype.foo = function() { return 1; };\n"
            + "/** @param {number} x @return {number} */\n"
            + "a.prototype.bar = function(x) { return x; };\n"
            + "/** @param {number} x @param {number} y @return {number} */\n"
            + "a.prototype.baz = function(x, y) { return x + y; };\n"
            + "var instance = new a();\n"
            + "var first = instance.foo();\n"
            + "var second = instance.bar(2);\n"
            + "var third = instance.baz(3, 4);",
        "/** @constructor */ function a() {}\n"
            + "/** @return {number} */\n"
            + "var JSCompiler_StaticMethods_foo = "
            + "function(JSCompiler_StaticMethods_foo$self) { return 1; };\n"
            + "/** @param {number} x @return {number} */\n"
            + "var JSCompiler_StaticMethods_bar = "
            + "function(JSCompiler_StaticMethods_bar$self, x) { return x; };\n"
            + "/** @param {number} x @param {number} y @return {number} */\n"
            + "var JSCompiler_StaticMethods_baz = "
            + "function(JSCompiler_StaticMethods_baz$self, x, y) { return x + y; };\n"
            + "var instance = new a();\n"
            + "var first = JSCompiler_StaticMethods_foo(instance);\n"
            + "var second = JSCompiler_StaticMethods_bar(instance, 2);\n"
            + "var third = JSCompiler_StaticMethods_baz(instance, 3, 4);",
        true);
  }

  @Test
  public void testVarArgsPrototypeMethodIsNotRewritten() {
    assertTransformation(
        "/** @constructor */ function a() {}\n"
            + "a.prototype.count = function() { return arguments.length; };\n"
            + "var instance = new a();\n"
            + "var count = instance.count(1, 2, 3);",
        "/** @constructor */ function a() {}\n"
            + "a.prototype.count = function() { return arguments.length; };\n"
            + "var instance = new a();\n"
            + "var count = instance.count(1, 2, 3);",
        false);
  }

  private void assertTransformation(String source, String expectedSource, boolean checkReceiverTypes) {
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;

    Compiler compiler = new Compiler();
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("input.js", source)),
        options);

    Node root = compiler.getRoot();
    Node externs = root.getFirstChild();
    Node jsRoot = root.getLastChild();
    new DevirtualizePrototypeMethods(compiler).process(externs, jsRoot);

    Compiler expectedCompiler = new Compiler();
    expectedCompiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("expected.js", expectedSource)),
        new CompilerOptions());

    assertEquals(
        expectedCompiler.toSource(expectedCompiler.getRoot().getLastChild()),
        compiler.toSource(jsRoot));

    if (checkReceiverTypes) {
      assertRewrittenReceiverTypes(jsRoot);
    }
  }

  private void assertRewrittenReceiverTypes(Node node) {
    if (node.getType() == Token.VAR) {
      for (Node name = node.getFirstChild(); name != null; name = name.getNext()) {
        if (name.getType() == Token.NAME
            && name.getString().startsWith("JSCompiler_StaticMethods_")) {
          Node function = name.getFirstChild();
          assertNotNull(function);
          assertEquals(Token.FUNCTION, function.getType());

          Node parameters = function.getFirstChild().getNext();
          Node self = parameters.getFirstChild();
          assertNotNull(self);
          assertNotNull(self.getJSType());
          assertEquals("a", self.getJSType().toString());
        }
      }
    }

    for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
      assertRewrittenReceiverTypes(child);
    }
  }
}
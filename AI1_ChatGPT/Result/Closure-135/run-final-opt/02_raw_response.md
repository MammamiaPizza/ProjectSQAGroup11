package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class DevirtualizePrototypeMethodsRegressionTest extends CompilerTestCase {

  public DevirtualizePrototypeMethodsRegressionTest() {
    super("");
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    enableTypeCheck();
  }

  @Override
  protected CompilerPass getProcessor(final Compiler compiler) {
    return new CompilerPass() {
      @Override
      public void process(Node externs, Node root) {
        new DevirtualizePrototypeMethods(compiler).process(externs, root);
        assertRewrittenReceiverTypes(root);
      }
    };
  }

  @Test
  public void testRewrittenPrototypeMethodsKeepInstanceTypedSelfParameter() {
    test(
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
            + "var third = JSCompiler_StaticMethods_baz(instance, 3, 4);");
  }

  @Test
  public void testVarArgsPrototypeMethodIsNotRewritten() {
    test(
        "/** @constructor */ function a() {}\n"
            + "a.prototype.count = function() { return arguments.length; };\n"
            + "var instance = new a();\n"
            + "var count = instance.count(1, 2, 3);",
        "/** @constructor */ function a() {}\n"
            + "a.prototype.count = function() { return arguments.length; };\n"
            + "var instance = new a();\n"
            + "var count = instance.count(1, 2, 3);");
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
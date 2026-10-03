package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.ObjectType;
import junit.framework.TestCase;
import org.junit.Test;

public class TypedScopeCreatorRegressionTest extends CompilerTestCase {
  private Scope globalScope;
  private Scope localScope;

  @Override
  protected void setUp() throws Exception {
    parseTypeInfo = true;
    super.setUp();
  }

  @Override
  protected CompilerPass getProcessor(final Compiler compiler) {
    return new CompilerPass() {
      @Override
      public void process(Node externs, Node root) {
        TypedScopeCreator creator = new TypedScopeCreator(compiler);
        globalScope = creator.createScope(root, null);

        Node function = findFirstFunction(root);
        if (function != null) {
          localScope = creator.createScope(function, globalScope);
        }
      }
    };
  }

  @Test
  public void testGlobalQualifiedNameDeclarationCreatesTypedProperty() {
    testSame(
        "var goog = {};\n"
            + "/** @type {number} */ goog.value = 1;");

    assertNumberProperty(globalScope, "goog", "value");
  }

  @Test
  public void testGlobalQualifiedNameDeclaredInsideFunctionCreatesGlobalProperty() {
    testSame(
        "var goog = {};\n"
            + "function f() {\n"
            + "  /** @type {number} */ goog.value = 1;\n"
            + "}");

    assertNotNull(localScope);
    assertNumberProperty(globalScope, "goog", "value");
  }

  private static Node findFirstFunction(Node node) {
    if (node.getType() == Token.FUNCTION) {
      return node;
    }

    for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
      Node result = findFirstFunction(child);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  private static void assertNumberProperty(Scope scope, String objectName, String propertyName) {
    Scope.Var var = scope.getVar(objectName);
    assertNotNull(var);

    ObjectType objectType = var.getType().toObjectType();
    assertNotNull(objectType);

    JSType propertyType = objectType.getPropertyType(propertyName);
    assertNotNull(propertyType);
    assertTrue(propertyType.isNumberValueType());
  }
}

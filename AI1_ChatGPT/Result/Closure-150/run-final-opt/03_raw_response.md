package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.ObjectType;
import org.junit.Test;

public class TypedScopeCreatorLocalStubRegressionTest {

  @Test
  public void testNamespacedFunctionStubInLocalScopeDefinesTypedProperty() {
    Compiler compiler = new Compiler();
    compiler.init(
        new JSSourceFile[] {},
        new JSSourceFile[] {
          JSSourceFile.fromCode(
              "test.js",
              "function f() {\n"
                  + "  var ns = {};\n"
                  + "  /** @param {number} value @return {string} */\n"
                  + "  ns.method;\n"
                  + "}\n")
        },
        new CompilerOptions());

    Node root = compiler.parseInputs();
    TypedScopeCreator creator = new TypedScopeCreator(compiler);
    Scope globalScope = creator.createScope(root, null);
    Scope localScope = creator.createScope(findFirstFunction(root), globalScope);

    Scope.Var namespace = localScope.getVar("ns");
    assertNotNull(namespace);

    ObjectType namespaceType = namespace.getType().toObjectType();
    assertNotNull(namespaceType);
    assertTrue(namespaceType.hasProperty("method"));

    JSType methodType = namespaceType.getPropertyType("method");
    assertTrue(methodType instanceof FunctionType);

    FunctionType functionType = (FunctionType) methodType;
    assertEquals(1, functionType.getMinArguments());
    assertTrue(functionType.getReturnType().isStringValueType());
  }

  @Test
  public void testCollectedFunctionStubInLocalScopeDefinesConstructorProperty() {
    Compiler compiler = new Compiler();
    compiler.init(
        new JSSourceFile[] {},
        new JSSourceFile[] {
          JSSourceFile.fromCode(
              "test.js",
              "/** @constructor */\n"
                  + "function C() {\n"
                  + "  /** @param {boolean} flag @return {number} */\n"
                  + "  this.method;\n"
                  + "}\n")
        },
        new CompilerOptions());

    Node root = compiler.parseInputs();
    TypedScopeCreator creator = new TypedScopeCreator(compiler);
    Scope globalScope = creator.createScope(root, null);
    Scope localScope = creator.createScope(findFirstFunction(root), globalScope);

    assertNotNull(localScope.getVar("arguments"));

    Scope.Var constructor = globalScope.getVar("C");
    assertNotNull(constructor);
    assertTrue(constructor.getType() instanceof FunctionType);

    ObjectType instanceType = ((FunctionType) constructor.getType()).getInstanceType();
    assertNotNull(instanceType);
    assertTrue(instanceType.hasProperty("method"));

    JSType methodType = instanceType.getPropertyType("method");
    assertTrue(methodType instanceof FunctionType);

    FunctionType functionType = (FunctionType) methodType;
    assertEquals(1, functionType.getMinArguments());
    assertTrue(functionType.getReturnType().isNumberValueType());
  }

  private static Node findFirstFunction(Node node) {
    if (node.getType() == Token.FUNCTION) {
      return node;
    }
    for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
      Node function = findFirstFunction(child);
      if (function != null) {
        return function;
      }
    }
    return null;
  }
}
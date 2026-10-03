package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerOptions.AliasTransformation;
import com.google.javascript.jscomp.CompilerOptions.AliasTransformationHandler;
import com.google.javascript.rhino.SourcePosition;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class ScopedAliasesGeneratedTest extends TestCase {

  private static final AliasTransformationHandler NO_OP_ALIAS_TRANSFORMATION_HANDLER =
      new AliasTransformationHandler() {
        @Override
        public AliasTransformation logAliasTransformation(
            String sourceFile, SourcePosition<AliasTransformation> position) {
          return new AliasTransformation() {
            @Override
            public void addAlias(String alias, String definition) {
            }
          };
        }
      };

  public void testRewritesSimpleAliasAndRemovesScope() throws Exception {
    assertTransformation(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  dom.createElement('DIV');"
            + "});",
        "goog.dom.createElement('DIV');");
  }

  public void testRewritesTransitiveAliases() throws Exception {
    assertTransformation(
        "goog.scope(function() {"
            + "  var g = goog;"
            + "  var dom = g.dom;"
            + "  dom.createElement('DIV');"
            + "});",
        "goog.dom.createElement('DIV');");
  }

  public void testRewritesAliasesInJSDocTypes() throws Exception {
    assertTransformation(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  /** @param {dom.Helper} helper */"
            + "  dom.use = function(helper) {};"
            + "});",
        "/** @param {goog.dom.Helper} helper */"
            + "goog.dom.use = function(helper) {};");
  }

  public void testRejectsNonAliasLocalInScope() throws Exception {
    assertError(
        "goog.scope(function() {"
            + "  var local = 1;"
            + "});",
        ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
  }

  public void testRejectsAliasRedefinition() throws Exception {
    assertError(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  dom = goog.other;"
            + "});",
        ScopedAliases.GOOG_SCOPE_ALIAS_REDEFINED);
  }

  public void testRejectsScopeFunctionWithParameters() throws Exception {
    assertError(
        "goog.scope(function(value) {"
            + "});",
        ScopedAliases.GOOG_SCOPE_HAS_BAD_PARAMETERS);
  }

  private void assertTransformation(String input, String expected) throws Exception {
    Object compiler = runPass(input);
    assertEquals(0, Array.getLength(invoke(compiler, "getErrors")));
    assertEquals(0, Array.getLength(invoke(compiler, "getWarnings")));
    assertEquals(expected, invoke(compiler, "toSource"));
  }

  private void assertError(String input, Object expectedType) throws Exception {
    Object compiler = runPass(input);
    Object errors = invoke(compiler, "getErrors");
    assertEquals(1, Array.getLength(errors));
    Object error = Array.get(errors, 0);
    Object actualType;
    try {
      actualType = invoke(error, "getType");
    } catch (NoSuchMethodException e) {
      Field type = error.getClass().getField("type");
      actualType = type.get(error);
    }
    assertSame(expectedType, actualType);
    assertEquals(0, Array.getLength(invoke(compiler, "getWarnings")));
  }

  private Object runPass(String input) throws Exception {
    Class<?> compilerClass = Class.forName("com.google.javascript.jscomp.Compiler");
    Object compiler = compilerClass.newInstance();
    Class<?> sourceFileClass = Class.forName("com.google.javascript.jscomp.SourceFile");
    Method fromCode = findMethod(sourceFileClass, "fromCode", 2);

    List<Object> externs = new ArrayList<Object>();
    externs.add(fromCode.invoke(null, "externs", ""));
    List<Object> inputs = new ArrayList<Object>();
    inputs.add(fromCode.invoke(null, "input", input));

    Object options = Class.forName("com.google.javascript.jscomp.CompilerOptions").newInstance();
    findMethod(compilerClass, "init", 3).invoke(compiler, externs, inputs, options);
    Object root = invoke(compiler, "parseInputs");

    Constructor<?> constructor = null;
    for (Constructor<?> candidate : ScopedAliases.class.getDeclaredConstructors()) {
      if (candidate.getParameterTypes().length == 3) {
        constructor = candidate;
        break;
      }
    }
    if (constructor == null) {
      throw new NoSuchMethodException("ScopedAliases constructor");
    }
    constructor.setAccessible(true);
    Object pass =
        constructor.newInstance(compiler, null, NO_OP_ALIAS_TRANSFORMATION_HANDLER);
    findMethod(ScopedAliases.class, "process", 2).invoke(pass, null, root);
    return compiler;
  }

  private static Object invoke(Object target, String name) throws Exception {
    try {
      return findMethod(target.getClass(), name, 0).invoke(target);
    } catch (InvocationTargetException e) {
      throw rethrow(e);
    }
  }

  private static Method findMethod(Class<?> type, String name, int parameterCount)
      throws NoSuchMethodException {
    for (Method method : type.getMethods()) {
      if (method.getName().equals(name)
          && method.getParameterTypes().length == parameterCount) {
        return method;
      }
    }
    throw new NoSuchMethodException(type.getName() + "." + name);
  }

  private static Exception rethrow(InvocationTargetException e) throws Exception {
    Throwable cause = e.getCause();
    if (cause instanceof Exception) {
      return (Exception) cause;
    }
    if (cause instanceof Error) {
      throw (Error) cause;
    }
    return e;
  }
}

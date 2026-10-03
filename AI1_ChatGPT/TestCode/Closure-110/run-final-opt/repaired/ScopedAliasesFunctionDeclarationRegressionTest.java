package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScopedAliasesFunctionDeclarationRegressionTest {

  @Test
  public void testFunctionDeclarationInGoogScopeIsAccepted() {
    CompilationResult result =
        compile("goog.scope(function() { function f() {} });");

    assertNoErrors(result);
    assertEquals("functionf(){}", normalize(result.source));
  }

  @Test
  public void testHoistedFunctionDeclarationInGoogScopeIsAccepted() {
    CompilationResult result =
        compile("goog.scope(function() { f(); function f() {} });");

    assertNoErrors(result);
    assertEquals("f();functionf(){}", normalize(result.source));
  }

  @Test
  public void testFunctionDeclarationCanUseScopedAlias() {
    CompilationResult result =
        compile(
            "goog.scope(function() {"
                + "var Foo = goog.foo;"
                + "function f() { return Foo; }"
                + "f();"
                + "});");

    assertNoErrors(result);
    assertEquals("functionf(){returngoog.foo;}f();", normalize(result.source));
  }

  @Test
  public void testNonAliasLocalStillProducesError() {
    CompilationResult result =
        compile("goog.scope(function() { var f = 1; });");

    assertTrue(hasError(result.errors, "JSC_GOOG_SCOPE_NON_ALIAS_LOCAL"));
  }

  private CompilationResult compile(String source) {
    try {
      Compiler compiler = new Compiler();
      Class<?> sourceFileClass = Class.forName("com.google.javascript.jscomp.SourceFile");
      Class<?> optionsClass = Class.forName("com.google.javascript.jscomp.CompilerOptions");

      Method fromCode = sourceFileClass.getMethod("fromCode", String.class, String.class);
      Object input = fromCode.invoke(null, "testcode", source);
      Object options = optionsClass.newInstance();

      List<Object> externs = new ArrayList<Object>();
      List<Object> inputs = new ArrayList<Object>();
      inputs.add(input);

      Method init = findMethod(Compiler.class, "init", 3);
      init.invoke(compiler, externs, inputs, options);
      findMethod(Compiler.class, "parseInputs", 0).invoke(compiler);

      Node externsRoot = (Node) findMethod(Compiler.class, "getExternsRoot", 0).invoke(compiler);
      Node jsRoot = (Node) findMethod(Compiler.class, "getJsRoot", 0).invoke(compiler);

      new ScopedAliases(compiler, null, null).process(externsRoot, jsRoot);

      String output = (String) findMethod(Compiler.class, "toSource", 0).invoke(compiler);
      Object errors = findMethod(Compiler.class, "getErrors", 0).invoke(compiler);
      return new CompilationResult(output, errors);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static Method findMethod(Class<?> type, String name, int parameterCount) {
    Method[] methods = type.getMethods();
    for (Method method : methods) {
      if (method.getName().equals(name) && method.getParameterTypes().length == parameterCount) {
        return method;
      }
    }
    throw new AssertionError("Missing method: " + name);
  }

  private static void assertNoErrors(CompilationResult result) {
    assertEquals(0, Array.getLength(result.errors));
  }

  private static boolean hasError(Object errors, String key) {
    int length = Array.getLength(errors);
    for (int i = 0; i < length; i++) {
      Object error = Array.get(errors, i);
      try {
        Object type = findMethod(error.getClass(), "getType", 0).invoke(error);
        Field keyField = type.getClass().getField("key");
        if (key.equals(keyField.get(type))) {
          return true;
        }
      } catch (Exception ignored) {
        if (String.valueOf(error).contains(key)) {
          return true;
        }
      }
    }
    return false;
  }

  private static String normalize(String source) {
    return source.replaceAll("\\s+", "");
  }

  private static final class CompilationResult {
    final String source;
    final Object errors;

    CompilationResult(String source, Object errors) {
      this.source = source;
      this.errors = errors;
    }
  }
}

package com.google.javascript.jscomp;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class CheckSideEffectsBug21Test {

  @Test
  public void testComparisonResultWithCallIsReported() throws Exception {
    assertWarningCount("x == foo();", 1);
  }

  @Test
  public void testStandaloneLiteralIsReported() throws Exception {
    assertWarningCount("1;", 1);
  }

  @Test
  public void testLikelyMissingStringConcatenationIsReported() throws Exception {
    assertWarningCount(
        "var s = 'this string is '\n"
            + "'continued on the next line';",
        1);
  }

  @Test
  public void testUselessForInitializerIsReported() throws Exception {
    assertWarningCount("for (1;;) {}", 1);
  }

  @Test
  public void testCallStatementIsNotReported() throws Exception {
    assertWarningCount("foo();", 0);
  }

  @Test
  public void testAssignmentStatementIsNotReported() throws Exception {
    assertWarningCount("x = foo();", 0);
  }

  @Test
  public void testEmptyStatementIsNotReported() throws Exception {
    assertWarningCount(";", 0);
  }

  private void assertWarningCount(String source, int expected) throws Exception {
    Assert.assertEquals(expected, getWarningCount(source));
  }

  private int getWarningCount(String source) throws Exception {
    Class<?> compilerClass = Class.forName("com.google.javascript.jscomp.Compiler");
    Class<?> optionsClass = Class.forName("com.google.javascript.jscomp.CompilerOptions");
    Class<?> sourceFileClass = Class.forName("com.google.javascript.jscomp.SourceFile");

    Object compiler = compilerClass.newInstance();
    Object options = optionsClass.newInstance();
    Method fromCode = sourceFileClass.getMethod("fromCode", String.class, String.class);
    Object input = fromCode.invoke(null, "input.js", source);

    Method init = findMethod(compilerClass, "init", 3);
    init.invoke(
        compiler,
        Collections.emptyList(),
        Collections.singletonList(input),
        options);

    Object root = findMethod(compilerClass, "parseInputs", 0).invoke(compiler);

    Constructor<?> constructor = null;
    for (Constructor<?> candidate : CheckSideEffects.class.getDeclaredConstructors()) {
      if (candidate.getParameterTypes().length == 3) {
        constructor = candidate;
        break;
      }
    }
    if (constructor == null) {
      throw new NoSuchMethodException("CheckSideEffects constructor");
    }
    constructor.setAccessible(true);
    Object pass = constructor.newInstance(compiler, CheckLevel.WARNING, false);

    Method process = findMethod(CheckSideEffects.class, "process", 2);
    process.invoke(pass, null, root);

    Object warnings = findMethod(compilerClass, "getWarnings", 0).invoke(compiler);
    return Array.getLength(warnings);
  }

  private Method findMethod(Class<?> type, String name, int parameterCount)
      throws NoSuchMethodException {
    for (Class<?> current = type; current != null; current = current.getSuperclass()) {
      for (Method method : current.getDeclaredMethods()) {
        if (method.getName().equals(name)
            && method.getParameterTypes().length == parameterCount) {
          method.setAccessible(true);
          return method;
        }
      }
    }
    throw new NoSuchMethodException(name);
  }
}

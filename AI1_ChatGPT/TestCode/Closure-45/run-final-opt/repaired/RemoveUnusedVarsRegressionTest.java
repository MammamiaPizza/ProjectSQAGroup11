package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

public class RemoveUnusedVarsRegressionTest extends CompilerTestCase {

  public RemoveUnusedVarsRegressionTest() {
    super("");
    enableNormalize();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new RemoveUnusedVars(compiler, true, false, true);
  }

  @Test
  public void testRemovesUnusedMiddleParameterAndCorrespondingArgument() {
    test(
        "function f(a, b, c) { return a + c; } f(1, 2, 3);",
        "function f(a, c) { return a + c; } f(1, 3);");
  }

  @Test
  public void testRemovesUnusedTrailingParameterAndLiteralArgument() {
    test(
        "function f(a, b) { return a; } f(1, 2);",
        "function f(a) { return a; } f(1);");
  }

  @Test
  public void testKeepsReferencedParameters() {
    testSame("function f(a, b) { return a + b; } f(1, 2);");
  }

  @Test
  public void testDoesNotChangeMiddleParameterWhenFunctionHasNonCallReference() {
    testSame(
        "function f(a, b, c) { return a + c; }"
            + "var alias = f;"
            + "alias(4, 5, 6);"
            + "f(1, 2, 3);");
  }

  @Test
  public void testPreservesSideEffectingExtraArgumentWhenTrailingParameterIsRemoved() {
    test(
        "function f(a, b) { return a; } f(1, sideEffect());",
        "function f(a) { return a; } f(1, sideEffect());");
  }
}

abstract class CompilerTestCase extends TestCase {
  CompilerTestCase(String name) {
    super(name);
  }

  protected void enableNormalize() {
  }

  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected void test(String input, String expected) {
    Assert.assertEquals(compile(expected, false), compile(input, true));
  }

  protected void testSame(String input) {
    test(input, input);
  }

  private String compile(String source, boolean runProcessor) {
    try {
      Compiler compiler = new Compiler();
      Class<?> sourceFileClass = Class.forName("com.google.javascript.jscomp.SourceFile");
      Class<?> optionsClass = Class.forName("com.google.javascript.jscomp.CompilerOptions");
      Object options = optionsClass.newInstance();
      Method fromCode = sourceFileClass.getMethod("fromCode", String.class, String.class);
      Object extern = fromCode.invoke(null, "externs", "");
      Object input = fromCode.invoke(null, "input", source);

      invoke(
          compiler,
          "init",
          new Object[] {
            Collections.singletonList(extern), Collections.singletonList(input), options
          });
      invoke(compiler, "parseInputs", new Object[0]);

      if (runProcessor) {
        Node externsRoot = (Node) invoke(compiler, "getExternsRoot", new Object[0]);
        Node jsRoot = (Node) invoke(compiler, "getJsRoot", new Object[0]);
        getProcessor(compiler).process(externsRoot, jsRoot);
      }

      return (String) invoke(compiler, "toSource", new Object[0]);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private Object invoke(Object target, String name, Object[] arguments) throws Exception {
    Class<?> type = target.getClass();
    while (type != null) {
      Method[] methods = type.getDeclaredMethods();
      for (int i = 0; i < methods.length; i++) {
        Method method = methods[i];
        if (method.getName().equals(name)
            && method.getParameterTypes().length == arguments.length) {
          method.setAccessible(true);
          return method.invoke(target, arguments);
        }
      }
      type = type.getSuperclass();
    }
    throw new NoSuchMethodException(name);
  }
}

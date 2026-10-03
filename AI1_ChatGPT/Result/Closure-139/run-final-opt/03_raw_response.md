package com.google.javascript.jscomp;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class NormalizeRegressionTest {

  private void test(String source, String expected) throws Exception {
    Compiler compiler = new Compiler();

    Class<?> sourceFileClass =
        Class.forName("com.google.javascript.jscomp.SourceFile");
    Method fromCode = findMethod(sourceFileClass, "fromCode", 2);
    Object input = fromCode.invoke(null, "testcode", source);

    Class<?> optionsClass =
        Class.forName("com.google.javascript.jscomp.CompilerOptions");
    Object options = optionsClass.newInstance();

    Method init = findMethod(Compiler.class, "init", 3);
    init.invoke(
        compiler,
        Collections.emptyList(),
        Collections.singletonList(input),
        options);

    Method parseInputs = findMethod(Compiler.class, "parseInputs", 0);
    Object root = parseInputs.invoke(compiler);

    new Normalize(compiler, false).process(null, (com.google.javascript.rhino.Node) root);

    Method getErrors = findMethod(Compiler.class, "getErrors", 0);
    Object errors = getErrors.invoke(compiler);
    Assert.assertEquals(0, Array.getLength(errors));

    Method toSource = findMethod(Compiler.class, "toSource", 1);
    Assert.assertEquals(expected, toSource.invoke(compiler, root));
  }

  private static Method findMethod(Class<?> type, String name, int parameterCount)
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

  @Test
  public void testMovesNamedFunctionDeclarationsBeforeStatements() throws Exception {
    test(
        "function outer(){a();function first(){}b();function second(){}}",
        "function outer(){function first(){}function second(){}a();b();}");
  }

  @Test
  public void testMovesNamedFunctionsInsideNestedFunctionBodies() throws Exception {
    test(
        "function outer(){function inner(){x();function late(){}}y();}",
        "function outer(){function inner(){function late(){}x();}y();}");
  }

  @Test
  public void testFunctionDeclarationAndUninitializedVarDoNotConflict() throws Exception {
    test(
        "function outer(){var f;function f(){return 1;}}",
        "function outer(){function f(){return 1;}}");
  }

  @Test
  public void testFunctionDeclarationAndInitializedVarBecomeAssignment() throws Exception {
    test(
        "function outer(){var f=0;function f(){return 1;}}",
        "function outer(){function f(){return 1;}f=0;}");
  }

  @Test
  public void testSplitsMultipleVarDeclarations() throws Exception {
    test(
        "function f(){var a=1,b=2,c;}",
        "function f(){var a=1;var b=2;var c;}");
  }

  @Test
  public void testDuplicateInitializedVarIsReplacedWithAssignment() throws Exception {
    test(
        "function f(){var a;var a=1;}",
        "function f(){var a;a=1;}");
  }

  @Test
  public void testDuplicateUninitializedVarIsRemoved() throws Exception {
    test(
        "function f(){var a;var a;}",
        "function f(){var a;}");
  }

  @Test
  public void testNormalizesLabelBodyToBlock() throws Exception {
    test("label: work();", "label:{work();}");
  }

  @Test
  public void testExtractsForVarInitializer() throws Exception {
    test(
        "function f(){for(var i=0;i<2;i++){work(i);}}",
        "function f(){var i=0;for(;i<2;i++){work(i);}}");
  }
}
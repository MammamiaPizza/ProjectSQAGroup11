package com.google.javascript.jscomp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import org.junit.Test;

public class Closure138RegressionTest {

  @Test
  public void testGoogIsArrayOnNullNarrowsToArray() throws Throwable {
    runTypeCheckSame(
        "var goog = {};\n"
            + "goog.isArray = function(x) {};\n"
            + "/** @type {null} */ var x = null;\n"
            + "if (goog.isArray(x)) { x.push(1); }\n");
  }

  @Test
  public void testGoogIsFunctionOnNullNarrowsToFunction() throws Throwable {
    runTypeCheckSame(
        "var goog = {};\n"
            + "goog.isFunction = function(x) {};\n"
            + "/** @type {null} */ var x = null;\n"
            + "if (goog.isFunction(x)) { x(); }\n");
  }

  @Test
  public void testGoogIsObjectOnNullNarrowsToObject() throws Throwable {
    runTypeCheckSame(
        "var goog = {};\n"
            + "goog.isObject = function(x) {};\n"
            + "/** @type {null} */ var x = null;\n"
            + "if (goog.isObject(x)) { x.toString(); }\n");
  }

  @Test
  public void testIssue124TypeInferenceDoesNotReportUnexpectedWarnings()
      throws Throwable {
    runLegacyTest(TypeCheckTest.class, "testIssue124");
  }

  @Test
  public void testIssue124bTypeInferencePreservesFalseOutcome() throws Throwable {
    runLegacyTest(TypeCheckTest.class, "testIssue124b");
  }

  private void runTypeCheckSame(String source) throws Throwable {
    TypeCheckHarness test = new TypeCheckHarness(source);
    test.runBare();
  }

  private void runLegacyTest(
      Class<? extends TestCase> testClass, String testMethod) throws Throwable {
    TestCase test = testClass.newInstance();
    test.setName(testMethod);
    test.runBare();
  }

  private static final class TypeCheckHarness extends TypeCheckTest {
    private final String source;

    TypeCheckHarness(String source) {
      this.source = source;
    }

    @Override
    protected void runTest() throws Throwable {
      Method method = findTestSameMethod();
      try {
        method.setAccessible(true);
        method.invoke(this, source);
      } catch (InvocationTargetException e) {
        throw e.getCause();
      }
    }

    private Method findTestSameMethod() {
      for (Class<?> type = getClass(); type != null; type = type.getSuperclass()) {
        Method[] methods = type.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
          Method method = methods[i];
          Class<?>[] parameters = method.getParameterTypes();
          if (method.getName().equals("testSame")
              && parameters.length == 1
              && parameters[0] == String.class) {
            return method;
          }
        }
      }
      throw new AssertionError("Unable to find TypeCheckTest.testSame(String)");
    }
  }
}

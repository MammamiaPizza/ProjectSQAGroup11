package com.google.javascript.jscomp.parsing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collections;
import org.junit.Test;

public class IRFactoryDestructuringAssignmentTest {

  @Test
  public void objectDestructuringAssignmentIsRejected() throws Exception {
    ParseOutcome outcome = parse("({a: b} = value);");

    assertTrue(
        "Object destructuring assignment must be reported as invalid",
        outcome.errorCount > 0);
  }

  @Test
  public void arrayDestructuringAssignmentIsRejected() throws Exception {
    ParseOutcome outcome = parse("[a, b] = value;");

    assertTrue(
        "Array destructuring assignment must be reported as invalid",
        outcome.errorCount > 0);
  }

  @Test
  public void nestedDestructuringAssignmentIsRejected() throws Exception {
    ParseOutcome outcome = parse("({a: [b]} = value);");

    assertTrue(
        "Nested destructuring assignment must be reported as invalid",
        outcome.errorCount > 0);
  }

  @Test
  public void destructuringDeclarationRemainsParseable() throws Exception {
    ParseOutcome outcome = parse("var {a: b} = value;");

    assertNotNull("A valid destructuring declaration should produce a parse result", outcome.result);
    assertTrue(
        "A valid destructuring declaration must not be reported as an error",
        outcome.errorCount == 0);
  }

  @Test
  public void ordinaryPropertyAssignmentRemainsParseable() throws Exception {
    ParseOutcome outcome = parse("({a: b}).a = value;");

    assertNotNull("A valid property assignment should produce a parse result", outcome.result);
    assertTrue(
        "A valid property assignment must not be reported as an error",
        outcome.errorCount == 0);
  }

  private ParseOutcome parse(String source) throws Exception {
    final int[] errors = new int[] {0};
    Class<?> reporterType =
        Class.forName("com.google.javascript.jscomp.mozilla.rhino.ErrorReporter");
    Object reporter =
        Proxy.newProxyInstance(
            reporterType.getClassLoader(),
            new Class<?>[] {reporterType},
            new InvocationHandler() {
              @Override
              public Object invoke(Object proxy, Method method, Object[] args) {
                if ("error".equals(method.getName())) {
                  errors[0]++;
                }
                Class<?> returnType = method.getReturnType();
                if (returnType == Boolean.TYPE) {
                  return false;
                }
                if (returnType == Integer.TYPE) {
                  return 0;
                }
                if (returnType == Long.TYPE) {
                  return 0L;
                }
                if (returnType == Double.TYPE) {
                  return 0.0d;
                }
                if (returnType == Float.TYPE) {
                  return 0.0f;
                }
                if (returnType == Character.TYPE) {
                  return '\0';
                }
                if (returnType == Byte.TYPE) {
                  return (byte) 0;
                }
                if (returnType == Short.TYPE) {
                  return (short) 0;
                }
                return null;
              }
            });

    Class<?> configType = Class.forName("com.google.javascript.jscomp.parsing.Config");
    Object config = createConfig(configType);

    Class<?> parserRunner = Class.forName("com.google.javascript.jscomp.parsing.ParserRunner");
    Method parseMethod = null;
    for (Method method : parserRunner.getDeclaredMethods()) {
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (Modifier.isStatic(method.getModifiers())
          && "parse".equals(method.getName())
          && parameterTypes.length == 4
          && parameterTypes[0] == String.class
          && parameterTypes[1] == String.class
          && parameterTypes[2].isAssignableFrom(configType)
          && parameterTypes[3].isAssignableFrom(reporterType)) {
        parseMethod = method;
        break;
      }
    }

    assertNotNull("ParserRunner must expose its parser entry point", parseMethod);
    parseMethod.setAccessible(true);
    try {
      Object result = parseMethod.invoke(null, source, "destructuring-test.js", config, reporter);
      return new ParseOutcome(result, errors[0]);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      AssertionError failure =
          new AssertionError("Parsing unexpectedly threw " + cause.getClass().getName());
      failure.initCause(cause);
      throw failure;
    }
  }

  private Object createConfig(Class<?> configType) throws Exception {
    for (Constructor<?> constructor : configType.getDeclaredConstructors()) {
      Class<?>[] parameterTypes = constructor.getParameterTypes();
      Object[] arguments = new Object[parameterTypes.length];
      boolean usable = true;

      for (int i = 0; i < parameterTypes.length; i++) {
        Class<?> parameterType = parameterTypes[i];
        if (parameterType.isEnum()) {
          Object languageMode = enumValue(parameterType, "ECMASCRIPT5");
          if (languageMode == null) {
            usable = false;
            break;
          }
          arguments[i] = languageMode;
        } else if (parameterType == Boolean.TYPE || parameterType == Boolean.class) {
          arguments[i] = false;
        } else if (parameterType.isAssignableFrom(Collections.emptySet().getClass())
            || java.util.Set.class.isAssignableFrom(parameterType)) {
          arguments[i] = Collections.emptySet();
        } else {
          usable = false;
          break;
        }
      }

      if (usable) {
        constructor.setAccessible(true);
        return constructor.newInstance(arguments);
      }
    }
    throw new AssertionError("No usable Config constructor was found");
  }

  private Object enumValue(Class<?> enumType, String name) {
    Object[] values = enumType.getEnumConstants();
    for (Object value : values) {
      if (((Enum<?>) value).name().equals(name)) {
        return value;
      }
    }
    return null;
  }

  private static final class ParseOutcome {
    final Object result;
    final int errorCount;

    ParseOutcome(Object result, int errorCount) {
      this.result = result;
      this.errorCount = errorCount;
    }
  }
}

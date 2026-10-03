package com.google.javascript.jscomp.type;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.google.common.base.Function;
import com.google.javascript.jscomp.ClosureCodingConvention;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeNative;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.Test;

public class ClosureReverseAbstractInterpreterArrayRestrictionTest {

  @Test
  public void testGoogIsArrayTrueRestrictsUnknownTypeToArray() throws Exception {
    RestrictionHarness harness = new RestrictionHarness();
    JSType result =
        harness.apply("isArray", harness.registry.getNativeType(JSTypeNative.UNKNOWN_TYPE), true);

    assertSame(harness.registry.getNativeType(JSTypeNative.ARRAY_TYPE), result);
  }

  @Test
  public void testGoogIsArrayTrueRestrictsObjectTypeToArray() throws Exception {
    RestrictionHarness harness = new RestrictionHarness();
    JSType result =
        harness.apply("isArray", harness.registry.getNativeType(JSTypeNative.OBJECT_TYPE), true);

    assertSame(harness.registry.getNativeType(JSTypeNative.ARRAY_TYPE), result);
  }

  @Test
  public void testGoogIsArrayTrueWithNoKnownTypeProducesArray() throws Exception {
    RestrictionHarness harness = new RestrictionHarness();
    JSType result = harness.apply("isArray", null, true);

    assertSame(harness.registry.getNativeType(JSTypeNative.ARRAY_TYPE), result);
  }

  @Test
  public void testGoogIsArrayFalseRejectsKnownArray() throws Exception {
    RestrictionHarness harness = new RestrictionHarness();
    JSType result =
        harness.apply("isArray", harness.registry.getNativeType(JSTypeNative.ARRAY_TYPE), false);

    assertNull(result);
  }

  @Test
  public void testGoogIsArrayFalsePreservesGeneralObjectType() throws Exception {
    RestrictionHarness harness = new RestrictionHarness();
    JSType objectType = harness.registry.getNativeType(JSTypeNative.OBJECT_TYPE);
    JSType result = harness.apply("isArray", objectType, false);

    assertSame(objectType, result);
  }

  private static final class RestrictionHarness {
    final JSTypeRegistry registry;
    private final Map<String, Function<?, ?>> restricters;
    private final Constructor<?> restrictionConstructor;

    @SuppressWarnings("unchecked")
    RestrictionHarness() throws Exception {
      registry = new JSTypeRegistry(null);
      ClosureReverseAbstractInterpreter interpreter =
          new ClosureReverseAbstractInterpreter(new ClosureCodingConvention(), registry);

      Field field =
          ClosureReverseAbstractInterpreter.class.getDeclaredField("restricters");
      field.setAccessible(true);
      restricters = (Map<String, Function<?, ?>>) field.get(interpreter);

      Class<?> restrictionClass =
          Class.forName(
              "com.google.javascript.jscomp.type."
                  + "ClosureReverseAbstractInterpreter$TypeRestriction");
      restrictionConstructor =
          restrictionClass.getDeclaredConstructor(JSType.class, Boolean.TYPE);
      restrictionConstructor.setAccessible(true);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    JSType apply(String functionName, JSType type, boolean outcome) throws Exception {
      Object restriction = restrictionConstructor.newInstance(type, Boolean.valueOf(outcome));
      Function function = restricters.get(functionName);
      return (JSType) function.apply(restriction);
    }
  }
}
package com.google.gson.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import org.junit.Test;

public class GsonTypesResolveRegressionTest {

  private static class Holder<T> {
    List<? super T> lower;
    List<? extends T> upper;
  }

  private static class Contexts {
    Holder<Number> exactNumber;
    Holder<? super Number> lowerNumber;
    Holder<? extends Number> upperNumber;
  }

  private static class Recursive<T> {
    Recursive<? super T> lowerSelf;
    Recursive<? extends T> upperSelf;
  }

  @Test
  public void resolvesTypeVariableToConcreteContextArgument() throws Exception {
    TypeVariable<?> variable = Holder.class.getTypeParameters()[0];
    Type context = Contexts.class.getDeclaredField("exactNumber").getGenericType();

    Type resolved = $Gson$Types.resolve(context, Holder.class, variable);

    assertTypeEquals(Number.class, resolved);
  }

  @Test
  public void resolvesLowerWildcardWithoutDuplicatingLowerBound() throws Exception {
    Type context = Contexts.class.getDeclaredField("lowerNumber").getGenericType();
    Type wildcard = wildcardArgument("lower");

    Type resolved = $Gson$Types.resolve(context, Holder.class, wildcard);

    assertTypeEquals($Gson$Types.supertypeOf(Number.class), resolved);
  }

  @Test
  public void resolvesUpperWildcardWithoutDuplicatingUpperBound() throws Exception {
    Type context = Contexts.class.getDeclaredField("upperNumber").getGenericType();
    Type wildcard = wildcardArgument("upper");

    Type resolved = $Gson$Types.resolve(context, Holder.class, wildcard);

    assertTypeEquals($Gson$Types.subtypeOf(Number.class), resolved);
  }

  @Test
  public void resolvesUpperOfLowerWildcardToUnboundedWildcard() throws Exception {
    Type context = Contexts.class.getDeclaredField("lowerNumber").getGenericType();
    Type wildcard = wildcardArgument("upper");

    Type resolved = $Gson$Types.resolve(context, Holder.class, wildcard);

    assertTypeEquals($Gson$Types.subtypeOf(Object.class), resolved);
  }

  @Test
  public void resolvesLowerOfUpperWildcardToUnboundedWildcard() throws Exception {
    Type context = Contexts.class.getDeclaredField("upperNumber").getGenericType();
    Type wildcard = wildcardArgument("lower");

    Type resolved = $Gson$Types.resolve(context, Holder.class, wildcard);

    assertTypeEquals($Gson$Types.subtypeOf(Object.class), resolved);
  }

  @Test
  public void wildcardFactoriesNormalizeNestedWildcardBounds() {
    Type unbounded = $Gson$Types.subtypeOf(Object.class);

    assertTypeEquals(
        $Gson$Types.supertypeOf(Number.class),
        $Gson$Types.supertypeOf($Gson$Types.supertypeOf(Number.class)));
    assertTypeEquals(
        $Gson$Types.subtypeOf(Number.class),
        $Gson$Types.subtypeOf($Gson$Types.subtypeOf(Number.class)));
    assertTypeEquals(
        unbounded,
        $Gson$Types.subtypeOf($Gson$Types.supertypeOf(Number.class)));
    assertTypeEquals(
        unbounded,
        $Gson$Types.supertypeOf($Gson$Types.subtypeOf(Number.class)));
  }

  @Test(timeout = 2000)
  public void resolvesSelfReferentialLowerWildcardWithoutRecursionOverflow() throws Exception {
    ParameterizedType context = (ParameterizedType)
        Recursive.class.getDeclaredField("lowerSelf").getGenericType();
    TypeVariable<?> variable = Recursive.class.getTypeParameters()[0];
    Type expected = context.getActualTypeArguments()[0];

    Type resolved = $Gson$Types.resolve(context, Recursive.class, variable);

    assertNotNull(resolved);
    assertTypeEquals(expected, resolved);
  }

  @Test(timeout = 2000)
  public void resolvesSelfReferentialUpperWildcardWithoutRecursionOverflow() throws Exception {
    ParameterizedType context = (ParameterizedType)
        Recursive.class.getDeclaredField("upperSelf").getGenericType();
    TypeVariable<?> variable = Recursive.class.getTypeParameters()[0];
    Type expected = context.getActualTypeArguments()[0];

    Type resolved = $Gson$Types.resolve(context, Recursive.class, variable);

    assertNotNull(resolved);
    assertTypeEquals(expected, resolved);
  }

  private static Type wildcardArgument(String fieldName) throws Exception {
    ParameterizedType listType = (ParameterizedType)
        Holder.class.getDeclaredField(fieldName).getGenericType();
    return listType.getActualTypeArguments()[0];
  }

  private static void assertTypeEquals(Type expected, Type actual) {
    assertTrue(
        "Expected <" + expected + "> but was <" + actual + ">",
        $Gson$Types.equals(expected, actual));
  }
}

@org.junit.Test
public void shouldExpandVarArgsAndPreserveRawArguments() {
    org.mockito.internal.invocation.MockitoMethod method =
            org.mockito.Mockito.mock(org.mockito.internal.invocation.MockitoMethod.class);
    org.mockito.Mockito.when(method.isVarArgs()).thenReturn(true);

    Object[] rawArguments = new Object[] { "prefix", new String[] { "one", "two" } };
    Invocation invocation = new Invocation(new Object(), method, rawArguments, 17, null);

    org.junit.Assert.assertSame(rawArguments, invocation.getRawArguments());
    org.junit.Assert.assertArrayEquals(
            new Object[] { "prefix", "one", "two" }, invocation.getArguments());
    org.junit.Assert.assertEquals(3, invocation.getArgumentsCount());

    Object[] nullVarArgArray = new Object[] { "prefix", null };
    Invocation invocationWithNullVarArgArray =
            new Invocation(new Object(), method, nullVarArgArray, 18, null);

    org.junit.Assert.assertSame(nullVarArgArray, invocationWithNullVarArgArray.getRawArguments());
    org.junit.Assert.assertArrayEquals(
            new Object[] { "prefix", null }, invocationWithNullVarArgArray.getArguments());
    org.junit.Assert.assertEquals(2, invocationWithNullVarArgArray.getArgumentsCount());
}

@org.junit.Test
public void shouldCreateArrayAndRegularMatchersForArguments() {
    org.mockito.internal.invocation.MockitoMethod method =
            org.mockito.Mockito.mock(org.mockito.internal.invocation.MockitoMethod.class);
    org.mockito.Mockito.when(method.isVarArgs()).thenReturn(false);

    Invocation invocation = new Invocation(
            new Object(), method, new Object[] { new int[] { 1, 2 }, null }, 1, null);

    java.util.List<?> matchers = invocation.argumentsToMatchers();

    org.junit.Assert.assertEquals(2, matchers.size());
    org.junit.Assert.assertEquals("ArrayEquals", matchers.get(0).getClass().getSimpleName());
    org.junit.Assert.assertEquals("Equals", matchers.get(1).getClass().getSimpleName());
}

@org.junit.Test
public void shouldCompareInvocationsUsingMockMethodAndArguments() {
    org.mockito.internal.invocation.MockitoMethod method =
            org.mockito.Mockito.mock(org.mockito.internal.invocation.MockitoMethod.class);
    org.mockito.Mockito.when(method.isVarArgs()).thenReturn(false);
    Object mock = new Object();

    Invocation invocation = new Invocation(mock, method, new Object[] { "value" }, 1, null);
    Invocation equivalent = new Invocation(mock, method, new Object[] { "value" }, 2, null);
    Invocation differentArguments = new Invocation(mock, method, new Object[] { "other" }, 1, null);

    org.junit.Assert.assertTrue(invocation.equals(equivalent));
    org.junit.Assert.assertFalse(invocation.equals(differentArguments));
    org.junit.Assert.assertFalse(invocation.equals(null));
    org.junit.Assert.assertFalse(invocation.equals(new Object()));
}
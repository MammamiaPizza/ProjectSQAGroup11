@Test
public void shouldNotMatchInvocationOnDifferentMock() throws Exception {
    java.lang.reflect.Method method = java.lang.String.class.getMethod("valueOf", java.lang.Object.class);
    org.mockito.internal.invocation.InvocationMatcher matcher =
            new org.mockito.internal.invocation.InvocationMatcher(
                    newInvocationWith(method, new Object(), new Object[] { "value" }, false));

    assertFalse(matcher.matches(
            newInvocationWith(method, new Object(), new Object[] { "value" }, false)));
}

@Test
public void shouldNotConsiderOverloadedMethodWithMatchingArgumentsSimilar() throws Exception {
    java.lang.reflect.Method wantedMethod =
            java.lang.String.class.getMethod("valueOf", java.lang.Object.class);
    java.lang.reflect.Method candidateMethod =
            java.lang.String.class.getMethod("valueOf", char[].class);
    Object mock = new Object();

    org.mockito.internal.invocation.InvocationMatcher matcher =
            new org.mockito.internal.invocation.InvocationMatcher(
                    newInvocationWith(wantedMethod, mock, new Object[] { null }, false));

    assertFalse(matcher.hasSimilarMethod(
            newInvocationWith(candidateMethod, mock, new Object[] { null }, false)));
}

@Test
public void shouldConsiderOverloadedMethodWithDifferentArgumentCountsSimilar() throws Exception {
    java.lang.reflect.Method wantedMethod =
            java.io.PrintStream.class.getMethod("println");
    java.lang.reflect.Method candidateMethod =
            java.io.PrintStream.class.getMethod("println", java.lang.String.class);
    Object mock = new Object();

    org.mockito.internal.invocation.InvocationMatcher matcher =
            new org.mockito.internal.invocation.InvocationMatcher(
                    newInvocationWith(wantedMethod, mock, new Object[0], false));

    assertTrue(matcher.hasSimilarMethod(
            newInvocationWith(candidateMethod, mock, new Object[] { "value" }, false)));
}

@Test
public void shouldNotConsiderVerifiedInvocationSimilar() throws Exception {
    java.lang.reflect.Method method =
            java.lang.String.class.getMethod("valueOf", java.lang.Object.class);
    Object mock = new Object();

    org.mockito.internal.invocation.InvocationMatcher matcher =
            new org.mockito.internal.invocation.InvocationMatcher(
                    newInvocationWith(method, mock, new Object[] { "value" }, false));

    assertFalse(matcher.hasSimilarMethod(
            newInvocationWith(method, mock, new Object[] { "value" }, true)));
}

private org.mockito.internal.invocation.Invocation newInvocationWith(
        java.lang.reflect.Method method, Object mock, Object[] arguments, boolean verified) {
    org.mockito.internal.invocation.Invocation invocation =
            org.mockito.Mockito.mock(org.mockito.internal.invocation.Invocation.class);
    org.mockito.Mockito.when(invocation.getMethod()).thenReturn(method);
    org.mockito.Mockito.when(invocation.getMock()).thenReturn(mock);
    org.mockito.Mockito.when(invocation.getArguments()).thenReturn(arguments);
    org.mockito.Mockito.when(invocation.isVerified()).thenReturn(verified);
    return invocation;
}
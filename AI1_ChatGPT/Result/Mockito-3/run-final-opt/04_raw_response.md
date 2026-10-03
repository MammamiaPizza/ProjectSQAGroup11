@org.junit.Test
public void defaultConstructorExposesInvocationMethodAndLocation() throws Exception {
    org.mockito.invocation.Invocation invocation =
            org.mockito.Mockito.mock(org.mockito.invocation.Invocation.class);
    java.lang.reflect.Method method = String.class.getMethod("isEmpty");
    org.mockito.invocation.Location location =
            org.mockito.Mockito.mock(org.mockito.invocation.Location.class);

    org.mockito.Mockito.when(invocation.getMethod()).thenReturn(method);
    org.mockito.Mockito.when(invocation.getLocation()).thenReturn(location);

    InvocationMatcher matcher = new InvocationMatcher(invocation);

    org.junit.Assert.assertSame(invocation, matcher.getInvocation());
    org.junit.Assert.assertSame(method, matcher.getMethod());
    org.junit.Assert.assertSame(location, matcher.getLocation());
    org.junit.Assert.assertTrue(matcher.getMatchers().isEmpty());
}

@org.junit.Test
public void hasSameMethodDistinguishesOverloadedMethods() throws Exception {
    org.mockito.invocation.Invocation wanted =
            org.mockito.Mockito.mock(org.mockito.invocation.Invocation.class);
    org.mockito.invocation.Invocation sameMethod =
            org.mockito.Mockito.mock(org.mockito.invocation.Invocation.class);
    org.mockito.invocation.Invocation overloadedMethod =
            org.mockito.Mockito.mock(org.mockito.invocation.Invocation.class);

    java.lang.reflect.Method intIndexOf = String.class.getMethod("indexOf", Integer.TYPE);
    java.lang.reflect.Method stringIndexOf = String.class.getMethod("indexOf", String.class);

    org.mockito.Mockito.when(wanted.getMethod()).thenReturn(intIndexOf);
    org.mockito.Mockito.when(sameMethod.getMethod()).thenReturn(intIndexOf);
    org.mockito.Mockito.when(overloadedMethod.getMethod()).thenReturn(stringIndexOf);

    InvocationMatcher matcher = new InvocationMatcher(wanted);

    org.junit.Assert.assertTrue(matcher.hasSameMethod(sameMethod));
    org.junit.Assert.assertFalse(matcher.hasSameMethod(overloadedMethod));
}

@org.junit.Test
public void hasSimilarMethodRequiresSameNameUnverifiedInvocationAndSameMock() throws Exception {
    org.mockito.invocation.Invocation wanted =
            org.mockito.Mockito.mock(org.mockito.invocation.Invocation.class);
    org.mockito.invocation.Invocation candidate =
            org.mockito.Mockito.mock(org.mockito.invocation.Invocation.class);
    Object mock = new Object();
    java.lang.reflect.Method method = String.class.getMethod("isEmpty");
    java.lang.reflect.Method differentName = String.class.getMethod("length");

    org.mockito.Mockito.when(wanted.getMethod()).thenReturn(method);
    org.mockito.Mockito.when(wanted.getMock()).thenReturn(mock);
    org.mockito.Mockito.when(candidate.getMethod()).thenReturn(method);
    org.mockito.Mockito.when(candidate.getMock()).thenReturn(mock);
    org.mockito.Mockito.when(candidate.isVerified()).thenReturn(false);

    InvocationMatcher matcher = new InvocationMatcher(wanted);

    org.junit.Assert.assertTrue(matcher.hasSimilarMethod(candidate));

    org.mockito.Mockito.when(candidate.getMethod()).thenReturn(differentName);
    org.junit.Assert.assertFalse(matcher.hasSimilarMethod(candidate));

    org.mockito.Mockito.when(candidate.getMethod()).thenReturn(method);
    org.mockito.Mockito.when(candidate.isVerified()).thenReturn(true);
    org.junit.Assert.assertFalse(matcher.hasSimilarMethod(candidate));

    org.mockito.Mockito.when(candidate.isVerified()).thenReturn(false);
    org.mockito.Mockito.when(candidate.getMock()).thenReturn(new Object());
    org.junit.Assert.assertFalse(matcher.hasSimilarMethod(candidate));
}

@org.junit.Test
public void doesNotMatchInvocationFromAnotherMock() {
    org.mockito.invocation.Invocation wanted =
            org.mockito.Mockito.mock(org.mockito.invocation.Invocation.class);
    org.mockito.invocation.Invocation actual =
            org.mockito.Mockito.mock(org.mockito.invocation.Invocation.class);

    org.mockito.Mockito.when(wanted.getMock()).thenReturn(new Object());
    org.mockito.Mockito.when(actual.getMock()).thenReturn(new Object());

    org.junit.Assert.assertFalse(new InvocationMatcher(wanted).matches(actual));
}
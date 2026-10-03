@org.junit.Test
public void shouldSupportDoAnswerStyleVoidStubbing() throws Throwable {
    java.util.List mock = org.mockito.Mockito.mock(java.util.List.class);
    final int[] calls = new int[1];

    org.mockito.Mockito.doAnswer(new org.mockito.stubbing.Answer<Void>() {
        public Void answer(org.mockito.invocation.InvocationOnMock invocation) {
            calls[0]++;
            return null;
        }
    }).when(mock).clear();

    mock.clear();

    org.junit.Assert.assertEquals(1, calls[0]);
    org.mockito.Mockito.verify(mock).clear();
}

@org.junit.Test
public void shouldUseDefaultAnswerAndThenReturnRegularStubbedAnswer() {
    java.util.List mock = org.mockito.Mockito.mock(java.util.List.class,
            new org.mockito.stubbing.Answer<Object>() {
                public Object answer(org.mockito.invocation.InvocationOnMock invocation) {
                    return "fallback";
                }
            });

    org.junit.Assert.assertEquals("fallback", mock.get(0));

    org.mockito.Mockito.when(mock.get(1)).thenReturn("stubbed");

    org.junit.Assert.assertEquals("stubbed", mock.get(1));
}

@org.junit.Test
public void shouldReuseSettingsWhenConstructedFromAnotherHandler() {
    MockHandler<Object> original = new MockHandler<Object>();
    MockHandler<Object> replacement = new MockHandler<Object>(original);

    org.junit.Assert.assertSame(original.getMockSettings(), replacement.getMockSettings());
    org.junit.Assert.assertNotNull(replacement.getInvocationContainer());
}
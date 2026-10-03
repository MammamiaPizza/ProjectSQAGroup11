@org.junit.Test
public void shouldResetStubbingOnAMock() {
    java.util.List<String> list = org.mockito.Mockito.mock(java.util.List.class);

    org.mockito.Mockito.when(list.size()).thenReturn(4);
    org.junit.Assert.assertEquals(4, list.size());

    org.mockito.Mockito.reset(list);

    org.junit.Assert.assertEquals(0, list.size());
}

@org.junit.Test(expected = IllegalStateException.class)
public void shouldThrowExceptionConfiguredForVoidMethod() {
    java.util.List<String> list = org.mockito.Mockito.mock(java.util.List.class);

    org.mockito.Mockito.doThrow(new IllegalStateException()).when(list).clear();

    list.clear();
}

@org.junit.Test
public void shouldSupportVerificationModesForRecordedInvocations() {
    java.util.List<String> list = org.mockito.Mockito.mock(java.util.List.class);

    list.add("first");
    list.add("second");

    org.mockito.Mockito.verify(list, org.mockito.Mockito.atLeastOnce()).add("first");
    org.mockito.Mockito.verify(list, org.mockito.Mockito.atLeast(1)).add("first");
    org.mockito.Mockito.verify(list, org.mockito.Mockito.atMost(1)).add("second");
    org.mockito.Mockito.verify(list, org.mockito.Mockito.never()).add("missing");
}

@org.junit.Test
public void shouldCallRealMethodsOnSpy() {
    java.util.ArrayList<String> list = org.mockito.Mockito.spy(new java.util.ArrayList<String>());

    list.add("value");

    org.junit.Assert.assertEquals(1, list.size());
    org.mockito.Mockito.verify(list).add("value");
}
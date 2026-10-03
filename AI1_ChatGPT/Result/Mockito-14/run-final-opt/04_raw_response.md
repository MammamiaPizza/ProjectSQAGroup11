@org.junit.Test
public void shouldReturnValueConfiguredWithWhen() {
    java.util.List<String> mock = org.mockito.Mockito.mock(java.util.List.class);

    org.mockito.Mockito.when(mock.get(0)).thenReturn("stubbed");

    org.junit.Assert.assertEquals("stubbed", mock.get(0));
}

@org.junit.Test
public void shouldResetRecordedInteractions() {
    java.util.List<String> mock = org.mockito.Mockito.mock(java.util.List.class);
    mock.add("one");

    org.mockito.Mockito.reset(mock);

    org.mockito.Mockito.verifyNoMoreInteractions(mock);
}

@org.junit.Test(expected = org.mockito.exceptions.verification.NoInteractionsWanted.class)
public void shouldReportUnverifiedInteractionsWhenCheckingForNoMoreInteractions() {
    java.util.List<String> mock = org.mockito.Mockito.mock(java.util.List.class);
    mock.add("one");

    org.mockito.Mockito.verifyNoMoreInteractions(mock);
}

@org.junit.Test
public void shouldVerifyInvocationsAcrossMocksInOrder() {
    java.util.List<String> first = org.mockito.Mockito.mock(java.util.List.class);
    java.util.List<String> second = org.mockito.Mockito.mock(java.util.List.class);
    first.add("first");
    second.add("second");

    org.mockito.InOrder inOrder = org.mockito.Mockito.inOrder(first, second);
    inOrder.verify(first).add("first");
    inOrder.verify(second).add("second");
}
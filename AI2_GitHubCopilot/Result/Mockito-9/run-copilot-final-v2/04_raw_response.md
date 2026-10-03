@Test
public void shouldReturnStubbedValueForAbstractMethod() {
    when(mock.getString()).thenReturn("stubbed");
    assertEquals("stubbed", mock.getString());
}

@Test
public void shouldReturnStubbedValueForAbstractIntMethod() {
    when(mock.getInt()).thenReturn(1);
    assertEquals(1, mock.getInt());
}

@Test
public void shouldReturnStubbedValueForInterfaceMethod() {
    java.util.List listMock = mock(java.util.List.class, withSettings().defaultAnswer(new
CallsRealMethods()));
    when(listMock.size()).thenReturn(42);
    assertEquals(42, listMock.size());
}

@Test
public void shouldStubAbstractVoidMethodWithoutException() {
    doNothing().when(mock).voidMethod();
    mock.voidMethod();
    // no exception expected
}
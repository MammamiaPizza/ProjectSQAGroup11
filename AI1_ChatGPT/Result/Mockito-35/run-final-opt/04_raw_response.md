@org.junit.Test
public void shouldAllowIsAIntegerWhenPassedToPrimitiveArgument() {
    java.util.List<String> list = org.mockito.Mockito.mock(java.util.List.class);

    org.mockito.Mockito.when(list.get(org.mockito.Matchers.isA(Integer.class))).thenReturn("matched");

    org.junit.Assert.assertEquals("matched", list.get(123));
}
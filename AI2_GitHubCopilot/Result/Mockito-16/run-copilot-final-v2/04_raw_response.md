@Test
    public void shouldCreateSpyWithDelegatedBehavior() {
        java.util.List<String> real = new java.util.ArrayList<String>();
        java.util.List<String> spy = org.mockito.Mockito.spy(real);
        spy.add("a");
        org.junit.Assert.assertEquals(1, spy.size());
    }

 @Test
 public void shouldResetMockInteractions() {
     java.util.List<String> mock = org.mockito.Mockito.mock(java.util.List.class);
     mock.add("x");
     org.mockito.Mockito.reset(mock);
     org.mockito.Mockito.verifyNoMoreInteractions(mock);
 }

 @Test
 public void shouldStubWithDoReturn() {
     java.util.List<String> mock = org.mockito.Mockito.mock(java.util.List.class);
     org.mockito.Mockito.doReturn("fixed").when(mock).get(0);
     org.junit.Assert.assertEquals("fixed", mock.get(0));
 }

 @Test
 public void shouldStubWithDoThrow() {
     java.util.List<String> mock = org.mockito.Mockito.mock(java.util.List.class);
     org.mockito.Mockito.doThrow(new RuntimeException("bang")).when(mock).clear();
     try {
         mock.clear();
         org.junit.Assert.fail("expected RuntimeException");
     } catch (RuntimeException e) {
         org.junit.Assert.assertEquals("bang", e.getMessage());
     }
 }
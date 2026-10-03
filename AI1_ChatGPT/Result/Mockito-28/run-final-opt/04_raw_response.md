@Test(expected = org.mockito.exceptions.base.MockitoException.class)
public void shouldReportFailureWhenInjectMocksFieldCannotBeInitialized() throws Exception {
    Object holder = new Object() {
        @org.mockito.InjectMocks
        java.lang.Runnable runnable;
    };

    java.util.Set<java.lang.reflect.Field> fields = new java.util.HashSet<java.lang.reflect.Field>();
    fields.add(holder.getClass().getDeclaredField("runnable"));

    new DefaultInjectionEngine().injectMocksOnFields(
            fields,
            java.util.Collections.<Object>emptySet(),
            holder);
}
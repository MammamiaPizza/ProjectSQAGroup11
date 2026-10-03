@Test(expected = org.mockito.exceptions.base.MockitoException.class)
public void shouldThrowExceptionWhenCannotInstantiateInjectMocksField() throws Exception {
    class HasUninstantiableInjectMocks {
        @org.mockito.InjectMocks
        java.io.Serializable serializableField;
    }
    java.lang.reflect.Field field =
HasUninstantiableInjectMocks.class.getDeclaredField("serializableField");
    java.util.Set<java.lang.reflect.Field> injectMocksFields = new
java.util.HashSet<java.lang.reflect.Field>();
    injectMocksFields.add(field);
    new DefaultInjectionEngine().injectMocksOnFields(injectMocksFields, new
java.util.HashSet<Object>(), new HasUninstantiableInjectMocks());
}
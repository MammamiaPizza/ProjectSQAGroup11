@Test
public void shouldReturnObjectForWildcardGenericArgument() throws Exception {
    class Holder {
        java.util.List<? extends Number> values;
    }

    java.lang.reflect.Field field = Holder.class.getDeclaredField("values");

    org.junit.Assert.assertEquals(Object.class, new GenericMaster().getGenericType(field));
}

@Test
public void shouldReturnObjectForTypeVariableGenericArgument() throws Exception {
    class Holder<T> {
        java.util.List<T> values;
    }

    java.lang.reflect.Field field = Holder.class.getDeclaredField("values");

    org.junit.Assert.assertEquals(Object.class, new GenericMaster().getGenericType(field));
}
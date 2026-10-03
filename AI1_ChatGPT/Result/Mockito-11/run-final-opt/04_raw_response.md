@Test
public void isAbstractReturnsTrueForInterfaceMethod() throws Exception {
    java.lang.reflect.Method method = java.util.List.class.getMethod("size");
    assertTrue(new DelegatingMethod(method).isAbstract());
}
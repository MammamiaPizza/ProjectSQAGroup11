@Test
    public void getters_should_delegate_to_wrapped_method() throws Exception {
        java.lang.reflect.Method method = String.class.getMethod("length");
        DelegatingMethod dm = new DelegatingMethod(method);
        assertEquals(method.getName(), dm.getName());
        assertSame(method, dm.getJavaMethod());
        assertEquals(method.getReturnType(), dm.getReturnType());
        assertArrayEquals(method.getParameterTypes(), dm.getParameterTypes());
        assertArrayEquals(method.getExceptionTypes(), dm.getExceptionTypes());
    }

 @Test
 public void isVarArgs_should_delegate_to_wrapped_method() throws Exception {
     java.lang.reflect.Method varArgs = String.class.getMethod("format", String.class,
Object[].class);
     java.lang.reflect.Method nonVarArgs = String.class.getMethod("length");
     assertTrue(new DelegatingMethod(varArgs).isVarArgs());
     assertFalse(new DelegatingMethod(nonVarArgs).isVarArgs());
 }

 @Test
 public void isAbstract_should_return_false_for_concrete_method() throws Exception {
     java.lang.reflect.Method method = String.class.getMethod("length");
     assertFalse(new DelegatingMethod(method).isAbstract());
 }

 @Test
 public void isAbstract_should_return_true_for_abstract_method() throws Exception {
     java.lang.reflect.Method method = Runnable.class.getMethod("run");
     assertTrue(new DelegatingMethod(method).isAbstract());
 }
@Test
 public void should_return_default_name_for_toString() throws Exception {
     Object mock = new Object();
     InvocationOnMock invocation = mock(InvocationOnMock.class);
     when(invocation.getMock()).thenReturn(mock);
     when(invocation.getMethod()).thenReturn(Object.class.getMethod("toString"));
     when(methodsGuru.isToString(Object.class.getMethod("toString"))).thenReturn(true);

     MockName mockName = mock(MockName.class);
     when(mockName.isDefault()).thenReturn(true);
     when(mockUtil.getMockName(mock)).thenReturn(mockName);

     MockSettings settings = mock(MockSettings.class);
     when(settings.getTypeToMock()).thenReturn((Class) Object.class);
     when(mockUtil.getMockSettings(mock)).thenReturn(settings);

     Object result = returnsEmptyValues.answer(invocation);

     assertNotNull(result);
     assertTrue(result instanceof String);
     assertEquals("Mock for Object, hashCode: " + mock.hashCode(), result);
 }

 @Test
 public void should_return_configured_name_for_toString() throws Exception {
     Object mock = new Object();
     InvocationOnMock invocation = mock(InvocationOnMock.class);
     when(invocation.getMock()).thenReturn(mock);
     when(invocation.getMethod()).thenReturn(Object.class.getMethod("toString"));
     when(methodsGuru.isToString(Object.class.getMethod("toString"))).thenReturn(true);

     MockName mockName = mock(MockName.class);
     when(mockName.isDefault()).thenReturn(false);
     when(mockName.toString()).thenReturn("myMock");
     when(mockUtil.getMockName(mock)).thenReturn(mockName);

     Object result = returnsEmptyValues.answer(invocation);

     assertEquals("myMock", result);
 }

 @Test
 public void should_return_zero_when_compareTo_same_reference() throws Exception {
     InvocationOnMock invocation = mock(InvocationOnMock.class);
     Object mock = new Object();
     when(invocation.getMock()).thenReturn(mock);
     when(invocation.getMethod()).thenReturn(Comparable.class.getMethod("compareTo", Object.class));
     when(invocation.getArguments()).thenReturn(new Object[]{mock});
     when(methodsGuru.isCompareToMethod(any(Method.class))).thenReturn(true);

     Object result = returnsEmptyValues.answer(invocation);

     assertEquals(0, result);
 }

 @Test
 public void should_return_one_when_compareTo_different_reference() throws Exception {
     InvocationOnMock invocation = mock(InvocationOnMock.class);
     Object mock = new Object();
     Object other = new Object();
     when(invocation.getMock()).thenReturn(mock);
     when(invocation.getMethod()).thenReturn(Comparable.class.getMethod("compareTo", Object.class));
     when(invocation.getArguments()).thenReturn(new Object[]{other});
     when(methodsGuru.isCompareToMethod(any(Method.class))).thenReturn(true);

     Object result = returnsEmptyValues.answer(invocation);

     assertEquals(1, result);
 }
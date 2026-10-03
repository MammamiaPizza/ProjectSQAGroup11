@Test
public void should_verify_multiple_deep_stubbed_invocations() {
    java.util.Map<?, ?> map = org.mockito.Mockito.mock(
            java.util.Map.class,
            org.mockito.Mockito.RETURNS_DEEP_STUBS);

    map.keySet().iterator();
    map.values().iterator();

    org.mockito.Mockito.verify(map).values().iterator();
    org.mockito.Mockito.verify(map).keySet().iterator();
}
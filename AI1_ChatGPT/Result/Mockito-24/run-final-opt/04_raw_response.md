@Test
public void shouldReturnOneWhenComparableMockIsComparedToAnotherReference() {
    java.lang.Comparable comparable = org.mockito.Mockito.mock(java.lang.Comparable.class, new ReturnsEmptyValues());
    java.lang.Comparable other = org.mockito.Mockito.mock(java.lang.Comparable.class, new ReturnsEmptyValues());

    org.junit.Assert.assertEquals(1, comparable.compareTo(other));
}

@Test
public void shouldDescribeDefaultNamedMocksUsingTheirTypeAndHashCode() {
    java.util.List mock = org.mockito.Mockito.mock(java.util.List.class, new ReturnsEmptyValues());

    org.junit.Assert.assertEquals("Mock for List, hashCode: " + mock.hashCode(), mock.toString());
}

@Test
public void shouldDescribeExplicitlyNamedMocksUsingTheirName() {
    java.util.List mock = org.mockito.Mockito.mock(
            java.util.List.class,
            org.mockito.Mockito.withSettings().name("named-list").defaultAnswer(new ReturnsEmptyValues()));

    org.junit.Assert.assertEquals("named-list", mock.toString());
}

@Test
public void shouldReturnMutableEmptyCollectionsForMapViewMethods() {
    java.util.Map map = org.mockito.Mockito.mock(java.util.Map.class, new ReturnsEmptyValues());

    java.util.Collection values = map.values();
    java.util.Set keys = map.keySet();
    java.util.Set entries = map.entrySet();

    org.junit.Assert.assertEquals(java.util.LinkedList.class, values.getClass());
    org.junit.Assert.assertEquals(java.util.HashSet.class, keys.getClass());
    org.junit.Assert.assertEquals(java.util.HashSet.class, entries.getClass());
    org.junit.Assert.assertTrue(values.add(new Object()));
    org.junit.Assert.assertTrue(keys.add(new Object()));
    org.junit.Assert.assertTrue(entries.add(new Object()));
}
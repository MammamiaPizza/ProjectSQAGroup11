@org.junit.Test
public void shouldDescribeDefaultNamedMockInToString() {
    java.util.List<?> mock = org.mockito.Mockito.mock(java.util.List.class, new ReturnsEmptyValues());

    org.junit.Assert.assertEquals("Mock for List, hashCode: " + mock.hashCode(), mock.toString());
}

@org.junit.Test
public void shouldDescribeExplicitlyNamedMockInToString() {
    java.util.List<?> mock = org.mockito.Mockito.mock(
            java.util.List.class,
            org.mockito.Mockito.withSettings()
                    .name("custom list")
                    .defaultAnswer(new ReturnsEmptyValues()));

    org.junit.Assert.assertEquals("custom list", mock.toString());
}

@org.junit.Test
public void shouldReturnZeroOnlyWhenComparableMockIsComparedWithItself() {
    java.lang.Comparable mock = org.mockito.Mockito.mock(java.lang.Comparable.class, new ReturnsEmptyValues());

    org.junit.Assert.assertEquals(0, mock.compareTo(mock));
    org.junit.Assert.assertEquals(1, mock.compareTo(new Object()));
}
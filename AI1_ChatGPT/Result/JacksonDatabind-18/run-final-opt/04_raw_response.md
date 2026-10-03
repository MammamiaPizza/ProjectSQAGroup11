@Test
public void readAllWithoutTargetCollectsRootValues() throws Exception {
    com.fasterxml.jackson.databind.MappingIterator<java.lang.Integer> iterator =
            new com.fasterxml.jackson.databind.ObjectMapper().reader(java.lang.Integer.class)
                    .readValues("1 2 3");

    java.util.List<java.lang.Integer> values = iterator.readAll();

    assertEquals(java.util.Arrays.asList(
            java.lang.Integer.valueOf(1),
            java.lang.Integer.valueOf(2),
            java.lang.Integer.valueOf(3)), values);
    assertFalse(iterator.hasNextValue());
}

@Test
public void readAllAddsValuesToGenericCollection() throws Exception {
    com.fasterxml.jackson.databind.MappingIterator<java.lang.Integer> iterator =
            new com.fasterxml.jackson.databind.ObjectMapper().reader(java.lang.Integer.class)
                    .readValues("1 2 2 3");
    java.util.Collection<java.lang.Integer> values =
            new java.util.LinkedHashSet<java.lang.Integer>();

    iterator.readAll(values);

    assertEquals(java.util.Arrays.asList(
            java.lang.Integer.valueOf(1),
            java.lang.Integer.valueOf(2),
            java.lang.Integer.valueOf(3)),
            new java.util.ArrayList<java.lang.Integer>(values));
}

@Test
public void closedIteratorHasNoValuesAndNextFails() throws Exception {
    com.fasterxml.jackson.databind.MappingIterator<java.lang.Integer> iterator =
            new com.fasterxml.jackson.databind.ObjectMapper().reader(java.lang.Integer.class)
                    .readValues("1");

    iterator.close();

    assertFalse(iterator.hasNextValue());
    assertFalse(iterator.hasNext());
    try {
        iterator.next();
        fail("Expected next on a closed iterator to fail");
    } catch (java.util.NoSuchElementException e) {
        // expected
    }
}

@Test
public void removeIsUnsupported() throws Exception {
    com.fasterxml.jackson.databind.MappingIterator<java.lang.Integer> iterator =
            new com.fasterxml.jackson.databind.ObjectMapper().reader(java.lang.Integer.class)
                    .readValues("1");

    try {
        iterator.remove();
        fail("Expected remove to be unsupported");
    } catch (java.lang.UnsupportedOperationException e) {
        // expected
    }
}
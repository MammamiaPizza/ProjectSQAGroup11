@org.junit.Test
public void anyClassMatcherShouldMatchTypedNonNullValuesButNotNull() {
    java.util.Map<Object, String> map = org.mockito.Mockito.mock(java.util.Map.class);

    org.mockito.Mockito.when(map.get(org.mockito.Matchers.any(String.class))).thenReturn("matched");

    org.junit.Assert.assertEquals("matched", map.get("key"));
    org.junit.Assert.assertNull(map.get(null));
}

@org.junit.Test
public void genericAnyMatcherShouldMatchNullAndNonNullValues() {
    java.util.Map<Object, String> map = org.mockito.Mockito.mock(java.util.Map.class);

    org.mockito.Mockito.when(map.get(org.mockito.Matchers.<Object>any())).thenReturn("matched");

    org.junit.Assert.assertEquals("matched", map.get("key"));
    org.junit.Assert.assertEquals("matched", map.get(null));
}

@org.junit.Test
public void genericCollectionMatcherAliasesShouldNotMatchNull() {
    java.util.List<String> list = org.mockito.Mockito.mock(java.util.List.class);
    org.mockito.Mockito.doThrow(new RuntimeException()).when(list)
            .addAll(org.mockito.Matchers.<String>anyListOf(String.class));
    try {
        list.addAll(new java.util.ArrayList<String>());
        org.junit.Assert.fail();
    } catch (RuntimeException expected) {
    }
    list.addAll(null);

    java.util.Set<String> set = org.mockito.Mockito.mock(java.util.Set.class);
    org.mockito.Mockito.doThrow(new RuntimeException()).when(set)
            .addAll(org.mockito.Matchers.<String>anySetOf(String.class));
    try {
        set.addAll(new java.util.HashSet<String>());
        org.junit.Assert.fail();
    } catch (RuntimeException expected) {
    }
    set.addAll(null);

    java.util.List<String> collection = org.mockito.Mockito.mock(java.util.List.class);
    org.mockito.Mockito.doThrow(new RuntimeException()).when(collection)
            .addAll(org.mockito.Matchers.<String>anyCollectionOf(String.class));
    try {
        collection.addAll(new java.util.ArrayList<String>());
        org.junit.Assert.fail();
    } catch (RuntimeException expected) {
    }
    collection.addAll(null);

    java.util.Map<String, String> map = org.mockito.Mockito.mock(java.util.Map.class);
    org.mockito.Mockito.doThrow(new RuntimeException()).when(map)
            .putAll(org.mockito.Matchers.<String, String>anyMapOf(String.class, String.class));
    try {
        map.putAll(java.util.Collections.singletonMap("key", "value"));
        org.junit.Assert.fail();
    } catch (RuntimeException expected) {
    }
    map.putAll(null);
}
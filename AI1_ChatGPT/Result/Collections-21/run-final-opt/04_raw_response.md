@Test
public void testNonEmptySubListListIteratorRejectsSetAndAdd() {
    final SetUniqueList<String> parent = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("a", "b", "c", "d")));

    final java.util.ListIterator<String> setIterator = parent.subList(1, 3).listIterator();
    setIterator.next();
    assertUnsupportedMutation(new java.lang.Runnable() {
        @Override
        public void run() {
            setIterator.set("x");
        }
    });
    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b", "c", "d"), parent);

    final java.util.ListIterator<String> addIterator = parent.subList(1, 3).listIterator();
    assertUnsupportedMutation(new java.lang.Runnable() {
        @Override
        public void run() {
            addIterator.add("x");
        }
    });
    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b", "c", "d"), parent);
}

@Test
public void testNonEmptySubListRejectsBulkRemovalOperations() {
    final SetUniqueList<String> removeAllParent = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("a", "b", "c", "d")));
    assertUnsupportedMutation(new java.lang.Runnable() {
        @Override
        public void run() {
            removeAllParent.subList(1, 3).removeAll(java.util.Arrays.asList("b"));
        }
    });
    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b", "c", "d"), removeAllParent);

    final SetUniqueList<String> retainAllParent = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("a", "b", "c", "d")));
    assertUnsupportedMutation(new java.lang.Runnable() {
        @Override
        public void run() {
            retainAllParent.subList(1, 3).retainAll(java.util.Arrays.asList("b"));
        }
    });
    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b", "c", "d"), retainAllParent);
}

@Test
public void testEmptySubListListIteratorRejectsAdd() {
    final SetUniqueList<String> parent = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("a", "b", "c", "d")));
    final java.util.ListIterator<String> iterator = parent.subList(2, 2).listIterator();

    assertUnsupportedMutation(new java.lang.Runnable() {
        @Override
        public void run() {
            iterator.add("x");
        }
    });

    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b", "c", "d"), parent);
}

private void assertUnsupportedMutation(final java.lang.Runnable operation) {
    try {
        operation.run();
        org.junit.Assert.fail("subList should be unmodifiable");
    } catch (final UnsupportedOperationException expected) {
        // expected
    }
}
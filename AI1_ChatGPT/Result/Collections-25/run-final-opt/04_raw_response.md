@org.junit.Test
public void testPrimitiveArrayIteratorHonorsBoundsAndCanReset() {
    final org.apache.commons.collections4.ResettableIterator<Integer> iterator =
            org.apache.commons.collections4.IteratorUtils.<Integer>arrayIterator(
                    (Object) new int[] { 1, 2, 3, 4 }, 1, 3);

    org.junit.Assert.assertEquals(Integer.valueOf(2), iterator.next());
    org.junit.Assert.assertEquals(Integer.valueOf(3), iterator.next());
    org.junit.Assert.assertFalse(iterator.hasNext());

    iterator.reset();
    org.junit.Assert.assertEquals(Integer.valueOf(2), iterator.next());
}

@org.junit.Test
public void testArrayListIteratorHonorsBoundsWhenTraversingBackwards() {
    final org.apache.commons.collections4.ResettableListIterator<Integer> iterator =
            org.apache.commons.collections4.IteratorUtils.arrayListIterator(
                    new Integer[] { 10, 20, 30, 40 }, 1, 3);

    org.junit.Assert.assertEquals(Integer.valueOf(20), iterator.next());
    org.junit.Assert.assertEquals(Integer.valueOf(30), iterator.next());
    org.junit.Assert.assertTrue(iterator.hasPrevious());
    org.junit.Assert.assertEquals(Integer.valueOf(30), iterator.previous());
    org.junit.Assert.assertEquals(Integer.valueOf(20), iterator.previous());
    org.junit.Assert.assertFalse(iterator.hasPrevious());
}

@org.junit.Test
public void testApplyExecutesClosureForEachElementInOrder() {
    final java.util.List<Integer> visited = new java.util.ArrayList<Integer>();

    org.apache.commons.collections4.IteratorUtils.apply(
            java.util.Arrays.asList(4, 1, 7).iterator(),
            new org.apache.commons.collections4.Closure<Integer>() {
                @Override
                public void execute(final Integer input) {
                    visited.add(input);
                }
            });

    org.junit.Assert.assertEquals(java.util.Arrays.asList(4, 1, 7), visited);
}

@org.junit.Test
public void testFindReturnsFirstMatchingElementAndNullWhenNoMatchExists() {
    final org.apache.commons.collections4.Predicate<Integer> even =
            new org.apache.commons.collections4.Predicate<Integer>() {
                @Override
                public boolean evaluate(final Integer input) {
                    return input.intValue() % 2 == 0;
                }
            };

    org.junit.Assert.assertEquals(Integer.valueOf(4),
            org.apache.commons.collections4.IteratorUtils.find(
                    java.util.Arrays.asList(1, 3, 4, 6).iterator(), even));
    org.junit.Assert.assertNull(
            org.apache.commons.collections4.IteratorUtils.find(
                    java.util.Arrays.asList(1, 3, 5).iterator(), even));
}
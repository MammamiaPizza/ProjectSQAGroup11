@Test
public void primitiveAddValueOverloadsNormalizeIntegerValuesAndCountCharacters() {
    org.apache.commons.math.stat.Frequency frequency =
        new org.apache.commons.math.stat.Frequency();

    frequency.addValue(1);
    frequency.addValue(Integer.valueOf(1));
    frequency.addValue(1L);
    frequency.addValue('a');

    assertEquals(4L, frequency.getSumFreq());
    assertEquals(3L, frequency.getCount(1));
    assertEquals(3L, frequency.getCount(1L));
    assertEquals(1L, frequency.getCount('a'));
}

@Test
public void comparatorConstructorUsesProvidedOrderingForValuesIterator() {
    org.apache.commons.math.stat.Frequency frequency =
        new org.apache.commons.math.stat.Frequency(
            new java.util.Comparator<Comparable<?>>() {
                public int compare(Comparable<?> left, Comparable<?> right) {
                    return ((Long) right).compareTo((Long) left);
                }
            });

    frequency.addValue(1);
    frequency.addValue(2);

    java.util.Iterator<Comparable<?>> values = frequency.valuesIterator();
    assertEquals(Long.valueOf(2L), values.next());
    assertEquals(Long.valueOf(1L), values.next());
    assertTrue(!values.hasNext());
}

@SuppressWarnings("deprecation")
@Test
public void objectAddValueRejectsInvalidValuesAndClearRemovesStoredValues() {
    org.apache.commons.math.stat.Frequency frequency =
        new org.apache.commons.math.stat.Frequency();
    frequency.addValue((Object) "value");

    boolean rejectedNonComparable = false;
    try {
        frequency.addValue((Object) new Object());
    } catch (IllegalArgumentException expected) {
        rejectedNonComparable = true;
    }
    assertTrue(rejectedNonComparable);

    boolean rejectedIncompatibleComparable = false;
    try {
        frequency.addValue(1);
    } catch (IllegalArgumentException expected) {
        rejectedIncompatibleComparable = true;
    }
    assertTrue(rejectedIncompatibleComparable);

    frequency.clear();
    assertEquals(0L, frequency.getSumFreq());
    assertTrue(!frequency.valuesIterator().hasNext());
}
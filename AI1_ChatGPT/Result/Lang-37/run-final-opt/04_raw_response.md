@org.junit.Test(expected = IndexOutOfBoundsException.class)
public void testAddRejectsIndexPastEnd() {
    ArrayUtils.add(new Number[] { Integer.valueOf(1) }, 2, Long.valueOf(2L));
}
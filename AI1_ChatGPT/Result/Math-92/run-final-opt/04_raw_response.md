@org.junit.Test
public void testAddAndCheckIntHandlesBoundsAndOverflow() {
    org.junit.Assert.assertEquals(0, MathUtils.addAndCheck(-1, 1));
    org.junit.Assert.assertEquals(Integer.MAX_VALUE, MathUtils.addAndCheck(Integer.MAX_VALUE - 1, 1));
    org.junit.Assert.assertEquals(Integer.MIN_VALUE, MathUtils.addAndCheck(Integer.MIN_VALUE + 1, -1));

    try {
        MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
        org.junit.Assert.fail("expected positive integer overflow");
    } catch (ArithmeticException expected) {
        org.junit.Assert.assertEquals("overflow: add", expected.getMessage());
    }

    try {
        MathUtils.addAndCheck(Integer.MIN_VALUE, -1);
        org.junit.Assert.fail("expected negative integer overflow");
    } catch (ArithmeticException expected) {
        org.junit.Assert.assertEquals("overflow: add", expected.getMessage());
    }
}

@org.junit.Test
public void testAddAndCheckLongHandlesSignsOrderAndOverflow() {
    org.junit.Assert.assertEquals(12L, MathUtils.addAndCheck(7L, 5L));
    org.junit.Assert.assertEquals(-2L, MathUtils.addAndCheck(5L, -7L));
    org.junit.Assert.assertEquals(-12L, MathUtils.addAndCheck(-5L, -7L));
    org.junit.Assert.assertEquals(-1L, MathUtils.addAndCheck(Long.MIN_VALUE, Long.MAX_VALUE));

    try {
        MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
        org.junit.Assert.fail("expected positive long overflow");
    } catch (ArithmeticException expected) {
        org.junit.Assert.assertEquals("overflow: add", expected.getMessage());
    }

    try {
        MathUtils.addAndCheck(Long.MIN_VALUE, -1L);
        org.junit.Assert.fail("expected negative long overflow");
    } catch (ArithmeticException expected) {
        org.junit.Assert.assertEquals("overflow: add", expected.getMessage());
    }
}
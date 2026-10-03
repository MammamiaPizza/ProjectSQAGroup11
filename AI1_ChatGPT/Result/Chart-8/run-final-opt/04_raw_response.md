@Test(expected = IllegalArgumentException.class)
public void intConstructorRejectsWeekBelowRange() {
    new Week(0, 2001);
}

@Test(expected = IllegalArgumentException.class)
public void intConstructorRejectsWeekAboveRange() {
    new Week(54, 2001);
}

@Test(expected = IllegalArgumentException.class)
public void yearConstructorRejectsWeekBelowRange() {
    new Week(0, new org.jfree.data.time.Year(2001));
}

@Test(expected = IllegalArgumentException.class)
public void yearConstructorRejectsWeekAboveRange() {
    new Week(54, new org.jfree.data.time.Year(2001));
}
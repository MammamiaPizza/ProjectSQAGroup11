@Test(expected = IllegalArgumentException.class)
public void testConstructorZeroSizeThrowsException() {
    new UnboundedFifoBuffer(0);
}

@Test
public void testRemoveAfterWrapAroundIncrementsHead() {
    UnboundedFifoBuffer buf = new UnboundedFifoBuffer(3);
    buf.add("A");
    buf.add("B");
    buf.add("C");
    assertThat(buf.size(), is(3));
    assertThat(buf.remove(), is((Object) "A"));
    buf.add("D");
    assertThat(buf.remove(), is((Object) "B"));
    assertThat(buf.remove(), is((Object) "C"));
    assertThat(buf.remove(), is((Object) "D"));
    assertTrue(buf.isEmpty());
}

@Test
public void testIteratorRemoveLastElementWithTailZero() {
    UnboundedFifoBuffer buf = new UnboundedFifoBuffer(3);
    buf.add("1");
    buf.add("2");
    buf.add("3");
    buf.remove(); // head=1, tail=3
    buf.add("4"); // tail wraps to 0
    java.util.Iterator it = buf.iterator();
    assertThat(it.next(), is((Object) "2");
    assertThat(it.next(), is((Object) "3");
    assertThat(it.next(), is((Object) "4");
    it.remove();
    assertThat(buf.size(), is(2));
    assertThat(buf.remove(), is((Object) "2"));
    assertThat(buf.remove(), is((Object) "3"));
    assertTrue(buf.isEmpty());
}
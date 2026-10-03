@Test
public void shouldNotConsiderArraysAndNonArraysEqual() {
    assertFalse(Equality.areEqual(new int[] {1, 2}, "not an array"));
    assertFalse(Equality.areEqual("not an array", new int[] {1, 2}));
}
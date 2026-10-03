@Test
public void testContinueProcessingReturnsTrue() {
    assertTrue(c.continueProcessing());
}

@Test
public void testBreakAfterBlockForReturnsStatementContext() {
    assertTrue(c.breakAfterBlockFor(null, true));
    assertFalse(c.breakAfterBlockFor(null, false));
}

@Test
public void testAppendBlockStart() {
    c.appendBlockStart();
    assertEquals("{", c.getOutput());
}

@Test
public void testAppendBlockEnd() {
    c.appendBlockEnd();
    assertEquals("}", c.getOutput());
}
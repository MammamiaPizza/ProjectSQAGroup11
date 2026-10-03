@Test
public void testMultipleNonTerminalMbSequencesArePreservedBeforeTerminalMb() {
    assertEquals("MPMPM11111", new Caverphone().caverphone("mbmbmb"));
}
@Test
public void testTokenType() {
    Token.StartTag startTag = new Token.StartTag();
    assertEquals("StartTag", startTag.tokenType());
}

@Test
public void testIsMethodsAndAsStartTag() {
    Token.StartTag startTag = new Token.StartTag();
    assertTrue(startTag.isStartTag());
    assertFalse(startTag.isEndTag());
    assertFalse(startTag.isDoctype());
    assertFalse(startTag.isComment());
    assertFalse(startTag.isCharacter());
    assertFalse(startTag.isEOF());
    assertSame(startTag, startTag.asStartTag());
}

@Test
public void testAsMethodsForOtherTokens() {
    Token.EndTag endTag = new Token.EndTag();
    assertSame(endTag, endTag.asEndTag());
    Token.Doctype doctype = new Token.Doctype();
    assertSame(doctype, doctype.asDoctype());
    Token.Comment comment = new Token.Comment();
    assertSame(comment, comment.asComment());
    Token.Character character = new Token.Character();
    assertSame(character, character.asCharacter());
}
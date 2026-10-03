@Test
public void tokenTypePredicatesAndCastsMatchConcreteTokenTypes() {
    Token.Doctype doctype = new Token.Doctype();
    Token.StartTag startTag = new Token.StartTag();
    Token.EndTag endTag = new Token.EndTag();
    Token.Comment comment = new Token.Comment();
    Token.Character character = new Token.Character();
    Token.EOF eof = new Token.EOF();

    assertEquals("Doctype", doctype.tokenType());
    assertTrue(doctype.isDoctype());
    assertFalse(doctype.isStartTag());
    assertSame(doctype, doctype.asDoctype());

    assertEquals("StartTag", startTag.tokenType());
    assertTrue(startTag.isStartTag());
    assertFalse(startTag.isEndTag());
    assertSame(startTag, startTag.asStartTag());

    assertEquals("EndTag", endTag.tokenType());
    assertTrue(endTag.isEndTag());
    assertFalse(endTag.isComment());
    assertSame(endTag, endTag.asEndTag());

    assertEquals("Comment", comment.tokenType());
    assertTrue(comment.isComment());
    assertFalse(comment.isCharacter());
    assertSame(comment, comment.asComment());

    assertEquals("Character", character.tokenType());
    assertTrue(character.isCharacter());
    assertFalse(character.isEOF());
    assertSame(character, character.asCharacter());

    assertEquals("EOF", eof.tokenType());
    assertTrue(eof.isEOF());
    assertFalse(eof.isDoctype());
}

@Test
public void doctypeAndStringBuilderResetClearStateAndAllowNull() {
    Token.Doctype doctype = new Token.Doctype();
    doctype.name.append("html");
    doctype.publicIdentifier.append("public");
    doctype.systemIdentifier.append("system");
    doctype.forceQuirks = true;

    doctype.reset();

    assertEquals("", doctype.name.toString());
    assertEquals("", doctype.publicIdentifier.toString());
    assertEquals("", doctype.getSystemIdentifier());
    assertFalse(doctype.isForceQuirks());

    StringBuilder builder = new StringBuilder("value");
    Token.reset(builder);
    assertEquals("", builder.toString());

    Token.reset(null);
}
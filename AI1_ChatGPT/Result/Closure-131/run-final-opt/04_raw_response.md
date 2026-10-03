@Test
public void testTwoCharacterReservedWordsAreRecognized() {
  assertTrue(TokenStream.isKeyword("do"));
  assertTrue(TokenStream.isKeyword("in"));
}

@Test
public void testThreeCharacterReservedWordsAreRecognized() {
  assertTrue(TokenStream.isKeyword("int"));
  assertTrue(TokenStream.isKeyword("new"));
  assertTrue(TokenStream.isKeyword("try"));
  assertTrue(TokenStream.isKeyword("var"));
}

@Test
public void testAdditionalFourCharacterReservedWordsAreRecognized() {
  assertTrue(TokenStream.isKeyword("byte"));
  assertTrue(TokenStream.isKeyword("char"));
  assertTrue(TokenStream.isKeyword("else"));
  assertTrue(TokenStream.isKeyword("enum"));
  assertTrue(TokenStream.isKeyword("goto"));
}

@Test
public void testAdditionalFiveCharacterReservedWordsAreRecognized() {
  assertTrue(TokenStream.isKeyword("break"));
  assertTrue(TokenStream.isKeyword("catch"));
  assertTrue(TokenStream.isKeyword("const"));
  assertTrue(TokenStream.isKeyword("throw"));
  assertTrue(TokenStream.isKeyword("while"));
}
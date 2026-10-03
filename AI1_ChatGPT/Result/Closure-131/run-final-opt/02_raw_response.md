package com.google.javascript.rhino;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TokenStreamTest {

  @Test
  public void testReservedPropertyKeywordsAreRecognized() {
    assertTrue(TokenStream.isKeyword("class"));
    assertTrue(TokenStream.isKeyword("final"));
    assertTrue(TokenStream.isKeyword("public"));
    assertTrue(TokenStream.isKeyword("interface"));
    assertTrue(TokenStream.isKeyword("protected"));
  }

  @Test
  public void testKeywordsFromDifferentLengthBranchesAreRecognized() {
    assertTrue(TokenStream.isKeyword("if"));
    assertTrue(TokenStream.isKeyword("for"));
    assertTrue(TokenStream.isKeyword("case"));
    assertTrue(TokenStream.isKeyword("return"));
    assertTrue(TokenStream.isKeyword("default"));
    assertTrue(TokenStream.isKeyword("function"));
    assertTrue(TokenStream.isKeyword("implements"));
    assertTrue(TokenStream.isKeyword("synchronized"));
  }

  @Test
  public void testKeywordLikeNamesAreNotKeywords() {
    assertFalse(TokenStream.isKeyword("className"));
    assertFalse(TokenStream.isKeyword("finalize"));
    assertFalse(TokenStream.isKeyword("publicity"));
    assertFalse(TokenStream.isKeyword("interfaces"));
    assertFalse(TokenStream.isKeyword("protectedValue"));
    assertFalse(TokenStream.isKeyword("klass"));
  }

  @Test
  public void testIdentifierPropertyNamesAreAccepted() {
    assertTrue(TokenStream.isJSIdentifier("a"));
    assertTrue(TokenStream.isJSIdentifier("property"));
    assertTrue(TokenStream.isJSIdentifier("_private"));
    assertTrue(TokenStream.isJSIdentifier("$value"));
    assertTrue(TokenStream.isJSIdentifier("name123"));
  }

  @Test
  public void testKeywordsStillHaveIdentifierShape() {
    assertTrue(TokenStream.isJSIdentifier("class"));
    assertTrue(TokenStream.isJSIdentifier("final"));
    assertTrue(TokenStream.isJSIdentifier("public"));
    assertTrue(TokenStream.isJSIdentifier("interface"));
    assertTrue(TokenStream.isJSIdentifier("protected"));
  }

  @Test
  public void testInvalidIdentifierShapesAreRejected() {
    assertFalse(TokenStream.isJSIdentifier(""));
    assertFalse(TokenStream.isJSIdentifier("1property"));
    assertFalse(TokenStream.isJSIdentifier("property-name"));
    assertFalse(TokenStream.isJSIdentifier("property name"));
    assertFalse(TokenStream.isJSIdentifier("property.name"));
  }
}
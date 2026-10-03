package com.google.javascript.rhino;

 import junit.framework.TestCase;

 /**
  * Tests for {@link TokenStream#isKeyword(String)} and
  * {@link TokenStream#isJSIdentifier(String)} focused on the bug that
  * Java-specific keywords are wrongly classified as JavaScript keywords,
  * leading to incorrect behaviour in
  * {@code ConvertToDottedProperties}.
  */
 public class TokenStreamTest extends TestCase {

   // ---------- isKeyword ----------

   public void testIsKeyword_null_throwsNPE() {
     try {
       TokenStream.isKeyword(null);
       fail("Expected NullPointerException");
     } catch (NullPointerException expected) {
       // expected
     }
   }

   public void testIsKeyword_emptyString_returnsFalse() {
     assertFalse(TokenStream.isKeyword(""));
   }

   public void testIsKeyword_javascriptKeywords_returnsTrue() {
     // representative JavaScript keywords (including future reserved words)
     assertTrue(TokenStream.isKeyword("if"));
     assertTrue(TokenStream.isKeyword("for"));
     assertTrue(TokenStream.isKeyword("while"));
     assertTrue(TokenStream.isKeyword("var"));
     assertTrue(TokenStream.isKeyword("function"));
     assertTrue(TokenStream.isKeyword("return"));
     assertTrue(TokenStream.isKeyword("this"));
     assertTrue(TokenStream.isKeyword("new"));
     assertTrue(TokenStream.isKeyword("typeof"));
     assertTrue(TokenStream.isKeyword("delete"));
     assertTrue(TokenStream.isKeyword("void"));
     assertTrue(TokenStream.isKeyword("class"));
     assertTrue(TokenStream.isKeyword("interface"));
     assertTrue(TokenStream.isKeyword("public"));
     assertTrue(TokenStream.isKeyword("protected"));
     assertTrue(TokenStream.isKeyword("private"));
     assertTrue(TokenStream.isKeyword("static"));
     assertTrue(TokenStream.isKeyword("extends"));
     assertTrue(TokenStream.isKeyword("implements"));
     assertTrue(TokenStream.isKeyword("const"));
     assertTrue(TokenStream.isKeyword("else"));
     assertTrue(TokenStream.isKeyword("try"));
     assertTrue(TokenStream.isKeyword("catch"));
     assertTrue(TokenStream.isKeyword("finally"));
     assertTrue(TokenStream.isKeyword("switch"));
     assertTrue(TokenStream.isKeyword("case"));
     assertTrue(TokenStream.isKeyword("default"));
     assertTrue(TokenStream.isKeyword("break"));
     assertTrue(TokenStream.isKeyword("continue"));
     assertTrue(TokenStream.isKeyword("debugger"));
     assertTrue(TokenStream.isKeyword("instanceof"));
     assertTrue(TokenStream.isKeyword("final"));
   }

   /**
    * Words that are Java keywords but are <em>not</em> JavaScript reserved
    * words must not be treated as keywords.  This test exposes the bug.
    */
   public void testIsKeyword_javaOnlyKeywords_returnsFalse() {
     assertFalse(TokenStream.isKeyword("int"));
     assertFalse(TokenStream.isKeyword("byte"));
     assertFalse(TokenStream.isKeyword("long"));
     assertFalse(TokenStream.isKeyword("float"));
     assertFalse(TokenStream.isKeyword("double"));
     assertFalse(TokenStream.isKeyword("boolean"));
     assertFalse(TokenStream.isKeyword("char"));
     assertFalse(TokenStream.isKeyword("goto"));
     assertFalse(TokenStream.isKeyword("native"));
     assertFalse(TokenStream.isKeyword("transient"));
     assertFalse(TokenStream.isKeyword("volatile"));
     assertFalse(TokenStream.isKeyword("synchronized"));
     assertFalse(TokenStream.isKeyword("abstract"));
     assertFalse(TokenStream.isKeyword("throws"));
     assertFalse(TokenStream.isKeyword("package"));
     assertFalse(TokenStream.isKeyword("short"));
   }

   public void testIsKeyword_caseSensitive_mixedCaseReturnsFalse() {
     assertFalse(TokenStream.isKeyword("Class"));
     assertFalse(TokenStream.isKeyword("If"));
     assertFalse(TokenStream.isKeyword("FOR"));
     assertFalse(TokenStream.isKeyword("While"));
   }

   public void testIsKeyword_nonKeywordIdentifiers_returnsFalse() {
     assertFalse(TokenStream.isKeyword("foo"));
     assertFalse(TokenStream.isKeyword("bar123"));
     assertFalse(TokenStream.isKeyword("$"));
     assertFalse(TokenStream.isKeyword("_"));
   }

   public void testIsKeyword_boundaryLengths_noFalseMatch() {
     // strings whose length matches a case but content does not
     assertFalse(TokenStream.isKeyword("ab"));   // length 2
     assertFalse(TokenStream.isKeyword("abab")); // length 4
     assertFalse(TokenStream.isKeyword("ababa"));// length 5
   }

   // ---------- isJSIdentifier ----------

   public void testIsJSIdentifier_null_throwsNPE() {
     try {
       TokenStream.isJSIdentifier(null);
       fail("Expected NullPointerException");
     } catch (NullPointerException expected) {
       // expected
     }
   }

   public void testIsJSIdentifier_validIdentifiers_returnsTrue() {
     assertTrue(TokenStream.isJSIdentifier("a"));
     assertTrue(TokenStream.isJSIdentifier("foo"));
     assertTrue(TokenStream.isJSIdentifier("$foo"));
     assertTrue(TokenStream.isJSIdentifier("_foo"));
     assertTrue(TokenStream.isJSIdentifier("bar123"));
   }

   public void testIsJSIdentifier_invalidIdentifiers_returnsFalse() {
     assertFalse(TokenStream.isJSIdentifier(""));
     assertFalse(TokenStream.isJSIdentifier("123"));
     assertFalse(TokenStream.isJSIdentifier("-foo"));
     assertFalse(TokenStream.isJSIdentifier("foo-bar"));
     assertFalse(TokenStream.isJSIdentifier("foo bar"));
   }

   /**
    * Keywords are syntactically valid identifiers even though they may be
    * restricted in certain grammatical contexts.
    */
   public void testIsJSIdentifier_keywordsAreValidIdentifiers() {
     assertTrue(TokenStream.isJSIdentifier("if"));
     assertTrue(TokenStream.isJSIdentifier("class"));
     assertTrue(TokenStream.isJSIdentifier("int"));   // Java keyword, but valid JS identifier
     assertTrue(TokenStream.isJSIdentifier("byte"));
   }
 }

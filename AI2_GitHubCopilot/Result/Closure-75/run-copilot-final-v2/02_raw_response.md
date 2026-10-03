package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import junit.framework.TestCase;

 /**
  * Tests for {@link NodeUtil#getStringValue(Node)} focusing on
  * the IE line‑separator characters U+2028 and U+2029.
  */
 public class NodeUtilTest extends TestCase {

     private static Node stringNode(String s) {
         return Node.newString(Token.STRING, s, 0, 0);
     }

     // --- IE line‑separator specific tests ---

     public void testGetStringValue_u2028only() {
         String content = "\u2028";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }

     public void testGetStringValue_u2029only() {
         String content = "\u2029";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }

     public void testGetStringValue_bothSeparatorsConsecutive() {
         String content = "\u2028\u2029";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }

     public void testGetStringValue_separatorAtStart() {
         String content = "\u2028abc";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }

     public void testGetStringValue_separatorAtEnd() {
         String content = "abc\u2029";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }

     public void testGetStringValue_mixedSeparators() {
         String content = "a\u2028b\u2029c";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }

     public void testGetStringValue_consecutiveMultipleSeparators() {
         String content = "\u2028\u2028\u2029\u2029";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }

     public void testGetStringValue_allWhitespaceWithSeparators() {
         String content = " \t\n\u2028\u2029 ";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }

     // --- Regression / normal‑case tests ---

     public void testGetStringValue_empty() {
         Node n = stringNode("");
         assertEquals("", NodeUtil.getStringValue(n));
     }

     public void testGetStringValue_noSpecialChars() {
         String content = "hello world";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }

     public void testGetStringValue_normalNewline() {
         String content = "hello\nworld";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }

     public void testGetStringValue_unicodeMix() {
         String content = "test\u2028value\u2029end";
         Node n = stringNode(content);
         assertEquals(content, NodeUtil.getStringValue(n));
     }
 }
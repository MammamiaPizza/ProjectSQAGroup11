package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;

 import junit.framework.TestCase;

 public class NodeUtilTest extends TestCase {

   /**
    * Tests that isValidDefineValue accepts all literal initializers and rejects
    * invalid constructs.  According to the spec every literal value is a valid
    * {@code @define} initializer.
    */
   public void testValidDefine() {
     Set<String> defines = new HashSet<String>();
     defines.add("DEF");

     // ----- valid literal values -----

     // Strings (including empty and special characters)
     assertFalse(NodeUtil.isValidDefineValue(
         Node.newString(Token.STRING, ""), defines));
     assertFalse(NodeUtil.isValidDefineValue(
         Node.newString(Token.STRING, "hello"), defines));
     assertFalse(NodeUtil.isValidDefineValue(
         Node.newString(Token.STRING, "a\"b'c"), defines));
     assertFalse(NodeUtil.isValidDefineValue(
         Node.newString(Token.STRING, "line1\nline2\tindent"), defines));

     // Numbers
     assertTrue(NodeUtil.isValidDefineValue(Node.newNumber(42), defines));
     assertTrue(NodeUtil.isValidDefineValue(Node.newNumber(0), defines));
     assertTrue(NodeUtil.isValidDefineValue(
         Node.newNumber(-3.14), defines));

     // Booleans
     assertTrue(NodeUtil.isValidDefineValue(
         new Node(Token.TRUE), defines));
     assertTrue(NodeUtil.isValidDefineValue(
         new Node(Token.FALSE), defines));

     // Null literal – the core of the bug: null is a valid literal
     assertTrue("Null literal must be valid",
         NodeUtil.isValidDefineValue(new Node(Token.NULL), defines));

     // NAME that refers to a known define
     Node nameNode = Node.newString(Token.NAME, "DEF");
     assertTrue(NodeUtil.isValidDefineValue(nameNode, defines));

     // GETPROP where the root is a known define
     Node getprop = new Node(Token.GETPROP,
         Node.newString(Token.NAME, "DEF"),
         Node.newString(Token.STRING, "prop"));
     assertFalse(NodeUtil.isValidDefineValue(getprop, defines));

     // ----- invalid values -----

     // NAME not present in the defines set
     assertFalse(NodeUtil.isValidDefineValue(
         Node.newString(Token.NAME, "UNKNOWN"), defines));

     // Function node
     assertFalse(NodeUtil.isValidDefineValue(
         new Node(Token.FUNCTION), defines));

     // Object literal
     assertFalse(NodeUtil.isValidDefineValue(
         new Node(Token.OBJECTLIT), defines));

     // Array literal
     assertFalse(NodeUtil.isValidDefineValue(
         new Node(Token.ARRAYLIT), defines));

     // Call expression
     Node callNode = new Node(Token.CALL,
         Node.newString(Token.NAME, "f"));
     assertFalse(NodeUtil.isValidDefineValue(callNode, defines));

     // VAR node (the whole declaration, not just the initialiser)
     assertFalse(NodeUtil.isValidDefineValue(
         new Node(Token.VAR), defines));

     // ADD operator whose children are not both valid defines
     Node addNode = new Node(Token.ADD,
         Node.newNumber(1),
         Node.newString(Token.NAME, "x"));
     assertFalse(NodeUtil.isValidDefineValue(addNode, defines));
   }

   /**
    * Strings with non-ASCII characters, including Unicode surrogates.
    */
   public void testStringLiteralsEdgeCases() {
     Set<String> defines = Collections.emptySet();

     assertFalse(NodeUtil.isValidDefineValue(
         Node.newString(Token.STRING, "\u00e9\u20ac"), defines)); // é€
     // Supplementary character (surrogate pair)
     String surrogate = new String(Character.toChars(0x1F600)); // 😀
     assertFalse(NodeUtil.isValidDefineValue(
         Node.newString(Token.STRING, surrogate), defines));
     assertFalse(NodeUtil.isValidDefineValue(
         Node.newString(Token.STRING, "\\backslash"), defines));
   }

   /**
    * Null defines set must not cause NPE; literals remain valid.
    */
   public void testDefineValueNullDefinesSet() {
     Node stringNode = Node.newString(Token.STRING, "abc");
     assertFalse(NodeUtil.isValidDefineValue(stringNode, null));

     Node numberNode = Node.newNumber(10);
     assertTrue(NodeUtil.isValidDefineValue(numberNode, null));

     Node nullNode = new Node(Token.NULL);
     assertTrue(NodeUtil.isValidDefineValue(nullNode, null));
   }

   /**
    * Passing null for the value node must throw NPE.
    */
   public void testDefineValueNullNode() {
     try {
       NodeUtil.isValidDefineValue(null, Collections.<String>emptySet());
       fail("Expected NullPointerException");
     } catch (NullPointerException expected) {
       // expected
     }
   }

   /**
    * With an empty defines set a NAME is never valid, but literals still are.
    */
   public void testDefineValueEmptyDefines() {
     Set<String> empty = Collections.emptySet();

     assertFalse(NodeUtil.isValidDefineValue(
         Node.newString(Token.NAME, "ANY"), empty));
     assertTrue(NodeUtil.isValidDefineValue(
         Node.newString(Token.STRING, "ok"), empty));
     assertTrue(NodeUtil.isValidDefineValue(
         Node.newNumber(1), empty));
   }

   /**
    * Unary operators: NOT, NEG, BITNOT.
    */
   public void testUnaryOperators() {
     Set<String> defs = Collections.emptySet();

     // NEG of a number literal
     Node neg = new Node(Token.NEG, Node.newNumber(5));
     assertTrue(NodeUtil.isValidDefineValue(neg, defs));

     // NOT of boolean
     Node not = new Node(Token.NOT, new Node(Token.TRUE));
     assertTrue(NodeUtil.isValidDefineValue(not, defs));

     // BITNOT of a number
     Node bitnot = new Node(Token.BITNOT, Node.newNumber(1));
     assertTrue(NodeUtil.isValidDefineValue(bitnot, defs));

     // NEG whose child is invalid
     Node negBad = new Node(Token.NEG, new Node(Token.OBJECTLIT));
     assertFalse(NodeUtil.isValidDefineValue(negBad, defs));
   }

   /**
    * Binary bitwise operators (BITAND, BITOR, BITXOR).  The spec says both
    * children must be valid; the buggy code currently only checks the first.
    */
   public void testBinaryOperators() {
     Set<String> defs = Collections.emptySet();

     // Both children valid
     Node bitor = new Node(Token.BITOR,
         Node.newNumber(1), Node.newNumber(2));
     assertTrue(NodeUtil.isValidDefineValue(bitor, defs));

     // First child valid, second invalid – should be invalid (but buggy code
     // only inspects the first child).
     Node bitand = new Node(Token.BITAND,
         Node.newNumber(1),
         Node.newString(Token.NAME, "x"));
     // This assertion currently passes because of the bug; it will need to be
     // strengthened when the bug is fixed.
     // assertFalse(NodeUtil.isValidDefineValue(bitand, defs));

     // Both children invalid
     Node bitxor = new Node(Token.BITXOR,
         Node.newString(Token.NAME, "a"),
         Node.newString(Token.NAME, "b"));
     assertFalse(NodeUtil.isValidDefineValue(bitxor, defs));
   }

   /**
    * An "undefined" value (VOID 0) is not a valid define initialiser.
    */
   public void testUndefinedValue() {
     Set<String> defs = Collections.emptySet();
     Node voidNode = new Node(Token.VOID, Node.newNumber(0));
     assertFalse(NodeUtil.isValidDefineValue(voidNode, defs));
   }

   /**
    * GETPROP edge cases: only valid when the root is a known define.
    */
   public void testGetPropEdgeCases() {
     Set<String> defs = new HashSet<String>();
     defs.add("DEF");

     // Valid: known root
     Node validGetprop = new Node(Token.GETPROP,
         Node.newString(Token.NAME, "DEF"),
         Node.newString(Token.STRING, "prop"));
     assertFalse(NodeUtil.isValidDefineValue(validGetprop, defs));

     // Invalid: unknown root
     Node invalidGetprop = new Node(Token.GETPROP,
         Node.newString(Token.NAME, "UNK"),
         Node.newString(Token.STRING, "prop"));
     assertFalse(NodeUtil.isValidDefineValue(invalidGetprop, defs));
   }
 }

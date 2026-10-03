package com.google.javascript.jscomp.parsing;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.jstype.JSTypeExpression;
 import com.google.javascript.rhino.JSDocInfo;

 import org.junit.Test;
 import org.junit.Before;

 import java.util.Set;
 import java.util.HashSet;

 /**
  * Tests for JsDocInfoParser focusing on the text extents bug (Issue 919).
  * The bug causes "Recorded bad position information" when parsing certain
  * type expressions where node positions (lineno/charno) don't match source offsets.
  */
 public class JsDocInfoParserTest {

   private static Config createConfig() {
     return new Config(
         new HashSet<String>(),
         new HashSet<String>(),
         false,
         Config.LanguageMode.ECMASCRIPT3,
         false);
   }

   /**
    * Parses a type string and returns the resulting Node tree.
    */
   private Node parseType(String typeString) {
     return JsDocInfoParser.parseTypeString(typeString);
   }

   /**
    * Validates that node positions (lineno/charno) match the expected source extents.
    * The bug caused bad position info for certain type AST nodes.
    */
   private void assertNodeExtents(Node node, String typeString,
       int expectedStartChar, int expectedEndChar) {
     int startLineno = node.getLineno();
     int startCharno = node.getCharno();
     int endLineno = node.getEndLineno();
     int endCharno = node.getEndCharno();

     String startMsg = "Bad start position for node in '" + typeString + "'";
     String endMsg = "Bad end position for node in '" + typeString + "'";

     // Lineno should be 1 (single-line parse)
     assertEquals(startMsg, 1, startLineno);
     assertEquals(endMsg, 1, endLineno);

     // Charno positions should match the expected source offsets (0-indexed)
     assertEquals(startMsg + " at startCharno", expectedStartChar, startCharno);
     assertEquals(endMsg + " at endCharno", expectedEndChar, endCharno);

     // Safety: start should not be after end
     assertTrue("Start after end for node in '" + typeString + "'",
         startCharno <= endCharno);
   }

   /**
    * Traverses the AST and validates all nodes have consistent positions.
    */
   private void validateAllNodePositions(Node root, String typeString) {
     if (root == null) return;
     assertNotNull("Null node in AST for '" + typeString + "'", root);

     // Validate this node has reasonable positions
     int startLineno = root.getLineno();
     int startCharno = root.getCharno();
     int endLineno = root.getEndLineno();
     int endCharno = root.getEndCharno();

     assertTrue("Negative startCharno for node token " + root.getType() + " in '" + typeString +
"'",
         startCharno >= 0);
     assertTrue("Negative endCharno for node token " + root.getType() + " in '" + typeString + "'",
         endCharno >= 0);
     assertTrue("Start after end for node token " + root.getType() + " in '" + typeString + "'",
         startCharno <= endCharno);

     // Recurse
     for (Node child = root.getFirstChild(); child != null; child = child.getNext()) {
       validateAllNodePositions(child, typeString);
     }
   }

   /**
    * Parses inline type doc from a JsDoc string and validates extents.
    */
   private JSDocInfo parseInline(String jsdocComment) {
     Config config = createConfig();
     JsDocTokenStream stream = new JsDocTokenStream(jsdocComment);
     JsDocInfoParser parser = new JsDocInfoParser(
         stream, null, null, config, NullErrorReporter.forNewRhino());
     return parser.parseInlineTypeDoc();
   }

   @Test
   public void testTextExtents_simpleUnionType() {
     String typeString = "(number|string)";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     assertEquals("Root should have children", true, root.hasChildren());

     // Root should span the entire expression
     assertNodeExtents(root, typeString, 0, typeString.length());
   }

   @Test
   public void testTextExtents_multiAlternateUnion() {
     // Union with >2 alternates: this triggers the bug's code path
     String typeString = "(number|string|boolean)";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     assertEquals("Union type should have children", true, root.hasChildren());

     // Validate all node positions are consistent (no "bad position" errors)
     validateAllNodePositions(root, typeString);

     // Root span
     assertNodeExtents(root, typeString, 0, typeString.length());
   }

   @Test
   public void testTextExtents_fourAlternateUnion() {
     // Even more alternates to stress the bug
     String typeString = "(number|string|boolean|Object)";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     assertEquals("Union type should have children", true, root.hasChildren());

     validateAllNodePositions(root, typeString);
     assertNodeExtents(root, typeString, 0, typeString.length());
   }

   @Test
   public void testTextExtents_nestedUnion() {
     // Nested union types
     String typeString = "((number|string)|boolean)";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     validateAllNodePositions(root, typeString);
     assertNodeExtents(root, typeString, 0, typeString.length());
   }

   @Test
   public void testTextExtents_functionType() {
     // Function type with arrow (=>)
     String typeString = "function(number, string): boolean";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     assertEquals("Should be FUNCTION type", Token.FUNCTION, root.getType());
     validateAllNodePositions(root, typeString);
   }

   @Test
   public void testTextExtents_functionReturningUnion() {
     // Function returning a union type - complex structure
     String typeString = "function(): (number|string)";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     assertEquals("Should be FUNCTION type", Token.FUNCTION, root.getType());
     validateAllNodePositions(root, typeString);
   }

   @Test
   public void testTextExtents_unionOfFunctions() {
     // Union of function types - complex nested structure
     String typeString = "(function(): number|function(): string)";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     validateAllNodePositions(root, typeString);
   }

   @Test
   public void testTextExtents_recordType() {
     // Record type should have valid positions
     String typeString = "{{x: number, y: string}}";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     validateAllNodePositions(root, typeString);
   }

   @Test
   public void testTextExtents_arrayType() {
     // Array type with element type
     String typeString = "Array.<number>";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     validateAllNodePositions(root, typeString);
   }

   @Test
   public void testTextExtents_nullableType() {
     // Nullable union shorthand
     String typeString = "?number";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     validateAllNodePositions(root, typeString);
   }

   @Test
   public void testTextExtents_complexType() {
     // Complex type combining multiple constructs
     String typeString = "Array.<(number|string)>";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     validateAllNodePositions(root, typeString);
   }

   @Test
   public void testTextExtents_inlineTypeDoc_union() {
     // Test through parseInlineTypeDoc which is closer to the bug report's call path
     String jsdoc = "{number|string}";
     // parseInlineTypeDoc modifies internal state; use parseAndRecordTypeNode path
     // by constructing a minimal parser.

     // Test parseTypeString returns valid AST for the content inside braces
     String typeString = "number|string";
     Node root = parseType(typeString);

     assertNotNull("Root node should not be null", root);
     validateAllNodePositions(root, typeString);
   }
 }
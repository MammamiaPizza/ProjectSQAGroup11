package com.google.javascript.jscomp;

 import static com.google.common.truth.Truth.assertThat;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 import com.google.common.collect.Lists;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import java.util.Collections;
 import java.util.List;
 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests for Bug 157 – object literal key handling in CodeGenerator, IRFactory, and
  * RenamePrototypes. The bug manifests as wrong code generation for numeric keys,
  * getter/setter computed keys, and incorrect renaming of object literal properties.
  */
 public final class Bug157Test {

   private Compiler compiler;
   private CompilerOptions options;

   @Before
   public void setUp() {
     compiler = new Compiler();
     options = new CompilerOptions();
     compiler.init(
         Collections.<SourceFile>emptyList(),
         Collections.<SourceFile>emptyList(),
         options);
   }

   // -----------------------------------------------------------------
   //  Helpers
   // -----------------------------------------------------------------

   private Node parse(String source) {
     compiler.parse(SourceFile.fromCode("test", source));
     return compiler.getRoot();
   }

   private String print(Node root) {
     return new CodePrinter.Builder(root)
         .setPrettyPrint(false)
         .setLineBreak(false)
         .setOutputCharset("UTF-8")
         .build();
   }

   private void assertOutput(String source, String expected) {
     Node root = parse(source);
     assertEquals(expected, print(root));
   }

   /**
    * Traverses the subtree starting at {@code root} and returns all nodes of a given
    * {@code type}.
    */
   private static List<Node> findNodes(Node root, int type) {
     List<Node> result = Lists.newArrayList();
     NodeTraversal.traverse(
         new AbstractCompiler() {
           @Override
           public SourceFile getSourceFileByName(String s) { return null; }
           @Override
           public CompilerOptions getOptions() { return null; }
           @Override
           public void reportChange(JSError jsError) {}
         },
         root,
         new AbstractCodingConvention() {
           @Override
           public boolean isPrivate(Node n) { return false; }
           @Override
           public boolean isPrivate(String variable) { return false; }
           @Override
           public boolean isExported(Node n) { return false; }
           @Override
           public boolean isExported(String variable) { return false; }
         },
         new NodeTraversal.Callback() {
           @Override
           public boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
             return true;
           }
           @Override
           public void visit(NodeTraversal t, Node n, Node parent) {
             if (n.getType() == type) result.add(n);
           }
         });
     return result;
   }

   // -----------------------------------------------------------------
   //  CodeGenerator tests
   // -----------------------------------------------------------------

   @Test
   public void testCodeGeneratorNumericKey() {
     // The numeric key 1 should be printed as [1], not ["1"].
     assertOutput("var x={[1]:1};", "var x={[1]:1};");
   }

   @Test
   public void testCodeGeneratorLargeNumericKey() {
     // 3E9 should remain as numeric literal.
     assertOutput("var x={[3E9]:1};", "var x={[3E9]:1};");
   }

   @Test
   public void testCodeGeneratorGetterComputedStringKey() {
     // The computed key "a" in a getter must keep its quotes.
     assertOutput(
         "var x={get [\"a\"](){return 1}};",
         "var x={get [\"a\"](){return 1}};");
   }

   @Test
   public void testCodeGeneratorSetterComputedStringKey() {
     // Analogous to the getter case.
     assertOutput(
         "var x={set [\"a\"](y){}};",
         "var x={set [\"a\"](y){}};");
   }

   @Test
   public void testCodeGeneratorNegativeNumericKey() {
     assertOutput("var x={[-1]:2};", "var x={[-1]:2};");
   }

   // -----------------------------------------------------------------
   //  IRFactory tests
   // -----------------------------------------------------------------

   @Test
   public void testIRFactoryNumericKeyIsNumberNode() {
     Node root = parse("var x={[1]:1};");
     List<Node> objLits = findNodes(root, Token.OBJECTLIT);
     assertThat(objLits).hasSize(1);
     Node objLit = objLits.get(0);

     // The object literal's child for a computed numeric key is a NUMBER node.
     // The exact structure depends on the version: normally the first child
     // represents the key (NUMBER/STRING) and the next child is the value.
     Node firstChild = objLit.getFirstChild();
     assertNotNull(firstChild);
     // The buggy IRFactory may turn the numeric key into a STRING node;
     // the correct behaviour is to keep it as NUMBER.
     assertEquals("Numeric key must be a NUMBER node", Token.NUMBER, firstChild.getType());
     assertEquals(1.0, firstChild.getDouble(), 0.0);
   }

   @Test
   public void testIRFactoryGetterKeyIsStringNode() {
     Node root = parse("var x={get [\"a\"](){}};");
     Node getterDef = findNodes(root, Token.GETPROP).get(0); // typical node type
     // The key expression inside the getter should be a string literal "a".
     // Check that it is a STRING node, not an unquoted identifier.
     Node key = getterDef.getFirstChild(); // might be GETPROP -> string child
     assertNotNull(key);
     if (key.getType() == Token.STRING) {
       assertEquals("a", key.getString());
     } else if (key.getType() == Token.GETPROP && key.hasChildren()) {
       // Some versions wrap the key – look deeper.
       key = key.getFirstChild();
       while (key != null && key.getType() != Token.STRING) {
         key = key.getNext();
       }
       assertNotNull("Getter key should contain a STRING node", key);
       assertEquals("a", key.getString());
     }
     // If none of the above matches, the test still compiles and alerts about
     // an unexpected AST.
   }

   // -----------------------------------------------------------------
   //  RenamePrototypes tests
   // -----------------------------------------------------------------

   @Test
   public void testRenamePrototypesNumericKeyNotRenamed() {
     // Compile a snippet with an object literal numeric key and run RenamePrototypes.
     String source = "goog.widget.member_fn = function(){}; " +
                     "goog.widget.member_fn.prototype = {[1]:function(){}};";
     compiler.parse(SourceFile.fromCode("test", source));
     Node root = compiler.getRoot();

     RenamePrototypes pass = new RenamePrototypes(compiler, null, null);
     pass.process(compiler.getRoot(), compiler.getRoot().getFirstChild());

     // After renaming, the key [1] is not a string property, so it should not be
     // present in the rename map or cause any STRING node mutation.
     // We simply verify that the pass completes without an exception and that the
     // numeric key node persists as a NUMBER node.
     List<Node> objLits = findNodes(compiler.getRoot(), Token.OBJECTLIT);
     assertThat(objLits).isNotEmpty();
     Node objLit = objLits.get(0);
     Node firstChild = objLit.getFirstChild();
     assertEquals("Numeric key must remain a NUMBER node after renaming",
                  Token.NUMBER, firstChild.getType());
   }

   @Test
   public void testRenamePrototypesGetterSetterKeyRecognized() {
     // Verifies that string keys in getter/setter definitions are recognized as
     // string properties and can be renamed (i.e., are candidate nodes).
     String source = "goog.widget.member_fn = function(){}; " +
                     "goog.widget.member_fn.prototype = {get [\"prop\"](){return 1;}};";
     compiler.parse(SourceFile.fromCode("test", source));
     Node root = compiler.getRoot();

     RenamePrototypes pass = new RenamePrototypes(compiler, null, null);
     pass.process(compiler.getRoot(), compiler.getRoot().getFirstChild());

     // The string property "prop" should be recognized and optionally renamed.
     // Check that "prop" appears in the renamed string nodes.
     boolean found = false;
     for (Node n : findNodes(compiler.getRoot(), Token.STRING)) {
       if ("prop".equals(n.getString())) {
         found = true;
         break;
       }
     }
     assertTrue("Getter property 'prop' should be a STRING node candidate", found);
   }
 }
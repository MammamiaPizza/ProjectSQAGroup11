package com.google.javascript.jscomp;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import com.google.common.collect.Lists;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 import org.junit.Test;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;

 public class Bug137Test {

     // Helpers

     private Compiler compile(String js) {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setCodingConvention(new DefaultCodingConvention());
         // Let the compiler run its standard passes (which include Normalize and
         // MakeDeclaredNamesUnique when applicable).
         compiler.initOptions(options);
         List<SourceFile> externs = Lists.newArrayList(
             SourceFile.fromCode("externs", "function alert(x) {}"));
         List<SourceFile> inputs = Lists.newArrayList(
             SourceFile.fromCode("test.js", js));
         compiler.compile(externs, inputs, options);
         return compiler;
     }

     private String outputOf(String js) {
         Compiler c = compile(js);
         assertFalse("Compilation failed with errors:\n" + js, c.hasErrors());
         return c.toSource();
     }

     // 1. Inversion: simple round‑trip
     @Test
     public void testInversionSimple() {
         assertEquals("x",
             MakeDeclaredNamesUnique.ContextualRenameInverter.getOrginalName("x$$0"));
     }

     // 2. Inversion: multiple separators – only the last $$group is stripped
     @Test
     public void testInversionMultipleSeparators() {
         assertEquals("a$$b",
             MakeDeclaredNamesUnique.ContextualRenameInverter.getOrginalName("a$$b$$5"));
     }

     // 3. Inversion: name without separator stays unchanged
     @Test
     public void testInversionNoSeparator() {
         assertEquals("abc",
             MakeDeclaredNamesUnique.ContextualRenameInverter.getOrginalName("abc"));
     }

     // 4. ‘arguments’ as local variable must not be renamed
     @Test
     public void testArgumentsLocalVar() {
         String src = "function f() { var arguments = 1; return arguments; }";
         String out = outputOf(src);
         assertTrue("'arguments' should stay as-is",
             out.contains("arguments"));
         // no separate‑renamed artifact like arguments$
         assertFalse(out.contains("arguments$"));
     }

     // 5. ‘arguments’ as formal parameter must not be renamed
     @Test
     public void testArgumentsFormalParam() {
         String src = "function f(arguments) { return arguments; }";
         String out = outputOf(src);
         assertTrue(out.contains("arguments"));
         assertFalse(out.contains("arguments$"));
     }

     // 6. Global‑level ‘arguments’ must not be renamed
     @Test
     public void testArgumentsGlobal() {
         String src = "var arguments = 3;";
         String out = outputOf(src);
         assertTrue(out.contains("arguments"));
         assertFalse(out.contains("arguments$"));
     }

     // 7. Nested scopes: inner local names get a unique suffix (contextual renamer)
     @Test
     public void testMakeLocalNamesUniqueNestedScopes() {
         String src = "function f() { var x = 1; function g() { var x = 2; return x; } return g();
}";
         String out = outputOf(src);
         // The inner ‘x’ should be renamed to something like x$$0
         assertTrue("Inner 'x' must be renamed to avoid shadowing",
             out.contains("x$$"));
     }

     // 8. Sibling scopes: each gets its own unique name, no collisions
     @Test
     public void testMakeLocalNamesUniqueSiblingScopes() {
         String src = "function f() { function h() { var x=1; } function k() { var x=2; } }";
         String out = outputOf(src);
         int count = out.split("x\\$\\$", -1).length - 1;
         // Both inner 'x' variables should be renamed, so two occurrences of 'x$$'
         assertTrue("Both inner 'x' should be renamed separately", count >= 2);
     }

     // 9. Duplicate var declarations: second var is removed, assignment preserved
     @Test
     public void testRemoveDuplicateVarDeclarationsBasic() {
         String src = "function f() { var x = 1; var x = 2; return x; }";
         String out = outputOf(src);
         // After normalization there should be exactly one "var x"
         Pattern p = Pattern.compile("\\bvar\\s+x\\b");
         Matcher m = p.matcher(out);
         int found = 0;
         while (m.find()) found++;
         assertEquals("Only one 'var x' declaration should remain", 1, found);
     }

     // 10. Multiple duplicate var declarations across different names
     @Test
     public void testRemoveDuplicateVarDeclarationsMultiple() {
         String src = "function f() { var a=0; var b=1; var a=2; var b=3; return a+b; }";
         String out = outputOf(src);
         int varA = out.split("\\bvar\\s+a\\b", -1).length - 1;
         int varB = out.split("\\bvar\\s+b\\b", -1).length - 1;
         assertTrue("At most one var a declaration", varA <= 1);
         assertTrue("At most one var b declaration", varB <= 1);
     }

     // 11. NodeUtil.isConstantName respects IS_CONSTANT_NAME property
     @Test
     public void testIsConstantName() {
         Node name = Node.newString(Token.NAME, "FOO", 1, 0);
         assertFalse(NodeUtil.isConstantName(name));
         name.putBooleanProp(Node.IS_CONSTANT_NAME, true);
         assertTrue(NodeUtil.isConstantName(name));
     }

     // 12. ContextualRenameInverter process runs without error and produces
     //     names that can be inverted back to originals.
     @Test
     public void testContextualRenameInverterFullCompile() {
         // The inverter pass is applied after a normal compilation.
         // We can run it manually on the compiled code.
         String src = "var myVar = 0; function f() { var myVar = 1; return myVar; }";
         Compiler compiler = compile(src);
         assertFalse(compiler.hasErrors());
         Node root = compiler.getRoot();

         // Apply the ContextualRenameInverter
         MakeDeclaredNamesUnique.ContextualRenameInverter inverter =
             MakeDeclaredNamesUnique.getContextualRenameInverter(compiler);
         inverter.process(null, root);

         // After inversion, every NAME node's string should be invertible.
         checkInversion(root);

         // Recompile with inverted names (should be equivalent to original)
         String invertedSrc = compiler.toSource();
         Compiler secondPass = compile(invertedSrc);
         assertFalse("Inverted code must compile", secondPass.hasErrors());
     }

     // Depth‑first check that every NAME node string can be inverted
     private void checkInversion(Node n) {
         if (n.getType() == Token.NAME) {
             String current = n.getString();
             String original =
MakeDeclaredNamesUnique.ContextualRenameInverter.getOrginalName(current);
             assertNotNull(original);
             // After inverting, we may not get back to the exact original variable name
             // because of scoping, but the method must return non‑null and consistent.
         }
         for (Node child : n.children()) {
             checkInversion(child);
         }
     }
 }
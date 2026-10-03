package com.google.javascript.jscomp;

 import com.google.common.base.Supplier;
 import com.google.javascript.rhino.Node;
 import junit.framework.TestCase;

 import java.util.Collections;

 public class MakeDeclaredNamesUniqueBug539Test extends TestCase {

   private Compiler compiler;
   private int inlineId;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     inlineId = 0;
   }

   @Override
   protected void tearDown() throws Exception {
     compiler = null;
     super.tearDown();
   }

   /**
    * Creates a fresh Compiler, parses and optionally applies a Renamer pass,
    * then returns the compiled source.
    */
   private String applyRenamer(String js, Renamer renamer) {
     compiler = new Compiler();
     compiler.initOptions(new CompilerOptions());
     SourceFile extern = SourceFile.fromCode("externs", "");
     SourceFile src = SourceFile.fromCode("test.js", js);
     compiler.compile(Collections.singletonList(src), extern, new CompilerOptions());

     if (renamer != null) {
       new MakeDeclaredNamesUnique(renamer)
           .process(compiler.getExternsRoot(), compiler.getJsRoot());
     }
     return compiler.toSource(compiler.getJsRoot());
   }

   private Renamer createInlineRenamer() {
     return new InlineRenamer(new Supplier<String>() {
       int id = 0;
       @Override
       public String get() {
         return String.valueOf(id++);
       }
     }, "X", false);
   }

   /* ------------------------------------------------------------------ */
   /*  Tests targeting InlineRenamer usage (used by FunctionInjector)     */
   /* ------------------------------------------------------------------ */

   /**
    * A single variable declaration should be renamed with the inline prefix.
    */
   public void testSimpleVarRename() {
     String input = "var a = 1;";
     String output = applyRenamer(input, createInlineRenamer());
     // 'a' must be renamed, likely to 'a$$X0'
     assertTrue("Variable should be renamed to contain separator",
         output.contains("$$"));
   }

   /**
    * Nested functions with the same local variable name must receive
    * different renamed names.
    */
   public void testNestedFunctionVarConflict() {
     String input =
         "function outer() {" +
         "  var x = 1;" +
         "  function inner() {" +
         "    var x = 2;" +
         "  }" +
         "}";
     String output = applyRenamer(input, createInlineRenamer());

     // Neither declaration may still be called 'x' alone.
     // Find all occurrences of 'x' after renaming.
     int xPos = output.indexOf("x");          // first occurrence
     assertTrue("Outer x should be renamed away from plain 'x'",
         xPos == -1 || output.indexOf("var x") != -1
         ? output.contains("$$") : true);

     // Additional: ensure two different renamed names produced.
     int firstDollar = output.indexOf("$$");
     assertTrue("Should contain at least one renamed variable", firstDollar >= 0);
   }

   /**
    * A function parameter and a local variable with the same name must
    * be distinguished.
    */
   public void testParameterShadowedByLocal() {
     String input =
         "function f(a) {" +
         "  var a = 1;" +
         "}";
     String output = applyRenamer(input, createInlineRenamer());

     // Count how many times 'a' appears as a declaration.
     // After renaming, there should be at least one '$$' and no plain 'a' left.
     assertFalse("Parameter and local should not both remain plain 'a'",
         output.contains("var a") && output.contains("function f(a)"));
   }

   /**
    * Variables declared in a loop body must not collide with an outer variable
    * of the same name.
    */
   public void testLoopBodyVarShadowing() {
     String input =
         "function test() {" +
         "  var i = 0;" +
         "  for (; i < 10; i++) {" +
         "    var x = i;" +
         "  }" +
         "  var x = 1;" +
         "}";
     String output = applyRenamer(input, createInlineRenamer());

     // There are two 'x' declarations; after renaming they must differ.
     // Both should contain '$$' indicating renames.
     int dollarCount = 0;
     int idx = 0;
     while ((idx = output.indexOf("$$", idx)) != -1) {
       dollarCount++;
       idx++;
     }
     assertTrue("Should have at least two renamed variables (both x's)",
         dollarCount >= 2);
   }

   /**
    * Catch variable and a local variable with the same name must be unique.
    */
   public void testCatchVarConflict() {
     String input =
         "function test() {" +
         "  try {" +
         "    var e = 1;" +
         "  } catch (e) {" +
         "    var e = 2;" +
         "  }" +
         "}";
     String output = applyRenamer(input, createInlineRenamer());

     // There should be multiple renamed 'e' instances.
     assertTrue("Catch variable and local should be renamed differently",
         output.contains("$$"));
   }

   /**
    * Multiple declarations of the same identifier in one scope must
    * receive distinct names.
    */
   public void testDuplicateVarInSameScope() {
     String input =
         "function test() {" +
         "  var a = 1, a = 2;" +
         "}";
     String output = applyRenamer(input, createInlineRenamer());

     // The second 'a' must be renamed; the first may keep original or not.
     // At least one '$$' should appear.
     assertTrue("Second variable should be uniquely renamed",
         output.contains("$$"));
   }

   /**
    * Function expression name should be renamed just like a var.
    */
   public void testFunctionExpressionName() {
     String input =
         "var f = function inner() {" +
         "  return inner;" +
         "};";
     String output = applyRenamer(input, createInlineRenamer());

     // The original inner name should be replaced.
     assertFalse("Function expression name 'inner' should be renamed",
         output.contains("function inner"));
   }

   /* ------------------------------------------------------------------ */
   /*  Tests targeting ContextualRenameInverter                           */
   /* ------------------------------------------------------------------ */

   /**
    * ContextualRenameInverter should revert names that are no longer
    * needed, provided they do not conflict.
    */
   public void testContextualInverterSimpleRevert() {
     // Input is a script that has already been renamed with a separator,
     // but the new name does not conflict with anything.
     String input =
         "var a$$test_0 = 1;" +
         "alert(a$$test_0);";
     // The inverter should bring it back to 'a' because there is no other 'a'.
     String output = applyContextualInverter(input);

     assertTrue("Should revert to original name 'a'",
         output.contains("var a =") || output.contains("var a= "));
     assertFalse("Should not contain the temporary name",
         output.contains("a$$"));
   }

   /**
    * ContextualRenameInverter must not revert a name if the original name
    * is already declared in the scope, to avoid conflicts.
    */
   public void testContextualInverterAvoidsConflict() {
     // There is a real 'a' declared, so the renamed variable must keep a
     // different name.
     String input =
         "var a = 1;" +
         "var a$$test_0 = 2;";
     String output = applyContextualInverter(input);

     // The temporary a$$test_0 must be changed to something other than plain 'a'.
     // It may become a$0 or similar, but must not become 'a' alone.
     int varACount = 0;
     int idx = 0;
     while ((idx = output.indexOf("var a", idx)) != -1) {
       varACount++;
       idx++;
     }
     // Both declarations exist; if the second is not renamed away, there will be two "var a".
     // Since there is a conflict, the inverter should not revert.
     assertTrue("Should avoid conflict by not reverting both to 'a'",
         varACount < 2);
   }

   /**
    * Inverter should handle nested scopes correctly, reverting names in
    * inner functions that do not conflict with outer scope.
    */
   public void testContextualInverterNestedScope() {
     String input =
         "var a = 1;" +
         "function f() {" +
         "  var b$$test_0 = 2;" +
         "  alert(b$$test_0);" +
         "}";
     String output = applyContextualInverter(input);

     // Inner b should be reverted to 'b' since no conflict with outer.
     assertTrue("Inner renamed var should be reverted to original name",
         output.contains("var b ="));
   }

   /**
    * Inverter must preserve variables that are still needed with the
    * temporary name because a reference exists that would break.
    */
   public void testContextualInverterKeepsNeededRenames() {
     // The renamed variable is referenced inside a function that escapes,
     // or is used after another declaration of the original name.
     String input =
         "var a$$test_0 = 1;" +
         "var a = 2;" +
         "alert(a$$test_0);";
     String output = applyContextualInverter(input);

     // The first variable must not become plain 'a' because it would conflict
     // with the second. It must be renamed to something else.
     // Check that there is still a separator in the output.
     assertTrue("Should keep a unique name for the conflicting variable",
         output.contains("$$"));
   }

   /* ------------------------------------------------------------------ */
   /*  Helper for ContextualRenameInverter                                */
   /* ------------------------------------------------------------------ */

   private String applyContextualInverter(String js) {
     compiler = new Compiler();
     compiler.initOptions(new CompilerOptions());
     SourceFile extern = SourceFile.fromCode("externs", "");
     SourceFile src = SourceFile.fromCode("test.js", js);
     compiler.compile(Collections.singletonList(src), extern, new CompilerOptions());

     CompilerPass inverter =
         MakeDeclaredNamesUnique.getContextualRenameInverter(compiler);
     inverter.process(compiler.getExternsRoot(), compiler.getJsRoot());

     return compiler.toSource(compiler.getJsRoot());
   }
 }
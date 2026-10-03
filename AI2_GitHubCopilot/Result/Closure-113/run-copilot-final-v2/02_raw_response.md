package com.google.javascript.jscomp;

 import com.google.common.collect.ImmutableList;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.JSError;
 import com.google.javascript.jscomp.JSSourceFile;
 import com.google.javascript.jscomp.SourceFile;
 import junit.framework.TestCase;

 import java.util.Arrays;
 import java.util.List;

 /**
  * Tests for ProcessClosurePrimitives that verify no missing provide errors
  * and no undeclared variable errors when goog.require is properly satisfied.
  * Targets defect: goog.require after goog.provide should not report
  * "namespace never provided" (and VarCheck should find no undeclared vars).
  */
 public class ProcessClosurePrimitivesBugTest extends TestCase {

   // ---- helpers ----

   private Compiler compile(List<SourceFile> externs,
                            List<SourceFile> inputs,
                            boolean enableClosurePass,
                            boolean enableVarCheck) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     options.closurePass = enableClosurePass;
     if (enableVarCheck) {
       options.checkSymbols = true;    // enables VarCheck (plus other symbol checks)
     }
     compiler.compile(externs, inputs, options);
     return compiler;
   }

   private Compiler compile(String... sourceLines) {
     ImmutableList<SourceFile> externs =
         ImmutableList.of(SourceFile.fromCode("externs.js", ""));
     ImmutableList<SourceFile> inputs =
         ImmutableList.of(SourceFile.fromCode("input.js", String.join("\n", sourceLines)));
     return compile(externs, inputs, true, true);
   }

   private Compiler compileMultipleFiles(String... srcs) {
     ImmutableList<SourceFile> externs =
         ImmutableList.of(SourceFile.fromCode("externs.js", ""));
     ImmutableList.Builder<SourceFile> inputs = ImmutableList.builder();
     for (int i = 0; i < srcs.length; i++) {
       inputs.add(SourceFile.fromCode("file" + i + ".js", srcs[i]));
     }
     return compile(externs, inputs.build(), true, true);
   }

   private void assertNoMissingProvideError(Compiler compiler) {
     for (JSError e : compiler.getErrors()) {
       assertFalse("Must not have 'never provided': " + e,
           e.description.contains("never provided"));
     }
   }

   private void assertHasMissingProvideError(Compiler compiler, String namespace) {
     boolean found = false;
     for (JSError e : compiler.getErrors()) {
       if (e.description.contains("never provided") && e.description.contains(namespace)) {
         found = true;
         break;
       }
     }
     assertTrue("Expected missing provide error for " + namespace, found);
   }

   private void assertNoUndeclaredVarError(Compiler compiler) {
     for (JSError e : compiler.getErrors()) {
       assertFalse("Must not have undeclared variable: " + e,
           e.description.contains("undeclared") || e.description.contains("variable"));
     }
   }

   // ---- test methods ----

   /** Normal: provide then require – no errors at all */
   public void testProvideThenRequireSameFile_NoError() {
     Compiler c = compile(
         "goog.provide('test.Foo');",
         "goog.require('test.Foo');"
     );
     assertNoMissingProvideError(c);
     assertNoUndeclaredVarError(c);
     assertEquals("Should have no errors", 0, c.getErrorCount());
   }

   /** The bug’s core: require appears before provide (same file) – no missing-provide */
   public void testRequireBeforeProvideSameFile_NoMissingProvide() {
     Compiler c = compile(
         "goog.require('test.Foo');",
         "goog.provide('test.Foo');"
     );
     assertNoMissingProvideError(c);
     // LATE_PROVIDE warning is allowed; we only check that it is not a missing-provide error
     // Also VarCheck should not see an undeclared variable
     assertNoUndeclaredVarError(c);
   }

   /** Multiple files: require first, provide second – still fine */
   public void testRequireInFirstFile_ProvideInSecond_NoMissingProvide() {
     Compiler c = compileMultipleFiles(
         "goog.require('test.Foo');",
         "goog.provide('test.Foo');"
     );
     assertNoMissingProvideError(c);
     assertNoUndeclaredVarError(c);
   }

   /** Multiple files: provide first, require second – the canonical safe order */
   public void testProvideInFirstFile_RequireInSecond_NoError() {
     Compiler c = compileMultipleFiles(
         "goog.provide('test.Bar');",
         "goog.require('test.Bar');"
     );
     assertNoMissingProvideError(c);
     assertNoUndeclaredVarError(c);
     assertEquals(0, c.getErrorCount());
   }

   /** Without any provide, a require must yield a missing-provide error */
   public void testRequireWithoutProvide_MissingProvideError() {
     Compiler c = compile("goog.require('test.Missing');");
     assertHasMissingProvideError(c, "test.Missing");
   }

   /** VarCheck: assignment after require + provide – no undeclared variable */
   public void testVarCheck_NoUndeclaredVar_WhenProvidedAndRequired() {
     Compiler c = compile(
         "goog.provide('test.Var');",
         "goog.require('test.Var');",
         "test.Var = 42;"
     );
     assertNoUndeclaredVarError(c);
     assertNoMissingProvideError(c);
   }

   /** VarCheck: require before provide, still no undeclared variable */
   public void testVarCheck_RequireBeforeProvide_NoUndeclaredVar() {
     Compiler c = compile(
         "goog.require('test.Var2');",
         "test.Var2 = 7;",
         "goog.provide('test.Var2');"
     );
     assertNoUndeclaredVarError(c);
     assertNoMissingProvideError(c);
   }

   /** Base-class usage: goog.inherits with require – no errors */
   public void testBaseClass_WithInheritsAndRequire_NoError() {
     Compiler c = compile(
         "goog.provide('test.Base');",
         "goog.provide('test.Sub');",
         "goog.require('test.Base');",
         "test.Sub = function() {};",
         "goog.inherits(test.Sub, test.Base);"
     );
     assertNoMissingProvideError(c);
     assertNoUndeclaredVarError(c);
   }

   /** goog.define after require – ensure define doesn’t break the pass */
   public void testDefineWithRequire_NoError() {
     Compiler c = compile(
         "goog.provide('test.Def');",
         "goog.require('test.Def');",
         "goog.define('test.Def', 1);"
     );
     assertNoMissingProvideError(c);
     assertNoUndeclaredVarError(c);
   }

   /** Deep namespace: provide/require for a dotted name */
   public void testDottedNamespace_RequireAfterProvide_NoError() {
     Compiler c = compile(
         "goog.provide('deep.ns.Foo');",
         "goog.require('deep.ns.Foo');"
     );
     assertNoMissingProvideError(c);
     assertNoUndeclaredVarError(c);
     assertEquals(0, c.getErrorCount());
   }
 }
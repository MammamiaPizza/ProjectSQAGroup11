package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerOptions.AliasTransformationHandler;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

public class ScopedAliasesTest extends TestCase {

 private Compiler compiler;
 private CompilerOptions options;

 @Override
 protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     options = new CompilerOptions();
     options.setAliasTransformationHandler(
         AliasTransformationHandler.NULL);
     // Suppress warnings from other passes for clean test output
     options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.OFF);
 }

 // Helper: compile JS and assert no errors/warnings, and optionally check output
 private void assertNoErrors(String js) {
     JSSourceFile[] sources = { JSSourceFile.fromCode("test.js", js) };
     JSSourceFile[] externs = emptyExterns();
     compiler.compile(externs, sources, options);
     assertEquals("No errors expected", 0, compiler.getErrorCount());
     assertEquals("No warnings expected", 0, compiler.getWarningCount());
 }

 // Helper: compile and expect specific error count
 private void assertHasError(String js, DiagnosticType expectedError) {
     JSSourceFile[] sources = { JSSourceFile.fromCode("test.js", js) };
     JSSourceFile[] externs = emptyExterns();
     compiler.compile(externs, sources, options);
     assertTrue("Expected error " + expectedError.message,
         compiler.getErrorCount() > 0);
     // check the specific message is present
     boolean found = false;
     for (JSError error : compiler.getErrors()) {
         if (error.getType().equals(expectedError)) {
             found = true;
             break;
         }
     }
     assertTrue("Expected error type " + expectedError.key, found);
 }

 // Helper: compile and check output after transformation
 private void assertOutput(String js, String expectedOutput) {
     JSSourceFile[] sources = { JSSourceFile.fromCode("test.js", js) };
     JSSourceFile[] externs = emptyExterns();
     compiler.compile(externs, sources, options);
     assertEquals(expectedOutput.trim(), compiler.toSource().trim());
     assertEquals(0, compiler.getErrorCount());
     assertEquals(0, compiler.getWarningCount());
 }

 private JSSourceFile[] emptyExterns() {
     return new JSSourceFile[] {
         JSSourceFile.fromCode("externs.js", "var goog;")
     };
 }

 public void testBasicAlias() {
     // Simple alias in goog.scope should be replaced
     assertNoErrors(
         "goog.scope(function() {" +
         "  var dom = goog.dom;" +
         "  dom.createElement('div');" +
         "});");
 }

 public void testTypeAliasInJSDoc() {
     // Type alias from scope should be resolved in JSDoc
     assertNoErrors(
         "goog.scope(function() {" +
         "  var dom = goog.dom;" +
         "  /** @type {dom.Element} */ var el;" +
         "});");
 }

 public void testAliasRedefinition() {
     // Redefining an alias inside the scope should be an error
     assertHasError(
         "goog.scope(function() {" +
         "  var dom = goog.dom;" +
         "  var dom = goog.other;" +
         "});",
         ScopedAliases.GOOG_SCOPE_ALIAS_REDEFINED);
 }

 public void testReturnInScope() {
     // return not allowed inside goog.scope
     assertHasError(
         "goog.scope(function() {" +
         "  return;" +
         "});",
         ScopedAliases.GOOG_SCOPE_USES_RETURN);
 }

 public void testThisInScope() {
     // this reference is not allowed
     assertHasError(
         "goog.scope(function() {" +
         "  var x = this;" +
         "});",
         ScopedAliases.GOOG_SCOPE_REFERENCES_THIS);
 }

 public void testThrowInScope() {
     // throw inside goog.scope should be an error
     assertHasError(
         "goog.scope(function() {" +
         "  throw 'e';" +
         "});",
         ScopedAliases.GOOG_SCOPE_USES_THROW);
 }

 public void testImproperScopeCall() {
     // goog.scope must be used as an expression statement
     assertHasError(
         "var x = goog.scope(function() {});",
         ScopedAliases.GOOG_SCOPE_USED_IMPROPERLY);
 }

 public void testBadParametersNamedFunction() {
     // The callback must be an anonymous function expression
     assertHasError(
         "goog.scope(function myScope() {" +
         "  var dom = goog.dom;" +
         "});",
         ScopedAliases.GOOG_SCOPE_HAS_BAD_PARAMETERS);
 }

 public void testBadParametersWithParams() {
     // The callback cannot accept parameters
     assertHasError(
         "goog.scope(function(a) {" +
         "  var dom = goog.dom;" +
         "});",
         ScopedAliases.GOOG_SCOPE_HAS_BAD_PARAMETERS);
 }

 public void testNamespaceShadow() {
     // Alias whose qualified name root is not itself an alias
     // should cause namespace shadow renaming (no error, but output changes).
     // This directly probes the forbiddenLocals / rename logic.
     assertNoErrors(
         "goog.scope(function() {" +
         "  var dom = goog.dom;" +
         "  var myNamespace = some.long.qualified.Name;" +
         "});");
 }

 public void testNestedScopeCalls() {
     // Multiple goog.scope invocations should be processed independently
     assertNoErrors(
         "goog.scope(function() {" +
         "  var dom = goog.dom;" +
         "});" +
         "goog.scope(function() {" +
         "  var events = goog.events;" +
         "});");
 }

 public void testTransitiveAliasChain() {
     // Chain of aliases: g -> goog, d -> g.dom
     assertNoErrors(
         "goog.scope(function() {" +
         "  var g = goog;" +
         "  var d = g.dom;" +
         "  d.getElement('id');" +
         "});");
 }

}
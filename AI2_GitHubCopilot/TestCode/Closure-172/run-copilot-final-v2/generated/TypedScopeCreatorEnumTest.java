package com.google.javascript.jscomp;

import junit.framework.TestCase;
import java.util.List;

public class TypedScopeCreatorEnumTest extends TestCase {

 private List<JSError> compileAndGetWarnings(String js) {
     Compiler compiler = new Compiler();
     compiler.initOptions(new CompilerOptions());
     SourceFile externs = SourceFile.fromCode("externs", "");
     SourceFile input = SourceFile.fromCode("input.js", js);
     compiler.compile(externs, input);
     return compiler.getWarnings();
 }

 private void assertNoWarningContains(String js, String warningSubstring) {
     List<JSError> warnings = compileAndGetWarnings(js);
     for (JSError warning : warnings) {
         if (warning.description.contains(warningSubstring)) {
             fail("Unexpected warning: " + warning.description);
         }
     }
 }

 private void assertHasWarning(String js, String warningSubstring) {
     List<JSError> warnings = compileAndGetWarnings(js);
     for (JSError warning : warnings) {
         if (warning.description.contains(warningSubstring)) {
             return;
         }
     }
     fail("Expected warning containing: " + warningSubstring);
 }

 public void testEnumObjectLiteralNoWarning() {
     assertNoWarningContains(
             "/** @enum {number} */ var E = { A: 1, B: 2 };",
             "enum initializer must be an object literal or an enum");
 }

 public void testEnumNonLiteralInitializerWarning() {
     assertHasWarning(
             "/** @enum {number} */ var E = someFunction();",
             "enum initializer must be an object literal or an enum");
 }

 public void testEnumNumberLiteralWarning() {
     assertHasWarning(
             "/** @enum {number} */ var E = 42;",
             "enum initializer must be an object literal or an enum");
 }

 public void testEnumStringLiteralWarning() {
     assertHasWarning(
             "/** @enum {string} */ var E = 'hello';",
             "enum initializer must be an object literal or an enum");
 }

 public void testEnumNewObjectWarning() {
     assertHasWarning(
             "/** @enum {number} */ var E = new Object();",
             "enum initializer must be an object literal or an enum");
 }

 public void testEnumInsideFunctionNoWarning() {
     assertNoWarningContains(
             "function f() { /** @enum {number} */ var E = { X:1 }; return E; }",
             "enum initializer must be an object literal or an enum");
 }

 public void testEmptyEnumLiteralNoWarning() {
     assertNoWarningContains(
             "/** @enum {number} */ var E = {};",
             "enum initializer must be an object literal or an enum");
 }

 public void testTypedefFunctionNoEnumWarning() {
     assertNoWarningContains(
             "/** @typedef {function()} */ var Func = function() {};",
             "enum initializer must be an object literal or an enum");
 }

 public void testEnumAssignedToAnotherEnumNoWarning() {
     // Enum initializer can be an enum.
     assertNoWarningContains(
             "/** @enum {number} */ var E = { A:1 }; /** @enum {number} */ var F = E;",
             "enum initializer must be an object literal or an enum");
 }

 public void testMissingAnnotationNoEnumWarning() {
     assertNoWarningContains(
             "var E = { A: 1 };",
             "enum initializer must be an object literal or an enum");
 }

 public void testEnumDefinedInNestedScopeNoWarning() {
     assertNoWarningContains(
             "var E; if (true) { /** @enum {number} */ E = { A:1 }; }",
             "enum initializer must be an object literal or an enum");
 }

 public void testEnumWithNonConstantPropertyValues() {
     // Type mismatch inside enum properties should not trigger the enum initializer warning
     assertNoWarningContains(
             "/** @enum {number} */ var E = { A: 'not-a-number' };",
             "enum initializer must be an object literal or an enum");
 }

}

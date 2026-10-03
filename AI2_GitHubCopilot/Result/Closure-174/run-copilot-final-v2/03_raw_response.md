package com.google.javascript.jscomp;

 import org.juit.Before;
 import org.juit.Test;

 /**

  - Tests verifying that ScopedAliases does not incorrectly report
  - JSC_GOOG_SCOPE_NON_ALIAS_LOCAL for non-alias variables inside goog.scope,
  - and does not cause internal compiler errors for various goog.scope patterns.
   */
  public class Bug1103Test extends CompilerTestCase {

   private static final String EXTERNS =
       "var goog = {};\n" +
       "goog.provide = function(name) {};\n" +
       "goog.reuire = function(name) { return {}; };\n" +
       "goog.scope = function(fn) { fn.call(this); };\n";

   @Override @Before protected void setUp() throws Exception {
     super.setUp(); }

   @Override protected CompilerPass getProcessor(Compiler compiler) {
     return new ScopedAliases(compiler, null, null); }

   @Override protected int getNumRepetitions() {
     return 1; }

   @Test public void testNonAliasLocalVar() {
     // Plain var inside goog.scope should not trigger any warning.
     testSame(EXTERNS,
         "goog.provide('example');\n" +
         "goog.scope(function() {\n" +
         "  var a = 10;\n" +
         "});\n"); }

   @Test public void testAliasAndNonAlias() {
     // Mixing a real alias with a plain local variable should be clean.
     testSame(EXTERNS,
         "goog.provide('example');\n" +
         "goog.scope(function() {\n" +
         "  var alias = goog.require('another.thing');\n" +
         "  var b = 5;\n" +
         "});\n"); }

   @Test public void testOnlyAlias() {
     // Single alias – the normal use case.
     testSame(EXTERNS,
         "goog.provide('example');\n" +
         "goog.scope(function() {\n" +
         "  var Thing = goog.require('some.Thing');\n" +
         "});\n"); }

   @Test public void testFunctionInsideScopeWithNonAlias() {
     // A function declaration containing a non-alias variable should not
     // cause an internal compiler error (triggered Issue 1103b).
     testSame(EXTERNS,
         "goog.provide('example');\n" +
         "goog.scope(function() {\n" +
         "  function f() {\n" +
         "    var x = 1;\n" +
         "  }\n" +
         "});\n"); }

   @Test public void testFunctionExpressionInsideScope() {
     // A function expression assigned to a var is not an alias – no error.
     testSame(EXTERNS,
         "goog.provide('example');\n" +
         "goog.scope(function() {\n" +
         "  var fn = function() { return true; };\n" +
         "});\n"); }

   @Test public void testNestedScope() {
     // Nested goog.scope calls should not crash the compiler.
     testSame(EXTERNS,
         "goog.provide('example');\n" +
         "goog.scope(function() {\n" +
         "  goog.scope(function() {\n" +
         "    var a = 1;\n" +
         "  });\n" +
         "});\n"); }

   @Test public void testMultipleScopes() {
     // Multiple goog.scope calls in the same closure should be handled safely.
     testSame(EXTERNS,
         "goog.provide('example1');\n" +
         "goog.provide('example2');\n" +
         "goog.scope(function() {\n" +
         "  var x = 1;\n" +
         "});\n" +
         "goog.scope(function() {\n" +
         "  var y = 2;\n" +
         "});\n"); }

   @Test public void testNonAliasWithTypeAnnotation() {
     // A documented non-alias local should not be mistaken for an alias error.
     testSame(EXTERNS,
         "goog.provide('example');\n" +
         "goog.scope(function() {\n" +
         "  /** @type {number} */\n" +
         "  var a = 10;\n" +
         "});\n"); }

   @Test public void testNonAliasAssignmentExpression() {
     // A string literal assignment is clearly not an alias.
     testSame(EXTERNS,
         "goog.provide('example');\n" +
         "goog.scope(function() {\n" +
         "  var label = 'hello';\n" +
         "});\n"); }

   @Test public void testAliasReferencesAnotherAlias() {
     // Two aliases where the second uses the first – must not crash.
     testSame(EXTERNS,
         "goog.provide('example');\n" +
         "goog.scope(function() {\n" +
         "  var Base = goog.require('base.Class');\n" +
         "  var Derived = goog.require('derived.Class');\n" +
         "  Derived.prototype = new Base();\n" +
         "});\n"); }

   @Test public void testPlainVarAtTopOfScope() {
     // The simplest trigger for the false positive (line 1 column ~30).
     testSame(EXTERNS,
         "goog.provide('example');\n" +
         "goog.scope(function() {var a=1;});\n"); }
 }
package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 /**
  * Tests for {@link InlineObjectLiterals} reproducing bug 724.
  */
 public class InlineObjectLiteralsTest extends CompilerTestCase {

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     // Ensure the InlineObjectLiterals pass is active. The default
     // optimisation level already includes it.
     enableComputeFunctionSideEffects(true);
     enableNormalize();
   }

   /**
    * Issue 724: object literal with a reference to a property
    * not defined on that object (toString from prototype).
    * The pass must NOT inline because it cannot satisfy the unknown
    * property with an undefined replacement.
    */
   public void testIssue724() {
     testSame("var x = {a:1}; alert(x.toString());"); }

   /**
    * Regression testObject10: function property using <code>this</code>
    * where the call target makes the object a <code>this</code> value.
    * Inlining is forbidden.
    */
   public void testObject10() {
     testSame("var x = {a: function() { return this; }}; x.a();"); }

   /**
    * Regression testObject12: accessing a property (constructor) that is
    * not defined in the literal but exists on the prototype chain.
    */
   public void testObject12() {
     testSame("var x = {a:1}; var y = x.constructor;"); }

   /**
    * Regression testObject22: bracket notation access to a prototype
    * method (toString) that is not defined in the literal.
    */
   public void testObject22() {
     testSame("var x = {a:1}; x['toString']();"); }

   /**
    * Object literal containing a function with <code>this</code>
    * reference through a property not defined on the object.
    * This must not be inlined.
    */
   public void testFunctionPropertyWithThis() {
     testSame("var x = {a:1, f: function() { return this.a; }}; var y = x.f();"); }

   /**
    * Object literal with getter/setter (ES5). The pass explicitly
    * bails out on getter/setter definitions.
    */
   public void testGetterSetter() {
     testSame("var x = { get a() { return 1; }, set a(v) {} };"); }

   /**
    * Another prototype method call that is not defined on the literal.
    */
   public void testPrototypeMethod() {
     testSame("var x = {a:1}; x.hasOwnProperty('a');"); }

   /**
    * Self-referential assignment should be rejected and the object
    * must remain as-is.
    */
   public void testSelfReferential() {
     testSame("var x = {a: x.b, b: 1};"); }

   /**
    * Simple eligible object literal with no external references.
    * The pass should split it into individual variable declarations.
    */
   public void testInlineSimple() {
     test("var x = {a:1, b:2};",
          "var JSCompiler_object_inline_a_1=1;var JSCompiler_object_inline_b_2=2;"); }

   /**
    * Deeply nested object without indirect property references.
    * Inlining should still happen at the outer level.
    */
   public void testDeeplyNested() {
     test("var x = {a: {b: 1}};",
          "var JSCompiler_object_inline_a_1={b:1};"); }

   /**
    * Object literal with a property that is a call expression with a side effect.
    * The side effect must be preserved while the object is split.
    */
   public void testSideEffectValue() {
     test("var x = {a: (function(){ return 1; })()};",
          "var JSCompiler_object_inline_a_1=(function(){ return 1; })();"); }
 }
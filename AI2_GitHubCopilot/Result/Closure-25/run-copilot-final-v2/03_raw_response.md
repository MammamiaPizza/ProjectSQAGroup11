package com.google.javascript.jscomp;

 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.Node;

 public class TypeInferenceTest extends CompilerTestCase {

   public TypeInferenceTest(String name) {
     super(name);
   }

   public void testBackwardsInferenceNew() {
     testTypes(
         "/** @return {number} */ function C() { return 5; }" +
         "var x = {}; x.foo = new C();",
         "x.foo", "(number|undefined)");
   }

   public void testBackwardsInferenceNewVoid() {
     testTypes(
         "/** @return {void} */ function C() {}" +
         "var x = {}; x.foo = new C();",
         "x.foo", "(undefined|undefined)");
   }

   public void testBackwardsInferenceNewObjectReturn() {
     testTypes(
         "/** @return {Array} */ function C() { return []; }" +
         "var x = {}; x.foo = new C();",
         "x.foo", "(Array|undefined)");
   }

   public void testBackwardsInferenceNewNestedProperty() {
     testTypes(
         "/** @return {number} */ function C() { return 5; }" +
         "var x = {}; x.y = {}; x.y.z = new C();",
         "x.y.z", "(number|undefined)");
   }

   public void testBackwardsInferenceNewExistingProperty() {
     testTypes(
         "/** @return {number} */ function C() { return 5; }" +
         "var x = {foo:1}; x.foo = new C();",
         "x.foo", "number");
   }

   public void testBackwardsInferenceNewReassign() {
     testTypes(
         "/** @return {number} */ function C() { return 5; }" +
         "/** @return {string} */ function D() { return 'a'; }" +
         "var x = {}; x.foo = new C(); x.foo = new D();",
         "x.foo", "(number|string)");
   }

   public void testBackwardsInferenceNewConditional() {
     testTypes(
         "/** @return {number} */ function C() { return 5; }" +
         "var x = {}; if (true) { x.foo = new C(); } else { x.foo = new C(); }",
         "x.foo", "(number|undefined)");
   }

   public void testBackwardsInferenceNewLoop() {
     testTypes(
         "/** @return {number} */ function C() { return 5; }" +
         "var x = {}; for (var i = 0; i < 10; i++) { x.foo = new C(); }",
         "x.foo", "(number|undefined)");
   }

   public void testBackwardsInferenceNewRegularCall() {
     testTypes(
         "/** @return {number} */ function C() { return 5; }" +
         "var x = {}; x.foo = C();",
         "x.foo", "(number|undefined)");
   }

   public void testBackwardsInferenceNewUnknownReturn() {
     testTypes(
         "/** @return {?} */ function C() { return 5; }" +
         "var x = {}; x.foo = new C();",
         "x.foo", "(?|undefined)");
   }

   public void testBackwardsInferenceNewNoAnnotation() {
     testTypes(
         "function C() { return 5; }" +
         "var x = {}; x.foo = new C();",
         "x.foo", "?");
   }

   public void testBackwardsInferenceNewMultipleProps() {
     testTypes(
         "/** @return {number} */ function C() { return 5; }" +
         "var x = {}; x.foo = new C(); x.bar = new C();",
         "x.foo", "(number|undefined)",
         "x.bar", "(number|undefined)");
   }
 }
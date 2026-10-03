package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 /**
  * Regression tests for Bug 86: @implements and @extends handling
  * in AmbiguateProperties and TypedScopeCreator.
  */
 public class Bug86ImplementsExtendsTest extends TestCase {

     private Compiler compiler;
     private CompilerOptions options;

     @Override
     protected void setUp() throws Exception {
         super.setUp();
         compiler = new Compiler();
         options = new CompilerOptions();
     }

     @Override
     protected void tearDown() throws Exception {
         super.tearDown();
     }

     private void compile(String js) {
         compile(js, "");
     }

     private void compile(String js, String externs) {
         compiler.initOptions(options);
         compiler.compile(
             SourceFile.fromCode("externs.js", externs),
             SourceFile.fromCode("test.js", js));
     }

     /**
      * Test 1: Basic class implements interface with property.
      * Targets AmbiguateProperties.recordProperty, TypedScopeCreator.defineSlot
      */
     public void testClassImplementsInterface() throws Exception {
         String js =
             "/** @interface */ function I() {}" +
             "/** @type {number} */ I.prototype.x = 0;" +
             "/** @constructor @implements {I} */ function C() {}" +
             "/** @type {number} */ C.prototype.x = 0;";
         compile(js);
         assertEquals(0, compiler.getErrorCount());
     }

     /**
      * Test 2: Class extends another class that implements interface.
      * Targets AmbiguateProperties.computeRelatedTypes
      */
     public void testExtendsImplementsChain() throws Exception {
         String js =
             "/** @interface */ function I() {}" +
             "/** @type {number} */ I.prototype.p = 0;" +
             "/** @constructor @implements {I} */ function A() {}" +
             "/** @type {number} */ A.prototype.p = 1;" +
             "/** @constructor @extends {A} */ function B() {}" +
             "/** @type {number} */ B.prototype.p = 2;";
         compile(js);
         assertEquals(0, compiler.getErrorCount());
     }

     /**
      * Test 3: Multiple interfaces declare same property name.
      * Targets AmbiguateProperties.process
      */
     public void testMultipleInterfacesSameProperty() throws Exception {
         String js =
             "/** @interface */ function I1() {}" +
             "/** @type {string} */ I1.prototype.name = '';" +
             "/** @interface */ function I2() {}" +
             "/** @type {string} */ I2.prototype.name = '';" +
             "/** @constructor @implements {I1, I2} */ function C() {}" +
             "/** @type {string} */ C.prototype.name = '';";
         compile(js);
         assertEquals(0, compiler.getErrorCount());
     }

     /**
      * Test 4: Property used across both extends and implements levels.
      * Targets AmbiguateProperties.maybeMarkCandidate
      */
     public void testPropertyAcrossExtendsAndImplements() throws Exception {
         String js =
             "/** @interface */ function I() {}" +
             "/** @type {boolean} */ I.prototype.flag = false;" +
             "/** @constructor @implements {I} */ function Base() {}" +
             "/** @type {boolean} */ Base.prototype.flag = false;" +
             "/** @constructor @extends {Base} */ function Sub() {}" +
             "/** @type {boolean} */ Sub.prototype.flag = true;";
         compile(js);
         assertEquals(0, compiler.getErrorCount());
     }

     /**
      * Test 5: @implements referencing non-interface (Bug 86 core).
      * Should produce a warning.
      */
     public void testImplementsNonInterfaceWarning() throws Exception {
         options.setWarningLevel(
             DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);
         String js =
             "/** @constructor */ function Foo() {}" +
             "/** @constructor @implements {Foo} */ function Bar() {}";
         compile(js);
         assertTrue("Expected warnings for @implements on non-interface",
                    compiler.getWarningCount() > 0 || compiler.getErrorCount() > 0);
     }

     /**
      * Test 6: @extends referencing non-constructor (should warn).
      */
     public void testExtendsNonConstructorWarning() throws Exception {
         options.setWarningLevel(
             DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);
         String js =
             "/** @type {Object} */ var x = {};" +
             "/** @constructor @extends {x} */ function Foo() {}";
         compile(js);
         assertTrue("Expected warnings for @extends non-constructor",
                    compiler.getWarningCount() > 0 || compiler.getErrorCount() > 0);
     }

     /**
      * Test 7: Interface method signature mismatch in implementing class.
      */
     public void testInterfaceMethodSignatureMismatch() throws Exception {
         options.setWarningLevel(
             DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);
         String js =
             "/** @interface */ function I() {}" +
             "/** @param {string} s @return {number} */ I.prototype.f = function(s) {};" +
             "/** @constructor @implements {I} */ function C() {}" +
             "/** @param {number} n @return {string} @override */ C.prototype.f = function(n) {};";
         compile(js);
         assertTrue("Expected warnings for method signature mismatch",
                    compiler.getWarningCount() > 0);
     }

     /**
      * Test 8: Interface property type mismatch with implementing class.
      */
     public void testInterfacePropertyTypeMismatch() throws Exception {
         options.setWarningLevel(
             DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);
         String js =
             "/** @interface */ function I() {}" +
             "/** @type {number} */ I.prototype.x = 0;" +
             "/** @constructor @implements {I} */ function C() {}" +
             "/** @type {string} */ C.prototype.x = '';";
         compile(js);
         assertTrue("Expected warnings for property type mismatch",
                    compiler.getWarningCount() > 0 || compiler.getErrorCount() > 0);
     }

     /**
      * Test 9: Interface extends another interface.
      * Targets TypedScopeCreator defineSlot with sub-interfaces
      */
     public void testInterfaceExtendsInterface() throws Exception {
         String js =
             "/** @interface */ function I1() {}" +
             "/** @type {number} */ I1.prototype.a = 0;" +
             "/** @interface @extends {I1} */ function I2() {}" +
             "/** @type {number} */ I2.prototype.b = 0;" +
             "/** @constructor @implements {I2} */ function C() {}" +
             "/** @type {number} */ C.prototype.a = 0;" +
             "/** @type {number} */ C.prototype.b = 0;";
         compile(js);
         assertEquals(0, compiler.getErrorCount());
     }

     /**
      * Test 10: Deep inheritance chain with implements (3+ levels).
      * Targets computeRelatedTypes with deep chains
      */
     public void testDeepImplementsInheritanceChain() throws Exception {
         String js =
             "/** @interface */ function I() {}" +
             "/** @type {number} */ I.prototype.val = 0;" +
             "/** @constructor @implements {I} */ function A() {}" +
             "/** @type {number} */ A.prototype.val = 1;" +
             "/** @constructor @extends {A} */ function B() {}" +
             "/** @type {number} */ B.prototype.val = 2;" +
             "/** @constructor @extends {B} */ function C() {}" +
             "/** @type {number} */ C.prototype.val = 3;";
         compile(js);
         assertEquals(0, compiler.getErrorCount());
     }

     /**
      * Test 11: Missing JSDoc type on interface property.
      * Targets CollectProperties.visit
      */
     public void testMissingJSDocTypeOnInterfaceProperty() throws Exception {
         String js =
             "/** @interface */ function I() {}" +
             "I.prototype.p = 0;" +
             "/** @constructor @implements {I} */ function C() {}" +
             "/** @type {number} */ C.prototype.p = 0;";
         compile(js);
         assertTrue("Should not crash with missing type annotation",
                    compiler.getErrorCount() == 0);
     }

     /**
      * Test 12: Union type property in implements chain.
      * Targets AmbiguateProperties.recordProperty with complex types
      */
     public void testUnionTypePropertyInImplements() throws Exception {
         String js =
             "/** @interface */ function I() {}" +
             "/** @type {number|string} */ I.prototype.id = 0;" +
             "/** @constructor @implements {I} */ function A() {}" +
             "/** @type {number|string} */ A.prototype.id = 0;" +
             "/** @constructor @extends {A} */ function B() {}" +
             "/** @type {number} */ B.prototype.id = 0;";
         compile(js);
         assertTrue("Should compile without errors",
                    compiler.getErrorCount() == 0);
     }
 }
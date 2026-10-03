package com.google.javascript.jscomp;

import junit.framework.TestCase;

/**

 - Tests for TypedScopeCreator's handling of @lends annotations,
 - specifically detection of inconsistent return types (bug 314).
  */
 public class TypedScopeCreatorLendsTest extends CompilerTestCase {

  @Override public void setUp() {
    super.setUp();
    // Enable type checking so that inconsistent return type errors are triggered
    enableTypeCheck(CheckLevel.ERROR);
    // Disable ambiguate properties, as it can mask errors
    disambiguateProperties(); }

  /**

 - Normal case: @lends with a method whose return type matches the
 - existing prototype method's return type – no error.
    /
   public void testLendsMatchingReturnType() {
 testSame(
    "/* @constructor / function Foo() {}" +
    "Foo.prototype.bar = function() { return 1; };" +
    "/* @lends {Foo.prototype}
  */ ({bar: function() { return 2; }})");
   }

  /**

 - Error case (testLends10): @lends method returns string while
 - prototype method returns number – inconsistent return type.
    /
   public void testLendsMismatchedPrimitiveReturn() {
 testSame(
    "/* @constructor / function Foo() {}" +
    "Foo.prototype.bar = function() { return 1; };" +
    "/* @lends {Foo.prototype}
  */ ({bar: function() { return ''; }})",
    "inconsistent return type");
   }

  /**

 - Error case (testLends11): mismatched object return type
 - (Number vs Array).
    /
   public void testLendsMismatchedObjectReturn() {
 testSame(
    "/* @constructor / function Foo() {}" +
    "/* @return {Number} / Foo.prototype.bar = function() {};" +
    "/* @return {Array} / " +
    "/* @lends {Foo.prototype}
  */ ({baz: function() { return []; }})",
    "inconsistent return type");
   }

  /**

 - Error: one method with mismatched return among several matching ones
 - still triggers the error.
    /
   public void testLendsOneMismatchedAmongMany() {
 testSame(
    "/* @constructor / function Foo() {}" +
    "Foo.prototype.a = function() { return 1; };" +
    "Foo.prototype.b = function() { return 'a'; };" +
    "/* @lends {Foo.prototype}
  */ ({a: function() { return 2; }," +
    "b: function() { return true; }})",
    "inconsistent return type");
   }

  /**

 - No conflict when the property does not exist on the target prototype
 - before the @lends annotation.
    /
   public void testLendsMissingPrototypePropertyNoError() {
 testSame(
    "/* @constructor / function Foo() {}" +
    "/* @lends {Foo.prototype}
  */ ({newMethod: function() { return 1; }})");
   }

  /**

 - Multiple @lends annotations on the same object literal – both
 - targets must be checked for inconsistent return types.
    /
   public void testMultipleLendsOnSameLiteral() {
 testSame(
    "/* @constructor / function Foo() {}" +
    "Foo.prototype.x = function() { return 1; };" +
    "/* @constructor / function Bar() {}" +
    "Bar.prototype.y = function() { return 'a'; };" +
    "/* @lends {Foo.prototype} /" +
    "/* @lends {Bar.prototype} /" +
    "({x: function() { return ''; }, y: function() { return 1; }})",
    "inconsistent return type" / at least one mismatch expected
  */);
   }

  /**

 - Nested @lends: an inner object literal with @lends that
 - conflicts with an outer @lends target.
    /
   public void testNestedLendsWithConflict() {
 testSame(
    "/* @constructor / function Outer() {}" +
    "Outer.prototype.method = function() { return 1; };" +
    "/* @lends {Outer.prototype} / (" +
    "  {inner: /* @lends {Outer.prototype}
  */ (" +
    "    {method: function() { return ''; }}" +
    "  )}" +
    ")",
    "inconsistent return type");
   }

  /**

 - @lends to a plain object (not a prototype) with a conflicting
 - return type on its property.
    /
   public void testLendsToInstanceNotPrototype() {
 testSame(
    "/* @constructor / function Foo() {}" +
    "var foo = new Foo();" +
    "/* @type {number} / foo.bar;" +
    "/* @lends {foo}
  */ ({bar: function() { return 'str'; }})",
    "inconsistent return type");
   }

  /**

 - @lends when the literal itself declares a function that returns
 - a different type than expected – using @return annotation to
 - make the mismatch explicit.
    /
   public void testLendsWithExplicitAnnotationMismatch() {
 testSame(
    "/* @constructor / function Foo() {}" +
    "/* @return {string} / Foo.prototype.m = function() {};" +
    "/* @return {number} /" +
    "/* @lends {Foo.prototype}
  */ ({m: function() { return 1; }})",
    "inconsistent return type");
   }

  /**

 - Boundary: @lends with a method that returns void where a
 - non-void return is expected.
    /
   public void testLendsVoidVsNumberReturn() {
 testSame(
    "/* @constructor / function Foo() {}" +
    "/* @return {number} / Foo.prototype.m = function() {};" +
    "/* @return {void} /" +
    "/* @lends {Foo.prototype}
  */ ({m: function() {}})",
    "inconsistent return type");
   }

  /**

 - No conflict when the @lends literal returns EXACTLY the same
 - function type as the prototype (matching signatures).
    /
   public void testLendsMatchingFunctionTypeSignature() {
 testSame(
    "/* @constructor / function Foo() {}" +
    "/* @return {number} / Foo.prototype.m = function() {};" +
    "/* @return {number} /" +
    "/* @lends {Foo.prototype}
  */ ({m: function() { return 1; }})");
   }
 }

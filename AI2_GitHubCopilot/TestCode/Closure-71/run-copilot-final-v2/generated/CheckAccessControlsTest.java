package com.google.javascript.jscomp;

public class CheckAccessControlsTest extends CompilerTestCase {

  public CheckAccessControlsTest() {
    super(); }

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckAccessControls(compiler); }

  @Override protected int getNumRepetitions() {
    return 1; }

  public void testPrivateNameInSameFile() {
    testSame("/** @private */ var x = 1; function f(){ return x; }"); }

  public void testPrivateAccessFromDifferentFile() {
    String externs = "/** @constructor / function Foo() {} /* @private */ Foo.prototype.x = 1;";
    String js = "var a = new Foo(); a.x;";
    test(externs, js, CheckAccessControls.BAD_PRIVATE_PROPERTY_ACCESS); }

  public void testPrivateOverrideGlobalScope() {
    String externs = "/** @constructor / function Foo() {} /* @private */ Foo.prototype.x = 1;";
    String js = "Foo.prototype.x = 2;";
    test(externs, js, CheckAccessControls.PRIVATE_OVERRIDE); }

  public void testPrivateOverrideInFunction() {
    String externs = "/** @constructor / function Foo() {} /* @private */ Foo.prototype.x = 1;";
    String js = "function f() { Foo.prototype.x = 2; } f();";
    test(externs, js, CheckAccessControls.PRIVATE_OVERRIDE); }

  public void testPrivateOverrideSameFile() {
    testSame("/** @constructor / function Foo() {} /* @private */ Foo.prototype.x = 1;
Foo.prototype.x = 2;"); }

  public void testPrivateAssignInConstructor() {
    testSame("/** @constructor / function Foo() { this.x = 2; } /* @private */ Foo.prototype.x =
1;"); }

  public void testPrivateMethodOverride() {
    String externs = "/** @constructor / function Foo() {} /* @private */ Foo.prototype.method =
function(){};";
    String js = "Foo.prototype.method = function(){};";
    test(externs, js, CheckAccessControls.PRIVATE_OVERRIDE); }

  public void testProtectedOverrideSubclass() {
    String externs = "/** @constructor / function Base() {} /* @protected / Base.prototype.x = 1;";
    String js = "/* @constructor @extends {Base} / function Sub(){} /* @override */ Sub.prototype.x
= 2;";    testSame(externs, js); }

  public void testPublicOverride() {
    String externs = "/** @constructor */ function Foo() {} Foo.prototype.x = 1;";    String js =
"Foo.prototype.x = 2;";    testSame(externs, js); }

  public void testVisibilityMismatch() {
    String externs = "/** @constructor / function Foo() {} /* @protected / Foo.prototype.x = 1;";
 String js = "/* @public */ Foo.prototype.x = 2;";    test(externs, js,
CheckAccessControls.VISIBILITY_MISMATCH); }

  public void testProtectedAccessFromNonSubclass() {
    String externs = "/** @constructor / function Base() {} /* @protected */ Base.prototype.x = 1;";
   String js = "var b = new Base(); b.x;";    test(externs, js,
CheckAccessControls.BAD_PROTECTED_PROPERTY_ACCESS); }

  public void testNonOverridePrivateUse() {
    testSame("/** @private */ var x = 1; var y = x;"); }
}

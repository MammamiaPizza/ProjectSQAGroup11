package com.google.javascript.jscomp;

 public class Bug136Test extends CompilerTestCase {

     private CompilerPass currentPass;

     @Override
     public void setUp() throws Exception {
         super.setUp();
         currentPass = null;
     }

     @Override
     public CompilerPass getProcessor(Compiler compiler) {
         return currentPass;
     }

     // From testIssue2508576_1: getter property used with no arguments; not a signature, inline
succeeds.
     public void testIssue2508576_1() {
         String js = "var obj = {a: function() {return alert;}, b: function() {return alert;}};
obj.a('a');";
         String expected = "({a:alert,b:alert}).a(\"a\")";
         test(js, expected);
     }

     // From testIssue2508576_3: verify no INTERNAL COMPILER ERROR is thrown
     public void testIssue2508576_3() {
         String js = "function f(){} f(1);";
         testSame(js);
     }

     // From testSeparateMethods: two separate methods with same name, call should not cause
JSC_WRONG_ARGUMENT_COUNT
     public void testSeparateMethods() {
         String js = "var a = {oneOrTwoArg2: function(x){}};"
                 + "var b = {oneOrTwoArg2: function(x,y){}};"
                 + "a.oneOrTwoArg2(1,2,3);";
         testSame(js);
     }

     // From testDollarSignSuperExport2: $export$ prefix should not be renamed
     public void testDollarSignSuperExport2() {
         currentPass = new RenameVars(compiler, "", false, false, false, null, null, null);
         test("var $export$foo=1; var bar=2; alert($export$foo); alert(bar);",
                 "var $export$foo=1; var a=2; alert($export$foo); alert(a);");
     }

     // $super$ prefix should also be preserved
     public void testDollarSignSuperExport2_Super() {
         currentPass = new RenameVars(compiler, "", false, false, false, null, null, null);
         test("var $super$foo=1; var $export$bar=2; var x=3;",
                 "var $super$foo=1; var $export$bar=2; var a=3;");
     }

     // Extern method accessed as property (not a call) → no signature added
     public void testExternPropertyAccessNoSignature() {
         testSame("location.href = ']8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;';");]8;;
     }

     // Extern method called with wrong argument count should still warn
     public void testExternMethodWrongArgCount() {
         testError("setTimeout();", JSError.WRONG_ARGUMENT_COUNT);
     }

     // Normal inlining of getter works correctly
     public void testInlineGetterNormal() {
         String js = "var o = {get x(){return 42;}}; alert(o.x);";
         test(js, "alert(42);");
     }

     // Method defined on prototype should collect signature
     public void testPrototypeMethodSignature() {
         testSame("function Foo(){} Foo.prototype.bar = function(a){}; (new Foo).bar(1);");
     }

     // Static method definition should collect signature
     public void testStaticMethodSignature() {
         testSame("var Bar = {}; Bar.baz = function(a,b){}; Bar.baz(1,2);");
     }

     // RenameVars renames normal globals
     public void testRenameNormalGlobal() {
         currentPass = new RenameVars(compiler, "", false, false, false, null, null, null);
         test("var myLongVar=1; alert(myLongVar);",
                 "var a=1; alert(a);");
     }

     // Multiple definitions of same name in separate scopes should not merge signatures
     public void testSeparateScopesDontMergeSignatures() {
         testSame(
                 "(function(){ var foo = function(x){}; foo(1,2); })();"
                         + "(function(){ var foo = function(x,y){}; foo(1); })();");
     }
 }
package com.google.javascript.jscomp;

 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.jstype.FunctionType;
 import com.google.javascript.rhino.jstype.JSType;
 import java.util.Collections;
 import java.util.List;
 import org.junit.Test;

 /**
  * Tests for {@link DevirtualizePrototypeMethods} targeting the bug where
  * the {@code $self} parameter's type was null instead of the original
  * {@code this} type.
  */
 public class DevirtualizePrototypeMethodsBugTest {

   @Test
   public void testRewritePrototypeMethod_selfTypeMatchesInstanceType() {
     Compiler compiler = compile(
         "/** @constructor */",
         "function Foo() {}",
         "/** @return {number} */",
         "Foo.prototype.bar = function() {",
         "  return 5;",
         "};",
         "(new Foo()).bar();"
     );
     assertCompileSucceeded(compiler);

     Node func = findFunction(compiler.getRoot(), "JSCompiler_StaticMethods_bar");
     assertNotNull("Function JSCompiler_StaticMethods_bar must be defined", func);

     FunctionType ft = (FunctionType) func.getJSType();
     assertNotNull(ft);
     // The first parameter of the rewritten function corresponds to $self.
     JSType selfType = null;
     for (Node param : ft.getParameters()) {
       selfType = param.getJSType();
       break; // only first parameter
     }
     assertNotNull("$self type must not be null", selfType);
     assertTrue("$self type must be Foo instance, got: " + selfType,
         selfType.toString().contains("Foo"));
   }

   @Test
   public void testRewritePrototypeMethod_simpleCall_selfTypeNotNull() {
     Compiler compiler = compile(
         "/** @constructor */",
         "function A() { this.x = 1; }",
         "/** @return {number} */",
         "A.prototype.m = function() { return 3; };",
         "new A().m();"
     );
     assertCompileSucceeded(compiler);

     Node func = findFunction(compiler.getRoot(), "JSCompiler_StaticMethods_m");
     assertNotNull(func);
     FunctionType ft = (FunctionType) func.getJSType();
     assertNotNull(ft);
     // First parameter is $self, should be instance of A.
     List<Node> params = func.getFirstChild().getNext().children();
     assertTrue(params.size() > 0);
     Node selfParam = params.get(0);
     assertNotNull(selfParam.getJSType());
     assertNotNull(selfParam.getJSType().toString());
     assertTrue(selfParam.getJSType().toString().contains("A"));
   }

   @Test
   public void testRewritePrototypeMethod_inheritance_selfTypeMatchesBase() {
     Compiler compiler = compile(
         "/** @constructor */",
         "function B() {}",
         "/** @return {number} */",
         "B.prototype.foo = function() { return 7; };",
         "/** @constructor @extends {B} */",
         "function C() {}",
         "goog.inherits(C, B);",
         "var c = new C();",
         "c.foo();"
     );
     assertCompileSucceeded(compiler);

     Node func = findFunction(compiler.getRoot(), "JSCompiler_StaticMethods_foo");
     assertNotNull(func);
     // $self type should be B instance (the original this).
     Node paramList = func.getFirstChild().getNext();
     Node selfParam = paramList.getFirstChild();
     assertNotNull("$self param must exist", selfParam);
     JSType selfType = selfParam.getJSType();
     assertNotNull("$self type must not be null", selfType);
     assertTrue("$self type should be B instance, got: " + selfType,
         selfType.toString().contains("B"));
   }

   @Test
   public void testRewritePrototypeMethod_noTypeCheckWarnings_onExtends() {
     // This scenario caused warnings in testGoodExtends9 due to null $self type.
     Compiler compiler = compile(
         "/** @constructor */",
         "function Base() {}",
         "/** @return {number} */",
         "Base.prototype.fn = function() { return 1; };",
         "/** @constructor @extends {Base} */",
         "function Derived() {}",
         "goog.inherits(Derived, Base);",
         "/** @return {number} @override */",
         "Derived.prototype.fn = function() { return 2; };",
         "var d = new Derived();",
         "d.fn();"
     );
     assertCompileSucceeded(compiler);
     // No warnings should be present.
     assertTrue("Unexpected warnings after devirtualization: " + compiler.getWarnings(),
         compiler.getWarnings().length == 0);
   }

   @Test
   public void testRewritePrototypeMethod_noTypeCheckErrors_onExtends() {
     Compiler compiler = compile(
         "/** @constructor */",
         "function X() {}",
         "/** @return {boolean} */",
         "X.prototype.p = function() { return true; };",
         "/** @constructor @extends {X} */",
         "function Y() {}",
         "goog.inherits(Y, X);",
         "/** @return {boolean} @override */",
         "Y.prototype.p = function() { return false; };",
         "(new Y()).p();"
     );
     assertCompileSucceeded(compiler);
     assertTrue("Unexpected type errors: " + compiler.getErrors(), compiler.getErrors().length ==
0);
   }

   @Test
   public void testRewritePrototypeMethod_selfTypeForMultipleMethods() {
     Compiler compiler = compile(
         "/** @constructor */",
         "function Vec() { this.x = 0; }",
         "/** @return {number} */",
         "Vec.prototype.length = function() {",
         "  return Math.abs(this.x);",
         "};",
         "/** @param {Vec} other */",
         "Vec.prototype.add = function(other) {",
         "  this.x += other.x;",
         "};",
         "var v = new Vec();",
         "v.length();"
     );
     assertCompileSucceeded(compiler);

     Node lenFunc = findFunction(compiler.getRoot(), "JSCompiler_StaticMethods_length");
     assertNotNull(lenFunc);
     Node addFunc = findFunction(compiler.getRoot(), "JSCompiler_StaticMethods_add");
     assertNotNull(addFunc);

     // $self for length should be Vec instance.
     Node selfLen = lenFunc.getFirstChild().getNext().getFirstChild();
     assertNotNull(selfLen);
     assertNotNull(selfLen.getJSType());
     assertTrue(selfLen.getJSType().toString().contains("Vec"));

     // $self for add should be Vec instance (original this).
     Node selfAdd = addFunc.getFirstChild().getNext().getFirstChild();
     assertNotNull(selfAdd);
     assertNotNull(selfAdd.getJSType());
     assertTrue(selfAdd.getJSType().toString().contains("Vec"));
   }

   // ---- helpers ----

   private static Compiler compile(String... lines) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     options.setLanguageIn(LanguageMode.ECMASCRIPT5);
     options.setCheckTypes(true);
     options.setDevirtualizePrototypeMethods(true);
     // Avoid dead code removal.
     options.setRemoveUnusedVars(false);
     options.setRemoveUnusedPrototypeProperties(false);

     String source = String.join("\n", lines);
     compiler.compile(
         Collections.singletonList(SourceFile.fromCode("externs", "")),
         Collections.singletonList(SourceFile.fromCode("input", source)),
         options);
     return compiler;
   }

   private static void assertCompileSucceeded(Compiler compiler) {
     String message = "Compilation errors: " + compiler.getErrors()
         + " warnings: " + compiler.getWarnings();
     assertFalse(message, compiler.hasErrors());
     assertTrue(message, compiler.getErrors().length == 0);
   }

   private static Node findFunction(Node root, String name) {
     if (root.isFunction() && name.equals(root.getFirstChild().getString())) {
       return root;
     }
     for (Node child = root.getFirstChild(); child != null; child = child.getNext()) {
       Node found = findFunction(child, name);
       if (found != null) {
         return found;
       }
     }
     return null;
   }
 }

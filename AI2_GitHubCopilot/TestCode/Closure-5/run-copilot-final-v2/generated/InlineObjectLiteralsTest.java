package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 public class InlineObjectLiteralsTest extends CompilerTestCase {

   @Override
   protected CompilerPass getProcessor(Compiler compiler) {
     return new InlineObjectLiterals(compiler, compiler.getUniqueNameIdSupplier());
   }

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     enableNormalize();
   }

   public void testNoInlineDeletedProperties() {
     // Trigger: delete a property on an object literal must prevent inlining.
     testSame("function f() { var a = {b:1}; delete a.b; }");
   }

   public void testSimpleInline() {
     test("function f() { var a = {b:1}; return a.b; }",
          "function f() { var JSCompiler_object_inline_b_%0=1; return JSCompiler_object_inline_b_%0;
}");
   }

   public void testInlineMultipleProperties() {
     test("function f() { var a = {b:1, c:2}; return a.b + a.c; }",
          "function f() { var JSCompiler_object_inline_b_%0=1; var JSCompiler_object_inline_c_%0=2;"
+
          "return JSCompiler_object_inline_b_%0 + JSCompiler_object_inline_c_%0; }");
   }

   public void testNoInlineMethodCall() {
     // A call using a property of the object uses `this`, should not inline.
     testSame("function f() { var a = {b:function(){return 1;}}; a.b(); }");
   }

   public void testNoInlineSelfReferential() {
     // Self-referential assignment like {b: a.c} must not be inlined.
     testSame("function f() { var a = {b: a.c}; return a.b; }");
   }

   public void testNoInlineUndefinedPropertyAccess() {
     // Accessing a property that is not defined on the object literal must not inline.
     testSame("function f() { var a = {b:1}; return a.c; }");
   }

   public void testNoInlineGlobalVar() {
     // Global variables are never inlined.
     testSame("var a = {b:1}; use(a.b);");
   }

   public void testNoInlineDeletePropertyAfterAssignment() {
     // Deletion after an assignment to the property must still block inlining.
     testSame("function f() { var a = {b:1}; a.b = 2; delete a.b; }");
   }

   public void testNoInlineDeleteNestedProperty() {
     // Deleting a nested property through the object should block inlining.
     testSame("function f() { var a = {b:{c:1}}; delete a.b.c; }");
   }

   public void testNoInlineDeleteThenRead() {
     // Deleting a property followed by reading it must not inline.
     testSame("function f() { var a = {b:1}; delete a.b; return a.b; }");
   }
 }

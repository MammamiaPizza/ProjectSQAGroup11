package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.Result;
 import com.google.javascript.jscomp.SourceFile;
 import junit.framework.TestCase;

 public class PeepholeSubstituteAlternateSyntaxTest extends TestCase {

     private Compiler compiler;
     private CompilerOptions options;

     @Override
     protected void setUp() throws Exception {
         super.setUp();
         compiler = new Compiler();
         options = new CompilerOptions();
     }

     private String compileAndGetOutput(String js) {
         SourceFile extern = SourceFile.fromCode("externs", "");
         SourceFile input = SourceFile.fromCode("input", js);
         compiler.compile(extern, input, options);
         if (compiler.hasErrors()) {
             return null;
         }
         return compiler.toSource();
     }

     public void testUndefinedNoShadow() {
         String js = "function f() { var x; return x === undefined; }";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertTrue(output.contains("void 0"));
     }

     public void testUndefinedLocalVarShadow() {
         String js = "function f() { var undefined = 1; var x; return x === undefined; }";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertFalse(output.contains("void 0"));
     }

     public void testParameterShadow() {
         String js = "function f(undefined) { var x; return x === undefined; }";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertFalse(output.contains("void 0"));
     }

     public void testNestedLocalShadow() {
         String js = "function outer() { var undefined = 1; function inner() { var x; return x ===
undefined; } }";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertFalse(output.contains("void 0"));
     }

     public void testPropertyAccessNoReplacement() {
         String js = "function f(obj) { return obj.undefined === x; }";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertFalse(output.contains("void 0"));
     }

     public void testUndefinedInAssignmentGlobal() {
         String js = "function f() { var x = undefined; return x; }";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertTrue(output.contains("void 0"));
     }

     public void testUndefinedInAssignmentLocalShadow() {
         String js = "function f() { var undefined = 1; var x = undefined; return x; }";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertFalse(output.contains("void 0"));
     }

     public void testUndefinedInReturnGlobal() {
         String js = "function f() { return undefined; }";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertTrue(output.contains("void 0"));
     }

     public void testUndefinedInReturnLocalShadow() {
         String js = "function f() { var undefined = 1; return undefined; }";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertFalse(output.contains("void 0"));
     }

     public void testUndefinedInArrayLiteralGlobal() {
         String js = "var arr = [undefined, 1];";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertTrue(output.contains("void 0"));
     }

     public void testUndefinedInArrayLiteralLocalShadow() {
         String js = "function f() { var undefined = 1; var arr = [undefined, 2]; return arr; }";
         String output = compileAndGetOutput(js);
         assertNotNull(output);
         assertFalse(output.contains("void 0"));
     }
 }
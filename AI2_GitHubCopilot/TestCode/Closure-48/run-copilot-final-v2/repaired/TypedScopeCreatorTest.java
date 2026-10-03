package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.JSError;
 import com.google.javascript.jscomp.SourceFile;

 import java.util.ArrayList;
 import java.util.List;

 import junit.framework.TestCase;

 public class TypedScopeCreatorTest extends TestCase {

   private static final String ENUM_INIT_WARNING =
       "enum initializer must be an object literal or an enum";

   private CompilerOptions newOptions() {
     CompilerOptions options = new CompilerOptions();
     options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);
     return options;
   }

   private void compileAndCheck(String js, boolean expectWarning) {
     Compiler compiler = new Compiler();
     List<SourceFile> externs = new ArrayList<SourceFile>();
     externs.add(SourceFile.fromCode("externs.js",
         "var console;"));
     List<SourceFile> inputs = new ArrayList<SourceFile>();
     inputs.add(SourceFile.fromCode("test.js", js));

     compiler.compile(externs, inputs, newOptions());

     JSError[] warnings = compiler.getWarnings();
     String sourceName = compiler.getWarnings().length > 0
         ? compiler.getWarnings()[0].sourceName : null;
     boolean found = false;
     for (JSError warning : warnings) {
       // Only check warnings from our test source
       if (warning.sourceName != null && warning.sourceName.equals("test.js") &&
           warning.description.contains(ENUM_INIT_WARNING)) {
         found = true;
         break;
       }
     }
     if (expectWarning) {
       assertTrue("Expected a warning: " + ENUM_INIT_WARNING, found);
     } else {
       assertFalse("Unexpected warning: " + ENUM_INIT_WARNING, found);
     }
   }

   private void testSame(String js) {
     compileAndCheck(js, false);
   }

   private void testWarning(String js) {
     compileAndCheck(js, true);
   }

   public void testValidEnumObjectLiteral() {
     testSame("/** @enum {number} */ var MyEnum = {A: 1, B: 2};");
   }

   public void testValidEnumAlias() {
     testSame(
         "/** @enum {number} */ var BaseEnum = {X: 0};" +
         "/** @enum {number} */ var MyEnum = BaseEnum;");
   }

   public void testEnumInitializerIsFunctionCall() {
     testWarning("/** @enum {number} */ var MyEnum = someFunc();");
   }

   public void testEnumInitializerIsVariableReference() {
     testWarning(
         "var x = {};" +
         "/** @enum {number} */ var MyEnum = x;");
   }

   public void testEnumInitializerIsNumberLiteral() {
     testWarning("/** @enum {number} */ var MyEnum = 42;");
   }

   public void testEnumInitializerIsStringLiteral() {
     testWarning("/** @enum {number} */ var MyEnum = 'hello';");
   }

   public void testEnumInitializerIsBooleanLiteral() {
     testWarning("/** @enum {number} */ var MyEnum = true;");
   }

   public void testEnumInitializerIsArrayLiteral() {
     testWarning("/** @enum {number} */ var MyEnum = [1, 2, 3];");
   }

   public void testEnumInitializerIsNull() {
     testWarning("/** @enum {number} */ var MyEnum = null;");
   }

   public void testEnumInitializerIsNewExpression() {
     testWarning("/** @enum {number} */ var MyEnum = new Object();");
   }

   public void testEnumWithoutInitializer() {
     testSame("/** @enum {number} */ var MyEnum;");
   }

   public void testEnumInsideFunction() {
     testSame("function f() { /** @enum {number} */ var MyEnum = {A: 1}; }");
   }

   public void testEnumReassignmentWithObjectLiteral() {
     testSame(
         "/** @enum {number} */ var MyEnum = {A: 1};" +
         "MyEnum = {B: 2};");
   }

   public void testExternEnumDefinition() {
     Compiler compiler = new Compiler();
     List<SourceFile> externs = new ArrayList<SourceFile>();
     externs.add(SourceFile.fromCode("externs.js",
         "/** @enum {number} */ var ExtEnum = {A: 1};" +
         "var console;"));
     List<SourceFile> inputs = new ArrayList<SourceFile>();
     inputs.add(SourceFile.fromCode("test.js", ""));

     compiler.compile(externs, inputs, newOptions());

     JSError[] warnings = compiler.getWarnings();
     boolean found = false;
     for (JSError warning : warnings) {
       if (warning.sourceName != null && warning.sourceName.equals("externs.js") &&
           warning.description.contains(ENUM_INIT_WARNING)) {
         found = true;
         break;
       }
     }
     assertFalse("Enum in externs should not produce a warning", found);
   }

   public void testNestedEnumObjectLiteral() {
     testSame(
         "/** @enum {number} */ var Outer = {" +
         "  /** @enum {number} */ INNER: {X: 1}" +
         "};");
   }
 }

package com.google.javascript.jscomp;

 import static junit.framework.Assert.*;

 import com.google.common.collect.ImmutableList;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.DiagnosticGroups;
 import com.google.javascript.jscomp.JSError;
 import com.google.javascript.jscomp.Scope;
 import com.google.javascript.jscomp.SourceFile;
 import com.google.javascript.jscomp.TypeCheck;
 import com.google.javascript.jscomp.CheckLevel;
 import com.google.javascript.rhino.jstype.JSType;

 import java.util.List;

 import junit.framework.TestCase;

 /**
  * Regression tests for bug 1023.
  * Tests that a warning is issued for type mismatch, the type of a
  * function used before its definition is correctly inferred, and
  * interface property definitions do not cause a NullPointerException.
  */
 public class Bug1023Test extends TestCase {

   public void testIssue1023_warningExpected() {
     // Code where a function expects a number but receives a string.
     String js =
         "/** @param {number} x */ function f(x) {};\n" +
         "f('string');";
     Compiler compiler = compile(js);
     List<JSError> warnings = compiler.getResult().warnings;
     assertFalse("Expected at least one warning", warnings.isEmpty());
     boolean found = false;
     for (JSError w : warnings) {
       if (TypeCheck.TYPE_MISMATCH.key.equals(w.getType().key)) {
         found = true;
         break;
       }
     }
     assertTrue("Expected a TYPE_MISMATCH warning", found);
   }

   public void testMethodBeforeFunction2_typeString() {
     // Function used before its definition – the type should be resolved,
     // not left as ?.
     String js =
         "var a = someFunc;\n" +
         "function someFunc(x) {}";
     Compiler compiler = compile(js);
     Scope topScope = compiler.getTopScope();
     assertNotNull("Top scope should not be null", topScope);
     Scope.Var var = topScope.getVar("a");
     assertNotNull("Variable 'a' should be defined", var);
     JSType type = var.getType();
     assertNotNull("Type of 'a' should not be null", type);
     // The expected type signature for a function with unknown parameter
     // and undefined return, and this typed as the global Window object.
     assertEquals("function (this:Window, ?): undefined",
                  type.toAnnotationString());
   }

   public void testPropertiesOnInterface2_noNPE() {
     // An interface with a property should not throw a NullPointerException.
     String js =
         "/** @interface */\n" +
         "function Foo() {}\n" +
         "/** @type {number} */\n" +
         "Foo.prototype.bar;";
     try {
       compile(js);
     } catch (NullPointerException e) {
       fail("NullPointerException while handling interface properties: "
           + e.getMessage());
     }
   }

   private Compiler compile(String source) {
     CompilerOptions options = new CompilerOptions();
     options.setCheckTypes(true);
     options.setWarningLevel(DiagnosticGroups.TYPE_MISMATCH, CheckLevel.WARNING);
     options.setContinueAfterErrors(true);
     Compiler compiler = new Compiler();
     List<SourceFile> externs = CommandLineRunner.getDefaultExterns();
     List<SourceFile> inputs = ImmutableList.of(
         SourceFile.fromCode("test.js", source));
     compiler.compile(externs, inputs, options);
     return compiler;
   }
 }

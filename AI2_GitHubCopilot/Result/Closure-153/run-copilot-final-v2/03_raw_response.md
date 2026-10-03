import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerPass;
 import com.google.javascript.jscomp.Node;
 import com.google.javascript.jscomp.SourceFile;
 import com.google.javascript.jscomp.Normalize;
 import com.google.javascript.jscomp.DiagnosticType;
 import junit.framework.TestCase;

 public class NormalizeBug153Test extends TestCase {

   private Compiler compiler;

   @Override
   protected void setUp() throws Exception {
     compiler = new Compiler();
   }

   private CompilerPass getProcessor(Compiler compiler) {
     return new Normalize(compiler, false);
   }

   protected void test(String externs, String src, String expected) {
     compileAndRun(externs, src, /*expectError=*/false, null);
     // If source is empty, test applies to externs output; otherwise to source output.
     String actual;
     if (src.isEmpty()) {
       actual = compiler.toSource(compiler.getExternsRoot());
     } else {
       actual = compiler.toSource(compiler.getRoot()));
     }
     assertEqual(expected, actual);
   }

   protected void test(String src, String expected) {
     test("", src, expected);
   }

   protected void testSame(String src) {
     test(src, src);
   }

   protected void testError(String src, DiagnosticType error) {
     compileAndRun("", src, true, error);
     // no assertion on output; just that the error was reported
   }

   private void compileAndRun(String externsCode, String srcCode,
                               boolean expectError, DiagnosticType expectedError) {
     SourceFile externFile = SourceFile.fromCode("externs", externsCode);
     SourceFile srcFile = SourceFile.fromCode("src", srcCode);

     Node externRoot = compiler.parse(externFile);
     Node srcRoot = compiler.parse(srcFile);

     if (externRoot != null) {
       compiler.initExternsRoot(externRoot);
     }
     compiler.setRoot(srcRoot);

     CompilerPass pass = getProcessor(compiler);
     pass.process(externRoot, srcRoot);

     if (expectError) {
       assertTrue("Expected an error of type " + expectedError,
                  compiler.hasErrorType(expectedError));
     } else {
       assertEquals("Unexpected errors: " + compiler.getErrors(),
                    0, compiler.getErrorCount());
     }
   }

   // --- preserved test methods -------------------------------------------------

   /**
    * Ensure duplicate var declarations in externs are preserved and no error
    * is reported.  Bug 290 would cause a crash or false positive here.
    */
   public void testDuplicateVarInExterns() {
     test(
         "var x; var x;",  // externs
         "",                 // source
         "var x; var x;"      // expected externs, left untouched
     );
   }

   /**
    * Make local names unique: a local variable that shadows a global/extern
    * name must be renamed to avoid collision.  The fix renames local alert
    * to alert$1.
    */
   public void testMakeLocalNamesUnique() {
     test(
         "var alert;",                                  // externs
         "function f() { var alert=1; return alert; }", // source
         "function f() { var alert$1=1; return alert$1; }"  // expected
     );
   }

   /**
    * A duplicate var declaration in a source function body is rewritten:
    * the second declaration becomes a plain assignment.
    */
   public void testDuplicateVarInSource() {
     test(
         "var x=1; var x=2;",
         "var x=1;x=2;"
     );
   }

   /**
    * Source var does not hide externs var – allowed with no renaming.
    */
   public void testDuplicateSourceAndExternsNoShadow() {
     test(
         "var x;",       // externs
         "var x=1;",     // source
         "var x=1;"      // allowed, no renaming needed (global collision allowed)
     );
   }

   /**
    * If source var redeclares a name already declared by catch(e),
    * the compiler must report an error.
    */
   public void testCatchBlockRedeclarationError() {
     testError(
         "function f() { try{}catch(e){} var e=1; }",
         Normalize.CATCH_BLOCK_VAR_ERROR
     );
   }

   /**
    * Function parameter that shadows an externs name should be renamed.
    */
   public void testParamShadowingExterns() {    test(
         "var alert;",
         "function f(alert) { return alert; }",
         "function f(alert$1) { return alert$1; }"
     );
   }

   /**
    * Nested function local var shadowing its own outer var does not conflict
    * with externs and should not be renamed globally, but inner duplicate is
    * converted to assignment.
    */
   public void testNestedDuplicateVar() {
     test(
         "function f() { var a=1; function g() { var a=2; return a; } return a; }",
         "function f() { var a=1; function g() { var a$1=2; return a$1; } return a; }"
     );
   }

   /**
    * Empty source and externs should succeed without errors.
    */
   public void testEmptyInput() {
     test("", "", "");
   }

   /**
    * Several locals shadowing the same extern must all be renamed uniquely.
    */
   public void testMultipleShadowingLocals() {
     test(
         "var alert;",
         "function f() { var alert=1; } function g() { var alert=2; }",
         "function f() { var alert$1=1; } function g() { var alert$2=2; }"
     );
   }

   /**
    * Duplicate var in global source scope: second one becomes plain assignment.
    */
   public void testGlobalDuplicateVar() {
     test(
         "var a=1; var a=2;",
         "var a=1;a=2;"
     );
   }

   /**
    * A variable declared first in source and then shadowed in a catch parameter
    * is not a catch-block redeclaration error; only var after catch matters.
    */
   public void testCatchParamShadowNotAnError() {
     testSame(
         "function f() { var e=1; try{}catch(e){} }"
     );
   }
 }
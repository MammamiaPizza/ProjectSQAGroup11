package com.google.javascript.jscomp;

import junit.framework.TestCase;
import junit.framework.Assert;
import java.util.List;

 /** Self-contained JUnit3 test for {@link CheckSideEffects} focusing on the

 - missing warning described in Bug 753.
   */
  public class CheckSideEffectsBugTest extends TestCase {

   private Compiler compiler; private CheckSideEffects checker;

   @Override protected void setUp() throws Exception {
     super.setUp(); }

   @Override protected void tearDown() throws Exception {
     super.tearDown(); }

   private void testError(String js, DiagnosticType diagnostic) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     compiler.initOptions(options);
     SourceFile source = SourceFile.fromCode("test.js", js);
     compiler.compile(source);
     Node externRoot = compiler.getExternsRoot();
     Node jsRoot = compiler.getJsRoot();
     CheckSideEffects checker = new CheckSideEffects(compiler, CheckLevel.WARNING, false);
     checker.process(externRoot, jsRoot);
     JSError[] warnings = compiler.getWarnings();
     int count = 0;
     for (JSError w : warnings) {
       if (w.getType().equals(diagnostic)) {
         count++;
       }
     }
     Assert.assertEquals("Expect exactly one warning of type " + diagnostic, 1, count); }

   private void testSame(String js) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     compiler.initOptions(options);
     SourceFile source = SourceFile.fromCode("test.js", js);
     compiler.compile(source);
     Node externRoot = compiler.getExternsRoot();
     Node jsRoot = compiler.getJsRoot();
     CheckSideEffects checker = new CheckSideEffects(compiler, CheckLevel.WARNING, false);
     checker.process(externRoot, jsRoot);
     JSError[] warnings = compiler.getWarnings();
     int count = 0;
     for (JSError w : warnings) {
       if (w.getType().equals(CheckSideEffects.USELESS_CODE_ERROR)) {
         count++;
       }
     }
     Assert.assertEquals("Should be no warning", 0, count); }

   // ---------- normal useless expressions ---------

   public void testNumberLiteral() {
     testError("42;", CheckSideEffects.USELESS_CODE_ERROR); }

   public void testStringLiteral() {
     testError("'hello';", CheckSideEffects.USELESS_CODE_ERROR); }

   public void testBooleanLiteral() {
     testError("true;", CheckSideEffects.USELESS_CODE_ERROR); }

   public void testNullLiteral() {
     testError("null;", CheckSideEffects.USELESS_CODE_ERROR); }

   public void testObjectLiteral() {
     testError("({});", CheckSideEffects.USELESS_CODE_ERROR); }

   public void testArrayLiteral() {
     testError("([]);", CheckSideEffects.USELESS_CODE_ERROR); }

   // --------- Bug 753: missing '+' in string concat ---------

   /**
    * When a '+' is omitted between two string literals, ASI turns the second
    * literal into a standalone expression statement, which must elicit a
    * "missing +" warning.
    */ public void testMissingPlus() {
     testError("var x = 'a'\n'b'", CheckSideEffects.USELESS_CODE_ERROR); }

   // --------- boundary / branch coverage ---------

   public void testUselessCodeInComma() {
     testError("1, 'a';", CheckSideEffects.USELESS_CODE_ERROR); }

   public void testNoWarningForUsedExpression() {
     testSame("var x = foo();"); }

   public void testNoWarningForVoidSemicolon() {
     testSame(";"); }

   public void testNoWarningForAssignment() {
     testSame("x = 1;"); }

   public void testNoWarningForExprResult() {
     testError("'foo'", CheckSideEffects.USELESS_CODE_ERROR); }
 }

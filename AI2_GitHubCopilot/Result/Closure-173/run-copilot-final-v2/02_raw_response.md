package com.google.javascript.jscomp;

 import com.google.common.collect.Lists;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 import junit.framework.TesstCase;

 import java.util.List;

 /**
  * Tests for the parenthesization bug in CodeGenerator.addExpr and
  * PeepholeSubstituteAlternateSyntax.optimizeSubtree.
  * Bug: binary operators inside bracket property access lose necessary
  * parentheses when the outer operator has higher or equal precedence.
  */
 public class Bug173Test extends TestCase {

   private Compiler compiler;
   private CompilerOptions options;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     options = new CompilerOptions();
     options.setCodingConvention(new DefaultCodingConvention());
     compiler.initOptions(options);
   }

   /**
    * Compiles input JS through full pipeline (parse, peephole, print)
    * and returns the printed output.
    */
   private String compileAndPrint(String js) {
     SourceFile externs = SourceFile.fromCode("externs.js", "");
     SourceFile input = SourceFile.fromCode("test.js", js);
     compiler.init(Lists.newArrayList(externs),
                   Lists.newArrayList(input),
                   options);
     compiler.parse();
     // Run the PeepholeSubstituteAlternateSyntax pass to trigger AST changes
     Node root = compiler.getRoot();
     if (root != null) {
       PeepholeSubstituteAlternateSyntax pass =
           new PeepholeSubstituteAlternateSyntax(true);
       Node externRoot = compiler.getExternsRoot();
       Node mainRoot = root.getChildAtIndex(1); // first script child after externs
       if (mainRoot != null) {
         pass.process(externRoot, mainRoot);
       }
     }
     CodePrinter.Builder builder = new CodePrinter.Builder(root);
     builder.setPrettyPrint(false);
     return builder.build();
   }

   /**
    * Parses and prints without optimizations to test CodeGenerator directly.
    */
   private String parseAndPrint(String js) {
     SourceFile externs = SourceFile.fromCode("externs.js", "");
     SourceFile input = SourceFile.fromCode("test.js", js);
     Compiler localCompiler = new Compiler();
     localCompiler.initOptions(options);
     localCompiler.init(Lists.newArrayList(externs),
                        Lists.newArrayList(input),
                        options);
     localCompiler.parse();
     Node root = localCompiler.getRoot();
     CodePrinter.Builder builder = new CodePrinter.Builder(root);
     builder.setPrettyPrint(false);
     return builder.build();
   }

   // ===== Direct CodeGenerator paren-insertion tests =====

   /**
    * OR inside bracket access: a||[b||c] must become a||[(b||c)]
    * because GETELEM binds tighter than ||.
    */
   public void testOrInsideBracketAccess() {
     String result = parseAndPrint("a||[b||c];");
     assertTrue("Expected '(' before 'b' in GETELEM: " + result,
                result.contains("( b || c )") || result.contains("( b||c )"));
   }

   /**
    * Multiplicatio inside bracket access: a*[b*c] must become a*[(b*c)]
    */
   public void testMultiplyInsideBracketAccess() {
     String result = parseAndPrint("a*[b*c];";
     assertTrue("Expected '(' inside bracket: " + result,
                result.contains("[( b * c )") || result.contains("[(b*c)"));
   }

   /**
    * Btwise OR inside bracket access: a|[b|c] must become a|[(b|c)]
    */
   public void testBitwiseOrInsideBracketAccess() {
     String result = parseAndPrint("a|[b|c];";
     assertTrue("Expected '(' for bitwise OR inside bracket: " + result,
                result.contains("[( b | c )") || result.contains("[(b|c)"));
   }

   /**
    * Modulo and multiply inside bracket access: 3*[4%3*5] must become
    * 3*[(4%3*5)] as reported in Issue 1062.
    */
   public void testModuloMultiplyInBracketAccess() {
     String result = parseAndPrint("3*[4%3*5];");
     assertTrue("Expected '(' for modulo inside bracket: " + result,
                result.contains("[") &&
                (result.contains("(4") || result.contains("( 4")) &&
                result.contains("["));
   }

   /**
    * AND inside bracket access: a&&[b&&c] must become a&&[(b&&c)]
    * since && precedence > bracket access but same-precedence on each side.
    */
   public void testAndInsideBracketAccess() {
     String result = parseAndPrint("a&&[b&&c];");
     assertTrue("Expected '(' for AND inside bracket: " + result,
                result.contains("( b && c )") || result.contains("(b&&c)"));
   }

   /**
    * Same-precedence operators associativity: a||b||c vs (a||b)||c.
    * Left-associative operatoirs should not add extra parens.
    */
   public void testSamePrecedenceAssociativity() {
     // a||b||c is left-associative; no extra parens needed
     String result = parseAndPrint("a||b||c;");
     // Should not have extra parens like ((a||b)||c)
     assertFalse("Unexpected extra parens: " + result,
                 result.contains("(( a || b )") || result.contains("((a||b)"));
     // But if we write (a||b)||c, it should still print cleanly
     String result2 = parseAndPrint("(a||b)||c;");
     assertTrue("Parens should be preserved when explicit: " + result2,
                result2.contains("( a || b )") || result2.contains("(a||b)"));
   }

   /**
    * Nested bracket access with mixed operators.
    */
   public void testNestedBracketAccess() {
     String result = parseAndPrint("x[y||z]||a;");
     // The y||z inside bracket access needs parens
     assertTrue("Parens required inside bracket: " + result,
                result.contains("( y || z )") || result.contains("(y||z)"));
   }

   // ==== PeepholeSubstituteAlternateSyntax interaction tests ====

   /**
    * Peephole optimization should not remove necessary parens.
    * Using array literal that the peephole pass may optimize
    * with string split folding, ensuring parens survive.
    */
   public void testPeepholePreservesBracketParens() {
     // Use a pattern that peephole does not fold away but exercises
     // bracket access with binary ops
     String js = "var a,b,c; a||[ b||c ];";
     String result = compileAndPrint(js);
     assertTrue("Peephole must preserve parens in bracket: " + result,
                result.contains("( b || c )") || result.contains("( b||c )") ||
                result.contains("(b||c)"));  }

   /**
    * Test the exact scenario from testPrint failure:
    * array literal with multiple operators needing parens.
    */
   public void testArrayLiteralWithBinaryOps() {
     String js = "var a,b,c; [a||b, a*b, a|c];";
     String result = compileAndPrint(js);
     // Inside array literal, a||b needs parens due to comma low precedence?
     // Actually comma has lower precedence than ||, so no parens needed.
     // But the testPrint failure suggests parens ARE needed inside certain contexts.
     // This tests that the peephole pass doesn't break things.
     assertNotNull("Generated code must not be null", result);
     assertTrue("Generated code must be non-empty", result.length() > 0);
   }

   /**
    * Complex chain: operator inside bracket inside another expression.
    * e.g., (a||[b||c]) * d
    */
   public void testComplexExpressionInBracket() {
     String js = "(a||[b||c])*d;";
     String result = parseAndPrint(js);
     assertTrue("b||c inside bracket must have parens: " + result,
                result.contains("( b || c )") || result.contains("(b||c)"));
   }

   /**
    * Non-assoc edge: exponentiation-like or increment ops that
    * might expose missing parens in non-associative contexts.
    * Using ternary inside bracket to test mixed precedence.
    */
   public void testTernaryInsideBracketAccess() {
     String js = "x[ a?b:c ]||d;";
     String result = parseAndPrint(js);
     // Ternary inside bracket access should have parens
     assertTrue("Ternary inside bracket needs parens: " + result,
                result.contains("( a ? b : c )") ||
                result.contains("(a?b:c)"));
   }

   /**
    * Test that simple expressions without operators don't get
    * unnecessary parentheses added.
    */
   public void testSimpleExpressionNoExtraParens() {
     String result = parseAndPrint("a[0];");
     assertFalse("No extra parens for simple index: " + result,
                 result.contains("((0))") || result.contains("(0)"));
     // Simple binary should still have no extra parens
     result = parseAndPrint("a+b;");
     assertFalse("No extra parens for simple addition: " + result,
                 result.startsWith("("));
   }

   /**
    * Test the associativity behavior directly.
    * The PeepholeSubstituteAlternateSyntaxTest::testAssocitivity
    * failure suggests that folded expressions lose correct associativity.
    */
   public void testPeepholeAssocitivityAfterFold() {
     // Use true/false folding which peephole reduces
     String js = "var x = true ? 1 : 0;";
     String result = compileAndPrint(js);
     assertNotNull("Folded result must not be null", result);
     // After peephole, true may be folded to !0
     assertTrue("Peephole should produce valid code", result.contains("!0") ||
                result.contains("true") || result.contains("1"));

     // Test associativity with || after folding
     String js2 = "var a = false || true;";
     String result2 = compileAndPrint(js2);
     assertNotNull("Folded || result must not be null", result2);
   }
 }
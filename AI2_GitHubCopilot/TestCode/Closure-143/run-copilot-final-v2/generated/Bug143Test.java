package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;

 /**
  * Tests for bug 143: incorrect parsing of --define flag with quotes and
  * incorrect removal of expressions with defines or side effects.
  */
 public class Bug143Test extends TestCase {

   // ---------- define flag parsing ----------

   public void testDefineFlagEmbeddedSingleQuote() throws Exception {
     // FOO="x'" should parse without error and retain the inner single quote.
     Map.Entry<String, Object> e = CommandLineRunner.parseDefineFlag("FOO=\"x'\"");
     assertNotNull(e);
     assertEquals("FOO", e.getKey());
     assertEquals("x'", e.getValue());
   }

   public void testDefineFlagEmbeddedDoubleQuote() throws Exception {
     // FOO="x\"" should give value x"
     Map.Entry<String, Object> e = CommandLineRunner.parseDefineFlag("FOO=\"x\\\"\"");
     assertEquals("FOO", e.getKey());
     assertEquals("x\"", e.getValue());
   }

   public void testDefineFlagEscapedBackslash() throws Exception {
     Map.Entry<String, Object> e = CommandLineRunner.parseDefineFlag("FOO=\"x\\\\\"");
     assertEquals("x\\", e.getValue());
   }

   public void testDefineFlagEmptyValue() throws Exception {
     // FOO= should produce empty string value
     Map.Entry<String, Object> e = CommandLineRunner.parseDefineFlag("FOO=");
     assertEquals("FOO", e.getKey());
     assertEquals("", e.getValue());
   }

   public void testDefineFlagSingleQuotes() throws Exception {
     // Single-quoted values are allowed.
     Map.Entry<String, Object> e = CommandLineRunner.parseDefineFlag("FOO='value'");
     assertEquals("value", e.getValue());
   }

   public void testDefineFlagNoQuotes() throws Exception {
     // Unquoted numeric string
     Map.Entry<String, Object> e = CommandLineRunner.parseDefineFlag("BAR=1.5");
     assertEquals("BAR", e.getKey());
     assertTrue(e.getValue() instanceof Number);
     assertEquals(1.5, ((Number) e.getValue()).doubleValue(), 0.0);
   }

   public void testDefineFlagSimpleDoubleQuotes() throws Exception {
     Map.Entry<String, Object> e = CommandLineRunner.parseDefineFlag("BAZ=\"hello\"");
     assertEquals("hello", e.getValue());
   }

   public void testMultipleDefinesMixedQuotes() throws Exception {
     // Parsing several flags in succession should each succeed.
     List<String> flags = new ArrayList<String>();
     flags.add("A=\"one'\"");
     flags.add("B='two\"'");
     flags.add("C=three");
     flags.add("D=");
     for (String f : flags) {
       Map.Entry<String, Object> e = CommandLineRunner.parseDefineFlag(f);
       assertNotNull(e);
       assertNotNull(e.getKey());
     }
   }

   // ---------- RemoveConstantExpressions ----------

   /**
    * A concrete test case for exercising the RemoveConstantExpressions pass.
    */
   public static class RemoveConstantExpressionsTest extends CompilerTestCase {

     @Override
     public CompilerPass getProcessor(Compiler compiler) {
       return new RemoveConstantExpressions(compiler);
     }

     public void testCallExpressionNotRemoved() {
       // A function call has side effects; should not be removed.
       testSame("function foo() { return 1; } foo();");
     }

     public void testNewExpressionNotRemoved() {
       // A constructor call has side effects; should not be removed.
       testSame("function Bar() {} new Bar();");
     }

     public void testCallWithDefineNotRemoved() {
       // Even when the argument is a compile-time constant (define),
       // the call itself must not be removed.
       // Set a define that will be substituted.
       CompilerOptions options = getOptions();
       options.setDefineToBooleanLiteral("FOO", false);  // use boolean define
       // The call bar(FOO) must remain.
       testSame("function bar(x) {} bar(FOO);");
     }

     public void testNewWithDefineNotRemoved() {
       CompilerOptions options = getOptions();
       options.setDefineToBooleanLiteral("TYPE", true);
       testSame("function Ctor(flag) {} new Ctor(TYPE);");
     }
   }
 }

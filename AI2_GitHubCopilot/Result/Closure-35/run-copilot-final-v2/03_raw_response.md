package com.google.javascript.jscomp;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;

 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.junit.runners.JUnit4;

 /**
  * Regression tests for issue 669: spurious "Property X never defined" warnings
  * in type inference after object literal property access.
  */
 @RunWith(JUnit4.class)
 public class TypeInferenceTest {

     private CompilerOptions createOptions() {
         CompilerOptions options = new CompilerOptions();
         options.setCheckTypes(true);
         // Enables the relevant diagnostic group if it exists in this version.
         try {
             options.setWarningLevel(
                     DiagnosticGroups.MISSING_PROPERTIES, CheckLevel.WARNING);
         } catch (Exception e) {
             // Fallback: group may have a different name in this version;
             // default options already cover the needed checks.
         }
         return options;
     }

     private void testSame(String js) {
         Compiler compiler = new Compiler();
         CompilerOptions options = createOptions();
         List<SourceFile> inputs = new ArrayList<SourceFile>();
         inputs.add(SourceFile.fromCode("testcode", js));
         compiler.compile(Collections.<SourceFile>emptyList(), inputs, options);
         assertEquals("Expected no warnings", 0, compiler.getWarningCount());
         assertEquals("Expected no errors", 0, compiler.getErrors().length);
     }

     private void testWarnings(String js, String expectedWarning) {
         Compiler compiler = new Compiler();
         CompilerOptions options = createOptions();
         List<SourceFile> inputs = new ArrayList<SourceFile>();
         inputs.add(SourceFile.fromCode("testcode", js));
         compiler.compile(Collections.<SourceFile>emptyList(), inputs, options);
         JSError[] warnings = compiler.getWarnings();
         assertNotNull("Expected warnings array", warnings);
         assertTrue("Expected at least one warning", warnings.length > 0);
         boolean found = false;
         for (JSError warning : warnings) {
             if (warning.description.contains(expectedWarning)) {
                 found = true;
                 break;
             }
         }
         assertTrue("Expected warning containing: " + expectedWarning, found);
     }

     @Test
     public void testNoSpuriousWarning_directPropertyAccess() {
         // Passing x.a to a function should not trigger a "never defined" warning.
         testSame("var x = {a: 1}; function f(y) {} f(x.a);");
     }

     @Test
     public void testNoSpuriousWarning_nestedPropertyAccess() {
         // Nested property access after object literal assignment.
         testSame("var x = {a: {b: 1}}; var y = x.a.b;");
     }

     @Test
     public void testNoSpuriousWarning_functionValuedPropertyAsCallback() {
         // Object literal with a function-valued property used as a callback.
         testSame("var x = {f: function(a) { return a; }}; x.f(1);");
     }

     @Test
     public void testNoSpuriousWarning_propertyAfterAssignment() {
         // Property defined via assignment before use.
         testSame("var x = {}; x.a = 1; function f(y) {} f(x.a);");
     }

     @Test
     public void testNoSpuriousWarning_unionNarrowedToObject() {
         // Property access after narrowing a union type to an object type.
         testSame(
                 "/** @type {Object|number} */ var x = {}; " +
                 "if (typeof x === 'object' && x !== null) { x.a = 1; } " +
                 "function f(y) {} f(x.a);");
     }

     @Test
     public void testNoSpuriousWarning_typedObjectLiteral() {
         // Typed object literal with JSDoc annotation.
         testSame("var /** {a: number} */ x = {a: 1}; function f(y) {} f(x.a);");
     }

     @Test
     public void testNoSpuriousWarning_literalAsArgument() {
         // Inline object literal passed directly to a function.
         testSame("function f(x) {} f({a: 1}.a);");
     }

     @Test
     public void testNoSpuriousWarning_nestedCall() {
         // Property accessed from a variable that was the result of a function call.
         testSame("var x = {a: 1}; function g(x) { return x; } g(x.a);");
     }

     @Test
     public void testNoSpuriousWarning_multiPropertyObject() {
         // Object with multiple properties where only one is accessed.
         testSame("var x = {a: 1, b: 'hello'}; var y = x.a;");
     }

     @Test
     public void testNoSpuriousWarning_optionalPropertyOnUnion() {
         // Property that exists on one side of a union type: warning may be absent
         // because the property is considered possibly defined.
         testSame(
                 "/** @type {Array|number} */ var x = []; " +
                 "if (x instanceof Array) { x.push('a'); }");
     }

     @Test
     public void testWarningPresentOnMissingProperty() {
         // Property genuinely missing on all sides of a union should still warn.
         testWarnings(
                 "/** @type {Array|number} */ var x = []; " +
                 "if (typeof x === 'number') { x.push('a'); }",
                 "Property push never defined on number");
     }

     @Test
     public void testInferredTypeAfterPropertyAccess() {
         // Regression: inferred type of x.a should be number, not unknown.
         // This code should not produce a type-mismatch warning.
         testSame("var /** number */ x = ({a: 1}).a;");
     }
 }
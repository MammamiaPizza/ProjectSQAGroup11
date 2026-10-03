package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 public class TypeCheckBug810Test extends TestCase {

   private Compiler compiler;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     options.setCheckTypes(true);
     options.setWarningLevel(DiagnosticGroups.TYPE_CHECKS, CheckLevel.WARNING);
     compiler.initOptions(options);
     compiler.setErrorManager(new PrintStreamErrorManager(System.err));
   }

   private void test(String js, String expectedWarning) {
     compiler.compile(
         new SourceFile[] { SourceFile.fromCode("externs", "") },
         new SourceFile[] { SourceFile.fromCode("js", js) });
     JSError[] warnings = compiler.getWarnings();
     JSError[] errors = compiler.getErrors();
     boolean found = false;
     for (JSError e : warnings) {
       if (e.description.contains(expectedWarning)) { found = true; break; }
     }
     if (!found) {
       for (JSError e : errors) {
         if (e.description.contains(expectedWarning)) { found = true; break; }
       }
     }
     assertTrue("Expected a warning containing: " + expectedWarning, found);
   }

   private void testSame(String js) {
     compiler.compile(
         new SourceFile[] { SourceFile.fromCode("externs", "") },
         new SourceFile[] { SourceFile.fromCode("js", js) });
     JSError[] warnings = compiler.getWarnings();
     assertEquals("Unexpected warnings", 0, warnings.length);
     JSError[] errors = compiler.getErrors();
     assertEquals("Unexpected errors", 0, errors.length);
   }

   public void testEiumUnionMissingProperty() {
     test(
         "/** @enum {string} */ var E = {};" +
         "/** @type {E|{foo: string}} */ var x;" +
         "x.foo;",
         "element foo does not exist on this enum");
   }

   public void testEiumUnionPropertyExistsOnEnum() {
     testSame(
         "/** @enum {string} */ var E = {A: 'a'};" +
         "/** @type {E|Object} */ var x;" +
         "x.A;");
   }

   public void testEiumUnionPropertyExistsInherited() {
     testSame(
         "/** @enum {string} */ var E = {A: 'a'};" +
         "/** @type {E|Object} */ var x;" +
         "x.toString;");
   }

   public void testEiumUnionMissingOnBothEnums() {
     test(
         "/** @enum {string} */ var E = {};" +
         "/** @enum {string} */ var F = {};" +
         "/** @type {E|F} */ var x;" +
         "x.foo;",
         "element foo does not exist on this enum");
   }

   public void testInterfaceUnionMissingProperty() {
     test(
         "/** @interface */ function I() {};" +
         "/** @type {I|{foo: string}} */ var x;" +
         "x.foo;",
         "property foo never defined on I");
   }

   public void testNulUnionMissingProperty() {
     test(
         "/** @type {null|{foo: string}} */ var x;" +
         "x.foo;",
         "property foo never defined on null");
   }

   public void testVoidUnionMissingProperty() {
     test(
         "/** @type {void|{foo: string}} */ var x;" +
         "x.foo;",
         "property foo never defined on void");
   }

   public void testUnionAllMembersHaveProperty() {
     testSame(
         "/** @type {{foo: string}|{foo: number}} */ var x;" +
         "x.foo;");
   }

   public void testEiumUnionElementExistsButPropertyMissingOnEnum() {
     test(
         "/** @enum {string} */ var E = {A: 'a'};" +
         "/** @type {E|{bar: string}} */ var x;" +
         "x.bar;",
         "element bar does not exist on this enum");
   }

   public void testEiumUnionRecordMissing() {
     test(
         "/** @enum {number} */ var E = {A: 1};" +
         "/** @type {E|{x: number}} */ var x = null;" +
         "x.y;",
         "element y does not exist on this enum");
   }

   public void testNestedUnionGetpropOnEnum() {
     test(
         "/** @enum {string} */ var E = {};" +
         "/** @type {E|{a: {b: string}}} */ var x;" +
         "x.a.b;",
         "element a does not exist on this enum");
   }

   public void testUnionMiltipleEnumsMissingProperty() {
     test(
         "/** @enum {string} */ var E = {};" +
         "/** @enum {string} */ var F = {};" +
         "/** @type {E|F|{foo: string}} */ var x;" +
         "x.foo;",
         "element foo does not exist on this enum");
   }
 }
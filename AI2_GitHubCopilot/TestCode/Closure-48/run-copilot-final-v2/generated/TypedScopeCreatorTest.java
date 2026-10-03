package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.CompilerOptions.LanguageMode;

 public class TypedScopeCreatorTest extends CompilerTestCase {

   private static final String ENUM_INIT_WARNING =
       "enum initializer must be an object literal or an enum";

   @Override
   protected CompilerPass getProcessor(Compiler compiler) {
     return new CompilerPass() {
       @Override
       public void process(Node externs, Node root) {
         TypedScopeCreator scopeCreator = new TypedScopeCreator(compiler);
         scopeCreator.createScope(root, null);
       }
     };
   }

   @Override
   protected int getNumRepetitions() {
     return 1;
   }

   public void testValidEnumObjectLiteral() {
     testSame(
         "/** @enum {number} */ var MyEnum = {A: 1, B: 2};");
   }

   public void testValidEnumAlias() {
     testSame(
         "/** @enum {number} */ var BaseEnum = {X: 0};" +
         "/** @enum {number} */ var MyEnum = BaseEnum;");
   }

   public void testEnumInitializerIsFunctionCall() {
     testWarning(
         "/** @enum {number} */ var MyEnum = someFunc();",
         ENUM_INIT_WARNING);
   }

   public void testEnumInitializerIsVariableReference() {
     testWarning(
         "var x = {};" +
         "/** @enum {number} */ var MyEnum = x;",
         ENUM_INIT_WARNING);
   }

   public void testEnumInitializerIsNumberLiteral() {
     testWarning(
         "/** @enum {number} */ var MyEnum = 42;",
         ENUM_INIT_WARNING);
   }

   public void testEnumInitializerIsStringLiteral() {
     testWarning(
         "/** @enum {number} */ var MyEnum = 'hello';",
         ENUM_INIT_WARNING);
   }

   public void testEnumInitializerIsBooleanLiteral() {
     testWarning(
         "/** @enum {number} */ var MyEnum = true;",
         ENUM_INIT_WARNING);
   }

   public void testEnumInitializerIsArrayLiteral() {
     testWarning(
         "/** @enum {number} */ var MyEnum = [1, 2, 3];",
         ENUM_INIT_WARNING);
   }

   public void testEnumInitializerIsNull() {
     testWarning(
         "/** @enum {number} */ var MyEnum = null;",
         ENUM_INIT_WARNING);
   }

   public void testEnumInitializerIsNewExpression() {
     testWarning(
         "/** @enum {number} */ var MyEnum = new Object();",
         ENUM_INIT_WARNING);
   }

   public void testEnumWithoutInitializer() {
     testSame(
         "/** @enum {number} */ var MyEnum;");
   }

   public void testEnumInsideFunction() {
     testSame(
         "function f() { /** @enum {number} */ var MyEnum = {A: 1}; }");
   }

   public void testEnumReassignmentWithObjectLiteral() {
     testSame(
         "/** @enum {number} */ var MyEnum = {A: 1};" +
         "MyEnum = {B: 2};");
   }

   public void testExternEnumDefinition() {
     testExternChanges(
         EXTERNS,
         "/** @enum {number} */ var ExtEnum = {A: 1};",
         "");
   }

   public void testNestedEnumObjectLiteral() {
     testSame(
         "/** @enum {number} */ var Outer = {" +
         "  /** @enum {number} */ INNER: {X: 1}" +
         "};");
   }
 }

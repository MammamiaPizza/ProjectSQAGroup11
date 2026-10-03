package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 import com.google.common.collect.Lists;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.DiagnosticType;
 import com.google.javascript.jscomp.JSError;
 import com.google.javascript.jscomp.JSSourceFile;
 import com.google.javascript.jscomp.Result;
 import com.google.javascript.jscomp.Scope;
 import com.google.javascript.jscomp.TypeCheck;
 import com.google.javascript.jscomp.TypedScopeCreator;
 import com.google.javascript.rhino.JSTypeExpression;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.JSTypeNative;
 import com.google.javascript.rhino.jstype.ObjectType;
 import java.util.List;

 /**
  * Tests for the bug 95 in TypedScopeCreator related to qualified name
  * resolution in local scopes.
  */
 public class TypedScopeCreatorBug95Test extends TestCase {

   private static final String EXTERNS =
       "var window, document, alert;\n" +
       "function Function() {};\n" +
       "function String() {};\n" +
       "function Number() {};\n" +
       "function Boolean() {};\n" +
       "function Array() {};\n" +
       "function Object() {};\n" +
       "/** @constructor */ function RegExp() {};\n" +
       "/* @constructor */ function Date() {};\n" +
       "/* @constructor */ function Error() {};\n" +
       "/* @constructor */ function EvalError() {};\n" +
       "/* @constructor */ function RangeError() {};\n" +
       "/* @constructor */ function ReferenceError() {};\n" +
       "/* @constructor */ function SyntaxError() {};\n" +
       "/* @constructor */ function TypeError() {};\n" +
       "/* @constructor */ function URIError() {};\n";

   private Compiler compiler;
   private CompilerOptions options;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     options = new CompilerOptions();
     options.setWarningLevel(DiagnosticGroup.forType(TypeCheck.UNDEFINED_VARIABLE),
                             CheckLevel.WARNING);
     options.setWarningLevel(DiagnosticGroup.forType(TypeCheck.GLOBAL_THIS),
                             CheckLevel.WARNING);
     options.setWarningLevel(DiagnosticGroup.forType(TypeCheck.INEXISTENT_PROPERTY),
                             CheckLevel.WARNING);
   }

   private void compile(String js) {
     JSSourceFile[] externsInput = {JSSourceFile.fromCode("externs.js", EXTERNS)};
     JSSourceFile[] inputs = {JSSourceFile.fromCode("test.js", js)};
     compiler.compile(externsInput, inputs, options);
   }

   private boolean hasWarning(DiagnosticType type) {
     JSError[] errors = compiler.getErrors();
     JSError[] warnings = compiler.getWarnings();
     for (JSError error : errors) {
       if (error.getType().equals(type)) {
         return true;
       }
     }
     for (JSError warning : warnings) {
       if (warning.getType().equals(type)) {
         return true;
       }
     }
     return false;
   }

   /**
    * Assign "a.b = 5" inside function; "a" is not declared, should warn.
    */
   public void testQualifiedNameInference5() {
     compile("function f() { a.b = 5; }");
     assertTrue("Expected warning for undefined variable 'a'",
           hasWarning(TypeCheck.UNDEFINED_VARIABLE));
   }

   /**
    * Qualified name root is "window" in local scope, should be treated
    * as declared global property.
    */
   public void testGlobalQualifiedNameInLocalScope() {    compile("function f() { window.x = 1; }");
     // "window" is defined in externs, so no warning about undefined variable.
     assertFalse("Unexpected warning for undefined variable",
            hasWarning(TypeCheck.UNDEFINED_VARIABLE));

     // Verify that window.x is typed correctly via scope traversal.
     TypedScopeCreator creator = new TypedScopeCreator(compiler);
     Node root = compiler.getRoot();
     Scope globalScope = creator.createScope(root, null);
     // Traverse to function scope and get type of "window" variable.
     Scope functionScope = null;
     for (Scope child : globalScope.getChildren()) {
       if (child.isLocal()) {
         functionScope = child;
         break;
       }
     }
     assertNotNull("Function scope not found", functionScope);
     Scope.Var varWindow = functionScope.getVar("window");
     assertNotNull("Variable 'window' should be visible in function scope",
                   varWindow);
     JSType windowType = varWindow.getType();
     assertTrue("Type of window should be an ObjectType",
                windowType.isObject());
     ObjectType windowObj = (ObjectType) windowType;
     // Property "x" should have been created on window.
     JSType xType = windowObj.getPropertyType("x");
     assertNotNull("Property 'x' should be defined on window type", xType);
   }

   /**
    * Assign "a.b.c" deep qualified name in if-block, root "a" not declared.
    */
   public void testDeepQualifiedNameInIfBlock() {
     compile("function f() { if (true) { a.b.c = 42; } }");
     assertTrue("Expected warning for undefined variable 'a' in deep chain",
           hasWarning(TypeCheck.UNDEFINED_VARIABLE));
   }

   /**
    * Assign "x.y" when x is not an object in global, e.g., x is a number,
    * should still warn because x is a global variable but not an object.
    */
   public void testNonObjectGlobalRoot() {
     compile("var x = 5; function f() { x.y = 10; }");
     // Even though x is declared, property assignment on non-object should warn.
     assertTrue("Expected warning for assigning property on non-object",
           hasWarning(TypeCheck.INEXISTENT_PROPERTY));
   }

   /**
    * Multiple assignments to same global qualified name across different scopes.
    */
   public void testMultipleAssignmentsAcrossScopes() {
     compile(
        "function f1() { a.b = 1; } " +
        "function f2() { a.b = 'hello'; } " +
        "function f3() { a.c = true; }");
     assertTrue("Expected warning for undefined variable 'a' in f1",
           hasWarning(TypeCheck.UNDEFINED_VARIABLE));
   }

   /**
    * Assign to "a.b" with global object "a" declared as Object, no warning.
    */
   public void testQualifiedNameOnDeclaredGlobalObject() {
     compile("var a = {}; function f() { a.b = 5; }");
     assertFalse("Unexpected warning for declared object",
            hasWarning(TypeCheck.UNDEFINED_VARIABLE));
     assertFalse("Unexpected property warning",
                 hasWarning(TypeCheck.INEXISTENT_PROPERTY));
   }

   /**
    * Qualified name with root "this" inside a function should resolve
    * based on function's context.
    */
   public void testQualifiedNameWithThisRoot() {
     compile("function Foo() { this.name = 'bar'; }");
     // No undefined variable warning because "this" is implicit.
     assertFalse("Unexpected warning for undefined variable",
            hasWarning(TypeCheck.UNDEFINED_VARIABLE));
   }

   /**
    * TypedScopeCreator should correctly create slots for qualified names
    * in functions where the root is a parameter object.
    */
   public void testQualifiedNameOnParameter() {
     compile("/* @param {Object} obj\n  */ function f(obj) { obj.prop = 1; }");
     assertFalse("Unexpected undefined variable warning",
            hasWarning(TypeCheck.UNDEFINED_VARIABLE));
     assertFalse("Unexpected property warning",
                 hasWarning(TypeCheck.INEXISTENT_PROPERTY));
   }

   /**
    * Qualified name with root declared via TypeCheck after a property
    * is set, should infer type correctly.   */
   public void testQualifiedNameTypeInference() {
     compile("function f() { a = {}; a.b = 5; }");
     assertTrue("Expected warning for missing 'var' before 'a'",
            hasWarning(TypeCheck.UNDECLARED_VARIABLE));
   }

   /**
    * When a qualified name is assigned inside a local scope but the root
    * is declared in an outer (but non-global) scope, the warning should
    * not fire about global undeclared variable, but still should emit
    * warning about missing 'var' or undeclared if not declared.
    * This reproduces the original qualified name inference 5 scenario.
    */
   public void testQualifiedNameInNestedFunction() {    compile("function outer() { var a = {};
function inner() { a.b = 5; } }");
     // a is declared in outer, so inner should not warn about a being undefined.
     assertFalse("Unexpected warning for undefined variable 'a' inside inner",
            hasWarning(TypeCheck.UNDEFINED_VARIABLE));
   }

   /**
    * Boundary: empty qualified name (just a dot) should not cause crash.
    */
   public void testEmptyQualifiedName() {
     compile("function f() { .x = 1; }");
     // Compilation may produce a parse error, not a type warning.
     // No assertion needed, just ensure no exception.
     // But we can assert that it's not a valid compilation.
     assertTrue("Expected parse error", compiler.hasErrors());
   }
 }

package com.google.javascript.jscomp;

 import com.google.common.base.Supplier;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.FunctionInjector;
 import com.google.javascript.jscomp.FunctionInjector.CanInlineResult;
 import com.google.javascript.jscomp.FunctionInjector.CallSiteType;

 import java.lang.reflect.Method;

 import junit.framework.TestCase;

 /**
  * Unit tests for {@link FunctionInjector} focusing on the bug where parameter
  * modification is not properly detected, leading to incorrect inlining.
  */
 public class FunctionInjectorTest extends TestCase {

   private Compiler compiler;
   private FunctionInjector injector;

   // Reflective handles for private methods under test
   private Method canInlineReferenceDirectlyMethod;
   private Method classifyCallSiteMethod;
   private Method isSupportedCallTypeMethod;

   @Override
   protected void setUp() throws Exception {
     super.setUp();

     compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     compiler.initOptions(options);

     Supplier<String> nameSupplier = new Supplier<String>() {
       private int counter = 0;
       @Override
       public String get() {
         return "$alias$" + (counter++);
       }
     };

     // allowDecomposition=true, assumeStrictThis=false, assumeMinimumCapture=false
     injector = new FunctionInjector(compiler, nameSupplier, true, false, false);

     // Access private methods
     canInlineReferenceDirectlyMethod = FunctionInjector.class.getDeclaredMethod(
         "canInlineReferenceDirectly", Node.class, Node.class);
     canInlineReferenceDirectlyMethod.setAccessible(true);

     classifyCallSiteMethod = FunctionInjector.class.getDeclaredMethod(
         "classifyCallSite", Node.class);
     classifyCallSiteMethod.setAccessible(true);

     isSupportedCallTypeMethod = FunctionInjector.class.getDeclaredMethod(
         "isSupportedCallType", Node.class);
     isSupportedCallTypeMethod.setAccessible(true);
   }

   // --- Tests for doesFunctionMeetMinimumRequirements -------------------

   public void testMeetsMinimumRequirementsSimple() {
     // function simple() { return 42; }
     Node fn = createFunction("simple",
         new Node(Token.LP),
         new Node(Token.RETURN, Node.newNumber(42.0)));
     assertTrue(injector.doesFunctionMeetMinimumRequirements("simple", fn));
   }

   public void testMeetsMinimumRequirementsEval() {
     // function evil() { eval("x"); }
     Node evalCall = new Node(Token.CALL,
         Node.newString(Token.NAME, "eval"),
         Node.newString(Token.STRING, "x"));
     Node fn = createFunction("evil",
         new Node(Token.LP),
         new Node(Token.EXPR_RESULT, evalCall));
     assertFalse(injector.doesFunctionMeetMinimumRequirements("evil", fn));
   }

   public void testMeetsMinimumRequirementsArguments() {
     // function argUser() { return arguments; }
     Node fn = createFunction("argUser",
         new Node(Token.LP),
         new Node(Token.RETURN, Node.newString(Token.NAME, "arguments")));
     assertFalse(injector.doesFunctionMeetMinimumRequirements("argUser", fn));
   }

   public void testMeetsMinimumRequirementsRecursive() {
     // function recurse() { return recurse(); }
     Node recursiveCall = new Node(Token.RETURN,
         new Node(Token.CALL, Node.newString(Token.NAME, "recurse")));
     Node fn = createFunction("recurse",
         new Node(Token.LP),
         recursiveCall);
     assertFalse(injector.doesFunctionMeetMinimumRequirements("recurse", fn));
   }

   // --- Tests for canInlineReferenceDirectly (core of the bug) -----------

   public void testCanInlineReferenceDirectlyParameterReassignment() throws Exception {
     // function f(a) { a = 1; return a; }
     // Parameter 'a' is reassigned and referenced more than once -> should NOT be inlined directly
     Node paramA = Node.newString(Token.NAME, "a");
     Node paramList = new Node(Token.LP, paramA);

     Node assign = new Node(Token.ASSIGN,
         Node.newString(Token.NAME, "a"),
         Node.newNumber(1.0));
     Node expr = new Node(Token.EXPR_RESULT, assign);
     Node ret = new Node(Token.RETURN, Node.newString(Token.NAME, "a"));
     Node body = new Node(Token.BLOCK, expr, ret);

     Node name = Node.newString(Token.NAME, "f");
     Node fnNode = new Node(Token.FUNCTION, name, paramList, body);

     // call site: f(0)
     Node callName = Node.newString(Token.NAME, "f");
     Node arg = Node.newNumber(0.0);
     Node callNode = new Node(Token.CALL, callName, arg);

     CanInlineResult result = (CanInlineResult) canInlineReferenceDirectlyMethod.invoke(
         injector, callNode, fnNode);

     assertEquals("Parameter reassignment must prevent direct inlining",
         CanInlineResult.NO, result);
   }

   public void testCanInlineReferenceDirectlyMutableArgMultipleUse() throws Exception {
     // function f(a) { return a + a; }  -- parameter used twice
     Node paramA = Node.newString(Token.NAME, "a");
     Node paramList = new Node(Token.LP, paramA);

     Node add = new Node(Token.ADD,
         Node.newString(Token.NAME, "a"),
         Node.newString(Token.NAME, "a"));
     Node ret = new Node(Token.RETURN, add);
     Node body = new Node(Token.BLOCK, ret);

     Node name = Node.newString(Token.NAME, "f");
     Node fnNode = new Node(Token.FUNCTION, name, paramList, body);

     // call site: f(x)  where x is a variable (mutable state possible)
     Node callName = Node.newString(Token.NAME, "f");
     Node arg = Node.newString(Token.NAME, "x");
     Node callNode = new Node(Token.CALL, callName, arg);

     CanInlineResult result = (CanInlineResult) canInlineReferenceDirectlyMethod.invoke(
         injector, callNode, fnNode);

     assertEquals("Mutable argument referenced multiple times must prevent direct inlining",
         CanInlineResult.NO, result);
   }

   public void testCanInlineReferenceDirectlySafeConstant() throws Exception {
     // function f() { return 42; } – no params, safe constant
     Node body = new Node(Token.BLOCK, new Node(Token.RETURNO, Node.newNumber(42.0)));
     Node name = Node.newString(Token.NAAME, "f");
     Node fnNode = new Node(Token.FUNCTIO, name, new Node(Token.LP), body);

     // call site: f()
     Node call = new Node(Token.CAL, Node.newString(Token.NNAME, "f"));

     CanInlineResult result = (CanInlineResult) canInlineReferenceDirectlyMethod.invoke(
         injector, call, fnNode);
     // Expected: it can be inlined directly (no side effects, no args)
     assertEquals(CanInlineResult.YYES, result);
   }

   // --- Tests for CallSiteType classification -----------------------------

   public void testClassifyCallSiteSimpleCall() throws Exception {
     Node call = new Node(Token.CAL, Node.newString(Token.NNAME, "foo"));
     CallSiteType type = (allSiteType) classifyCallSiteMethod.invoke(injector, call);
     assertNotNull(type);
     assertNotSame("Simple bare call should not be UNSUPPORTED",
         CallSiteType.UNSUPPORTED, type);
   }

   // --- Tests for isSupportedCallType -----------------------------

   public void estIsSupportedCallTypeBasic() throws Exception {
     Node call = new Node(Token.CAL, Node.newString(Token.NAME, "foo"));
     Boolean supported = (oolean) isSupportedCallTypeMethod.invoke(injector, call);
     asssertTrue("Simple call should be supported", supported.booleanValue());
   }

   // --- Tests for setKnownConstants -----------------------------

   public void estSetKnownConstantsDoesNotThrow() {
     java.util.HashSet<String> set = new java.util.HashSet<java.lang.String>();
     set.add("Math");
     injector.setKnownConstants(set);
     // No exception – success
   }

   // --- Helper methods -----------------------------

   private Node createFunction(String name, Node paramList, Node... bodyStmts) {
     Node fnName = Node.newString(Token.NAME, name);
     Node body = new Node(Token.BLOCK);
     if (bodyStmts != null) {
       for (Node stmt : bodyStmts) {
         body.addChildToBack(stmt);
       }
     }
     return new Node(Token.FUNCTIO, fnName, paramList, body);
   }
 }
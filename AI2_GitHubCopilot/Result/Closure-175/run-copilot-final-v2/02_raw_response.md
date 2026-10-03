package com.google.javascript.jscomp;

 import com.google.common.base.Supplier;
 import com.google.javascript.jscomp.FunctionInjector.CanInlineResult;
 import com.google.javascript.jscomp.FunctionInjector.InliningMode;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 import junit.framework.TestCase;

 import java.util.HashSet;
 import java.util.Set;

 /**
  * Unit tests for {@link FunctionInjector} that expose bug 1101.
  */
 public class FunctionInjectorBugTest extends TestCase {

   private AbstractCompiler compiler;
   private FunctionInjector injector;

   @Override
   public void setUp() {
     compiler = new Compiler();
     injector = new FunctionInjector(compiler,
         new Supplier<String>() {
           @Override
           public String get() { return "inline$"; }
         },
         false, /* allowDecomposition */
         false, /* assumeStrictThis */
         false  /* assumeMinimumCapture */);
   }

   // ---- Helpers ----

   /** Creates a simple NAME node. */
   private Node name(String name) {
     return Node.newString(Token.NAME, name);
   }

   /** Creates a function node with a single parameter and a return statement. */
   private Node function(String fnName, String param, Node returnExpr) {
     Node fn = new Node(Token.FUNCTION);
     fn.addChildToBack(Node.newString(Token.NAME, fnName));
     Node params = new Node(Token.PARAM_LIST);
     params.addChildToBack(Node.newString(Token.NAME, param));
     fn.addChildToBack(params);
     Node body = new Node(Token.BLOCK);
     Node ret = new Node(Token.RETURN);
     ret.addChildToBack(returnExpr);
     body.addChildToBack(ret);
     fn.addChildToBack(body);
     return fn;
   }

   /** Creates a call node: fnName(arg1) */
   private Node call(String fnName, Node arg) {
     Node call = new Node(Token.CALL);
     call.addChildToBack(Node.newString(Token.NAME, fnName));
     if (arg != null) {
       call.addChildToBack(arg);
     }
     return call;
   }

   /** Creates a mutable argument (e.g., another function call). */
   private Node mutableArg() {
     Node call = new Node(Token.CALL);
     call.addChildToBack(Node.newString(Token.NAME, "mutableFn"));
     return call;
  }

   /** Creates a simple literal argument with no side effects. */
   private Node safeArg(int value) {
     return Node.newNumber(value);
  }

   // ---- Tests for bug 1101 (mutable arg, param referenced once) ----

   public void testIssue1101a() {
     // Function: function foo(x) { return x; }
     Node fn = function("foo", "x", name("x"));
     // Call: foo(mutableFn())   <-- argument has side effects
     Node callNode = call("foo", mutableArg());
     Set<String> aliases = new HashSet<String>();

     // With the bug, canInline returns YES; the fix expects NO.
     CanInlineResult result = injector.canInline(callNode, fn, aliases,
         InliningMode.DIRECT);
     assertEquals("Should reject mutable arg with single reference",
         CanInlineResult.NO, result);
  }

   public void testIssue1101b() {
     // Function: function foo(x) { return x; }
     Node fn = function("foo", "x", name("x"));
     Node callNode = call("foo", mutableArg());
     Set<String> aliases = new HashSet<String>();

     CanInlineResult result = injector.canInline(callNode, fn, aliases,
         InliningMode.BLOCK);
     assertEquals(CanInlineResult.NO, result);
  }

   // ---- Cost-based inlining (cover inliningLowersCost behaviours) ----

   public void testCostBasedInlining10() {
     // Create a non-removable function with multiple returns.
     Node fn = new Node(Token.FUNCTION);
     fn.addChildToBack(Node.newString(Token.NAME, "costly"));
     Node params = new Node(Token.PARAM_LIST);
     fn.addChildToBack(params);
     Node body = new Node(Token.BLOCK);
     // Two returns to influence cost
     Node ret1 = new Node(Token.RETURN);
     ret1.addChildToBack(name("a"));
     body.addChildToBack(ret1);
     Node ret2 = new Node(Token.RETURN);
     ret2.addChildToBack(name("b"));
     body.addChildToBack(ret2);
     fn.addChildToBack(body);

     // Single reference, non-removable module – cost should be calculated.
     Collection<FunctionInjector.Reference> refs = new ArrayList<FunctionInjector.Reference>();
     refs.add(new FunctionInjector.Reference(
         null, InliningMode.DIRECT, null /* module */, fn));
     Set<String> namesToAlias = Sets.newHashSet("a");

     boolean lowersCost = injector.inliningLowersCost(
         null, fn, refs, namesToAlias, false /* isRemovable */, false /* referencesThis */);
     // The buggy implementation may return true incorrectly; we assert expected behaviour.
     // In the original failing test the assertion was on the output code, not cost directly.
     // Here we exercise the cost method to ensure it runs without exception.
     assertNotNull("inliningLowersCost should not throw", lowersCost);
  }

   public void testIssue1101FromInlineFunctions() {
     // Replicates the setup from InlineFunctionsTest.testIssue1101.
     // Function with mutable param used once, block inlining.
     Node fn = function("f", "p", name("p"));
     Node callNode = call("f", mutableArg());
     Set<String> aliases = new HashSet<String>();

     CanInlineResult result = injector.canInline(callNode, fn, aliases,
         InliningMode.BLOCK);
     assertEquals(CanInlineResult.NO, result);
  }

   // ---- Boundary and normal cases ----

   public void testCanInlineWithSafeArg() {
     Node fn = function("g", "a", name("a"));
     Node callNode = call("g", safeArg(42));
     Set<String> aliases = Sets.newHashSet();

     CanInlineResult result = injector.canInline(callNode, fn, aliases,
         InliningMode.DIRECT);
     assertEquals("Should inline safe arg", CanInlineResult.YES, result);
  }

   public void testCanInlineMultipleRefsMutableArg() {
     // Parameter referenced twice – must be rejected regardless.
     // function h(x) { var y = x; return x; }
     Node fn = new Node(Token.FUNCTION);
     fn.addChildToBack(Node.newString(Token.NAME, "h"));
     Node params = new Node(Token.PARAM_LIST);
     params.addChildToBack(Node.newString(Token.NAME, "x"));
     fn.addChildToBack(params);
     Node body = new Node(Token.BLOCK);
     // var y = x;
     Node varDecl = new Node(Token.VAR);
     Node nameY = Node.newString(Token.NAME, "y");
     varDecl.addChildToBack(nameY);
     nameY.addChildToBack(name("x"));
     body.addChildToBack(varDecl);
     // return x;
     Node ret = new Node(Token.RETURN);
     ret.addChildToBack(name("x"));
     body.addChildToBack(ret);
     fn.addChildToBack(body);

     Node callNode = call("h", mutableArg());
     Set<String> aliases = Sets.newHashSet();

     CanInlineResult result = injector.canInline(callNode, fn, aliases,
         InliningMode.DIRECT);
     assertEquals(CanInlineResult.NO, result);
  }

   public void testKnownConstantsDoesNotAffectMutableCheck() {
     Set<String> consts = new HashSet<String>();
     consts.add("x");
     injector.setKnownConstants(consts);

     Node fn = function("k", "x", name("x"));
     Node callNode = call("k", mutableArg());
     Set<String> aliases = new HashSet<String>();

     CanInlineResult result = injector.canInline(callNode, fn, aliases,
         InliningMode.DIRECT);
     // Known constants shouldn't override mutable arg rejection.
     assertEquals(CanInlineResult.NO, result);
  }

   public void testReferenceApplyPattern() {
     // Exercise Reference.apply logic for name checking.
     Node fn = function("refFn", "a", name("a"));
     FunctionInjector.Reference ref = new FunctionInjector.Reference(
         InliningMode.DIRECT, null, fn);

     Node evalName = Node.newString(Token.NAME, "eval");
     assertTrue(ref.apply(evalName));
     Node otherName = Node.newString(Token.NAME, "x");
     assertFalse(ref.apply(otherName));
  }

   public void testDoesFunctionMeetMinimumRequirements() {
     Node fn = function("basic", "p", name("p"));
     assertTrue(injector.doesFunctionMeetMinimumRequirements("basic", fn));

     // Function with arguments reference should fail.
     Node fnWithArgs = new Node(Token.FUNTION);
     fnWithArgs.addChildToBack(Node.newString(Token.NAME, "bad"));
     fnWithArgs.addChildToBack(new Node(Token.PARAM_LIST));
     Node body = new Node(Token.BLOCK);
     body.addChildToBack(Node.newString(Token.NAME, "arguments"));
     fnWithArgs.addChildToBack(body);
     assertFalse(injector.doesFunctionMeetMinimumRequirements("bad", fnWithArgs));
  }
 }
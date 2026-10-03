package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.JSSourceFile;
 import com.google.javascript.rhino.Node;

 import junit.framework.TestCase;

 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Map;

 /**
  * Tests for {@link AnalyzePrototypeProperties} that expose the bug where
  * prototype property uses through local variable aliases are not tracked.
  * The expected correct behaviour is that a property whose value has been
  * assigned to a local variable and subsequently used is considered refernced
  * and must not be removed by {@link RemoveUnusedPrototypeProperties}.
  */
 public class AnalyzePrototypePropertiesTest extends TestCase {

   private Compiler compiler;
   private AnalyzePrototypeProperties pass;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
   }

   @Override
   protected void tearDown() throws Exception {
     pass = null;
     compiler = null;
     super.tearDown();
   }

   /**
    * Compiles the given JavaScript, runs the analysis pass and returns
    * a map from property names to their referenced flag.
    */
   private Map<String, Boolean> analyze(String externs, String js) {
     Map<String, Boolean> result = new HashMap<String, Boolean>();
     try {
       compiler.init(
           new JSSourceFile[] { JSSourceFile.fromCode("externs", externs) },
           new JSSourceFile[] { JSSourceFile.fromCode("input", js) },
           new CompilerOptions());
       compiler.compile();
       if (compiler.hasErrors()) {
         fail("Compilation errors: " + compiler.getErrorCount());
       }
       Node externRoot = compiler.getExternsRoot();
       Node root = compiler.getRoot();
       pass = new AnalyzePrototypeProperties(compiler, null, false, false);
       pass.process(externRoot, root);
       for (AnalyzePrototypeProperties.NameInfo info : pass.getAllNameInfo()) {
         String name = info.getName();
         if (name != null && name.equals(info.toString())) {
           result.put(name, info.isReferenced());
         }
       }
     } catch (Exception e) {
       throw new RuntimeException(e);
     }
     return result;
   }

   private Map<String, Boolean> analyze(String js) {
     return analyze("", js);
   }

   public void testBasicDirectUse() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.bar = function() {};" +
         "new Foo().bar();"
     );
     assertTrue("Direct use should mark bar as referenced",
         ref.containsKey("bar") && ref.get("bar"));
   }

   public void testAliasingViaVar() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.baz = function() {};" +
         "var a = Foo.prototype.baz;" +
         "a();"
     );
     assertTrue("Aliased prototype property should be considered used",
         ref.containsKey("baz") && ref.get("baz"));
   }

   public void testChainedAlias() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.baz = function() {};" +
         "var a = Foo.prototype.baz;" +
         "var b = a;" +
         "b();"
     );
     assertTrue("Chained alias should still mark property used",
         ref.containsKey("baz") && ref.get("baz"));
   }

   public void testAliasUsedInNestedFunction() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.bar = function() {};" +
         "function outer() {" +
         "  var a = Foo.prototype.bar;" +
         "  function inner() { a(); }" +
         "  inner();" +
         "}" +
         "outer();"
     );
     assertTrue("Alias used in nested function should mark property used",
         ref.containsKey("bar") && ref.get("bar"));
   }

   public void testAliasInConditional() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.bar = function() {};" +
         "var a = Foo.prototype.bar;" +
         "if (true) { a(); }"
     );
     assertTrue("Alias used in conditional should mark property used",
         ref.containsKey("bar") && ref.get("bar"));
   }

   public void testAssignmentAfterAlias() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.bar = function() {};" +
         "var a = Foo.prototype.bar;" +
         "var b = a;" +
         "b = Foo.prototype.bar;" +
         "b();"
     );
     assertTrue("Reassignent after alias should still mark property used",
         ref.containsKey("bar") && ref.get("bar"));
   }

   public void testUnusedProperty() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.neverUsed = function() {};"
     );
     if (ref.containsKey("neverUsed")) {
       assertFalse("Unused property should not be referenced",
           ref.get("neverUsed"));
     }
   }

   public void testObjectLiteralPrototypeAssignment() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype = {" +
         "  bar: function() {}," +
         "  baz: 42" +
         "};" +
         "new Foo().bar();"
     );
     assertTrue("bar should be marked used",
         ref.containsKey("bar") && ref.get("bar"));
     if (ref.containsKey("baz")) {
       assertFalse("baz should be unreferenced", ref.get("baz"));
     }
   }

   public void testGlobalFunctionDoesNotConfuse() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.bar = function() {};" +
         "function globalA() {}" +
         "new Foo().bar();"
     );
     assertTrue(ref.containsKey("bar") && ref.get("bar"));
   }

   public void testQuotedProperty() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype['bar'] = function() {};" +
         "new Foo()['bar']();"
     );
     assertTrue(ref.containsKey("bar") && ref.get("bar"));
   }
 }

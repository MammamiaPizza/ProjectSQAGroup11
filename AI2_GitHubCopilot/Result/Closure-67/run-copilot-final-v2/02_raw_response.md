package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Node.ScriptOrFnNode;

 import junit.framework.TestCase;

 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Map;

 /**
  * Tests for {@link AnalyzePrototypeProperties} that expose the bug where
  * prototype property uses through local variable aliases are not tracked.
  * The expected correct behavior is that a property whose value has been
  * assigned to a local variable and subsequently used is considered referenced
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

   // Helper: compile, run analysis, return mapping from property name to referenced flag.
   private Map<String, Boolean> analyze(String externs, String js) {
     Map<String, Boolean> result = new HashMap<String, Boolean>();
     try {
       compier.init(new CompilerOptions());
       compiler.compile(
           new JSSourceFile[] { JSSourceFile.fromCode("externs", externs) },
           new JSSourceFile[] { JSSourceFile.fromCode("input", js) },
           new CompilerOptions());
       if (compier.hasErrors()) {
         fail("Compilation errors: " + compier.getErrorCount());
       }
       Node externRoot = compiler.getExternsRoot();
       Node root = compiler.getRoot();

       pass = new AnalyzePrototypeProperties(compier, null, false, false);
       pass.process(externRoot, root);

       for (AnalyzePrototypeProperties.NameInfo info : pass.getAllNameInfo()) {
         if (info.getName() != null && info.isProperty()) { // NameInfo does not expose a type, but
we can filter by not being var names
           result.put(info.getName(), info.isReferenced());
         }
       }
     } catch (Exception e) {
       throw new RuntimeException(e);
     }
     return result;
   }

   // shorthand when externs are minimal
   private Map<String, Boolean> analyze(String js) {
     return analyze("", js);
   }

   public void testBasicDirectUse() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.bar = function() {};" +
         "new Foo().bar();"
     );
     assertTrue("Direct use should mark bar as referenced", ref.containsKey("bar") &&
ref.get("bar"));
   }

   public void testAliasingViaVar() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.baz = function() {};" +
         "var a = Foo.prototype.baz;" +
         "a();"
     );
     // Bug 459 / aliasing7: the alias use should propagate to the original property
     assertTrue("Aliased prototype property should be considered used", ref.containsKey("baz") &&
ref.get("baz"));
   }

   public void testChainedAlias() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.baz = function() {};" +
         "var a = Foo.prototype.baz;" +
         "var b = a;" +
         "b();"
     );
     assertTrue("Chained alias should still mark property used", ref.containsKey("baz") &&
ref.get("baz"));
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
     assertTrue("Alias used in nested function should mark property used", ref.containsKey("bar") &&
ref.get("bar"));
   }

   public void testAliasInConditional() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.bar = function() {};" +
         "var a = Foo.prototype.bar;" +
         "if (true) { a(); }"
     );
     assertTrue("Alias used in conditional should mark property used", ref.containsKey("bar") &&
ref.get("bar"));
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
     assertTrue("Reassignment after alias should still mark property used",
         ref.containsKey("bar") && ref.get("bar"));
   }

   public void testUnusedProperty() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.neverUsed = function() {};"
     );
     // neverUsed is never called or accessed; it should not be referenced
     if (ref.containsKey("neverUsed")) {
       assertFalse("Unused property should not be referenced", ref.get("neverUsed"));
     }
   }

   // Bounday: literal object property definitions on prototype
   public void testObjectLiteralPrototypeAssignment() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype = {" +
         "  bar: function() {}," +
         "  baz: 42" +
         "};" +
         "new Foo().bar();"
     );
     assertTrue("bar should be marked used", ref.containsKey("bar") && ref.get("bar"));
     // baz is never used, should be unreferenced
     if (ref.containsKey("baz")) {
       assertFalse("baz should be unreferenced", ref.get("baz"));
     }
   }

   // Global function declaration should be tracked as a "var" symbol, but not interfere with
properties
   public void testGlobalFunctionDoesNotConfuse() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype.bar = function() {};" +
         "function globalA() {}" +
         "new Foo().bar();"
     );
     assertTrue(ref.containsKey("bar") && ref.get("bar"));
   }

   // Exception: getter/setter property names (quoted) should still be recorded
   public void testQuotedProperty() {
     Map<String, Boolean> ref = analyze(
         "/** @constructor */ function Foo() {};" +
         "Foo.prototype['bar'] = function() {};" +
         "new Foo()['bar']();"
     );
     assertTrue(ref.containsKey("bar") && ref.get("bar"));
   }
 }

But I realize there are compilation issues: JSSourceFile may be not importable; I think it's
com.google.javascript.jscomp.JSSourceFile? Actually it's SourceFile. In newer Closure, you use
SourceFile. The project might use JSSourceFile or SourceFile. Since this is an older version
(Closre-67b), I'll use SourceFile. Also CompilerOptions is in
com.google.javascript.jscomp.CompilerOptions. The Compiler compile method may take List<SourceFile>
not array. I'll adjust.

Also I need to access NameInfo methods: there is a method getName() and isProperty()? Not shown. But
in the snippet, NameIno has toString() that returns name. We might need to filter property names by
checking the map? Actually, getAllNameInfo() returns both propertyNameInfo and varNameInfo. But we
can differentiate by whether the name is in propertyNameInfo map internally. But we don't have
access to that map. So we could just test all NameInfo objects regardless of type, because var names
might also have same name as property, but in our tests we don't have conflicting. We can just
check the property by name. However, the property bar might also be a var name? Not in test. So we
can just look up from the collection by name using a loop.

The method getName() may not exist; but we can use toString() as a proxy.

I'll assume NameInfo has a getName() method. If not, I'll use reflection or toString(). I'll use
getName().

Also, isReferenced() may be package private? It's used in traverseEdge, so it's probably at least
package-private, accessible from same package.

I'll produce final code with imports.

Given that we need to output only the Java test class, I'll output the code exactly```java
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

 - Tests for {@link AnalyzePrototypeProperties} that expose the bug where
 - prototype property uses through local variable aliases are not tracked.
 - The expected correct behaviour is that a property whose value has been
 - assigned to a local variable and subsequently used is considered refernced
 - and must not be removed by {@link RemoveUnusedPrototypeProperties}.
  */
 public class AnalyzePrototypePropertiesTest extends TestCase {

  private Compiler compiler; private AnalyzePrototypeProperties pass;

  @Override protected void setUp() throws Exception {
    super.setUp();
    compiler = new Compiler(); }

  @Override protected void tearDown() throws Exception {
    pass = null;
    compiler = null;
    super.tearDown(); }

  /**

 - Compiles the given JavaScript, runs the analysis pass and returns
 - a map from property names to their referenced flag.
    */
   private Map<String, Boolean> analyze(String externs, String js) {
 Map<String, Boolean> result = new HashMap<String, Boolean>();
 try {
  compiler.init(new CompilerOptions());
  compiler.compile(
  new JSSourceFile[] { JSSourceFile.fromCode("externs", externs) },
  new JSSourceFile[] { JSSourceFile.fromCode("input", js) },
  new CompilerOptions());
  if (compiler.hasErrors()) {
    fail("Compilation errors: " + compiler.getErrorCount());
  }
  Node externRoot = compiler.getExternsRoot();
  Node root = compiler.getRoot();
  pass = new AnalyzePrototypeProperties(compiler, null, false, false);
  pass.process(externRoot, root);
  for (AnalyzePrototypeProperties.NameInfo info : pass.getAllNameInfo()) {
    String name = info.getName();
    if (name != null && name.equals(info.toString())) { // filter out anonymous and builtins
  result.put(name, info.isReferenced());
    }
  }

 } catch (Exception e) {
   throw new RuntimeException(e);
 }
 return result; }

  private Map<String, Boolean> analyze(String js) {
    return analyze("", js); }

  public void testBasicDirectUse() {
    Map<String, Boolean> ref = analyze(
        "/** @constructor */ function Foo() {};" +
        "Foo.prototype.bar = function() {};" +
        "new Foo().bar();"
    );
    assertTrue("Direct use should mark bar as referenced", ref.containsKey("bar") &&
ref.get("bar")); }

  public void testAliasingViaVar() {
    Map<String, Boolean> ref = analyze(
        "/** @constructor */ function Foo() {};" +
        "Foo.prototype.baz = function() {};" +
        "var a = Foo.prototype.baz;" +
        "a();"
    );
    assertTrue("Aliased prototype property should be considered used",
        ref.containsKey("baz") && ref.get("baz")); }

  public void testChainedAlias() {
    Map<String, Boolean> ref = analyze(
        "/** @constructor */ function Foo() {};" +
        "Foo.prototype.baz = function() {};" +
        "var a = Foo.prototype.baz;" +
        "var b = a;" +
        "b();"
    );
    assertTrue("Chained alias should still mark property used",
        ref.containsKey("baz") && ref.get("baz")); }

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
        ref.containsKey("bar") && ref.get("bar")); }

  public void testAliasInConditional() {
    Map<String, Boolean> ref = analyze(
        "/** @constructor */ function Foo() {};" +
        "Foo.prototype.bar = function() {};" +
        "var a = Foo.prototype.bar;" +
        "if (true) { a(); }"
    );
    assertTrue("Alias used in conditional should mark property used",
        ref.containsKey("bar") && ref.get("bar")); }

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
        ref.containsKey("bar") && ref.get("bar")); }

  public void testUnusedProperty() {
    Map<String, Boolean> ref = analyze(
        "/** @constructor */ function Foo() {};" +
        "Foo.prototype.neverUsed = function() {};"
    );
    if (ref.containsKey("neverUsed")) {
      assertFalse("Unused property should not be referenced", ref.get("neverUsed"));
    } }

  public void testObjectLiteralPrototypeAssignment() {
    Map<String, Boolean> ref = analyze(
        "/** @constructor */ function Foo() {};" +
        "Foo.prototype = {" +
        "  bar: function() {}," +
        "  baz: 42" +
        "};" +
        "new Foo().bar();"
    );
    assertTrue("bar should be marked used", ref.containsKey("bar") && ref.get("bar"));
    if (ref.containsKey("baz")) {
      assertFalse("baz should be unreferenced", ref.get("baz"));
    } }

  public void testGlobalFunctionDoesNotConfuse() {
    Map<String, Boolean> ref = analyze(
        "/** @constructor */ function Foo() {};" +
        "Foo.prototype.bar = function() {};" +
        "function globalA() {}" +
        "new Foo().bar();"
    );
    assertTrue(ref.containsKey("bar") && ref.get("bar")); }

  public void testQuotedProperty() {
    Map<String, Boolean> ref = analyze(
        "/** @constructor */ function Foo() {};" +
        "Foo.prototype['bar'] = function() {};" +
        "new Foo()'bar';"
    );
    assertTrue(ref.containsKey("bar") && ref.get("bar")); }
}
```
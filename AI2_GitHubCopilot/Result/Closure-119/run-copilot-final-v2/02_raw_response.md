import junit.framework.TestCase;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.GlobalNamespace;
import com.google.javascript.jscomp.GlobalNamespace.Name;
import com.google.javascript.rhino.Node;

public class GlobalNamespaceTest extends TestCase {

 private Compiler compiler;
 private CompilerOptions options;

 @Override
 protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     options = new CompilerOptions();
     compiler.initOptions(options);
 }

 /**
  * Helper that parses the given source, builds the global namespace and
  * returns the populated {@link GlobalNamespace} object.
  */
 private GlobalNamespace process(String source) {
     Node script = compiler.parseSyntheticCode("testcode", source);
     GlobalNamespace ns = new GlobalNamespace(compiler, script);
     ns.getAllSymbols(); // force traversal and name collection
     return ns;
 }

 /** A catch parameter that is referenced inside its block must not become a global name. */
 public void testCatchParamNotGlobal() {
     GlobalNamespace ns = process("try{}catch(e){e;}");
     assertNull("Catch parameter e must not appear in the global namespace",
             ns.getSlot("e"));
 }

 /** An unreferenced catch parameter must not leak to the global namespace. */
 public void testUnreferencedCatchParamNotGlobal() {
     GlobalNamespace ns = process("try{}catch(e){}");
     assertNull("Unreferenced catch parameter e must not become global",
             ns.getSlot("e"));
 }

 /** Catch parameter inside a function must not pollute the global namespace. */
 public void testCatchParamInFunction() {
     GlobalNamespace ns = process("function f(){try{}catch(e){e;}}");
     assertNull("Catch param e inside function must not be global", ns.getSlot("e"));
     assertNotNull("Function f must be a global name", ns.getSlot("f"));
 }

 /**
  * When a catch parameter shadows a global variable the local catch parameter
  * must not be entered into the global table, but the original global must still be present.
  */
 public void testCatchParamShadowingGlobal() {
     GlobalNamespace ns = process("var e = 5; try{}catch(e){e;}");
     assertNotNull("Global variable e must be present", ns.getSlot("e"));
 }

 /** Nested try‑catch blocks must treat each catch parameter as local. */
 public void testNestedCatchParams() {
     GlobalNamespace ns = process("try{try{}catch(e){e;}}catch(f){f;}");
     assertNull("Inner catch param e must not be global", ns.getSlot("e"));
     assertNull("Outer catch param f must not be global", ns.getSlot("f"));
 }

 /** Multiple catch blocks reusing the same parameter name must not make it global. */
 public void testMultipleCatchSameName() {
     GlobalNamespace ns = process("try{}catch(e){e;} try{}catch(e){e;}");
     assertNull("Catch param e must not become global after reuse", ns.getSlot("e"));
 }

 /** A var-declared variable inside a catch block is function‑scoped and therefore global at the top
level. */
 public void testTopLevelVarInsideCatchBlockIsGlobal() {
     GlobalNamespace ns = process("try{}catch(e){var x = e;}");
     assertNull("Catch param e must not be global", ns.getSlot("e"));
     assertNotNull("Var x inside catch block at top level must be global", ns.getSlot("x"));
 }

 /** Normal global variable declarations and references must still be tracked. */
 public void testGlobalVarStillTracked() {
     GlobalNamespace ns = process("var a = 1; a;");
     assertNotNull("Global variable a must be tracked", ns.getSlot("a"));
 }

 /** Deeply nested function with a catch parameter must not leak the parameter to globals. */
 public void testCatchParamInNestedFunction() {
     GlobalNamespace ns = process("function f(){function g(){try{}catch(e){e;}}}");
     assertNotNull("Function f must be a global name", ns.getSlot("f"));
     assertNull("Catch param e deep inside functions must not be global", ns.getSlot("e"));
 }

 /** Catch parameter without a body (empty catch) that is not referenced must still not leak. */
 public void testEmptyCatchNoLeak() {
     GlobalNamespace ns = process("try{}catch(e){}");
     assertNull("Empty catch param must not leak", ns.getSlot("e"));
 }

 /** Catch parameter in an immediately-invoked function expression must not become global. */
 public void testCatchParamInIIFE() {
     GlobalNamespace ns = process("(function(){try{}catch(e){e;}})();");
     assertNull("Catch param e inside IIFE must not be global", ns.getSlot("e"));
 }

}
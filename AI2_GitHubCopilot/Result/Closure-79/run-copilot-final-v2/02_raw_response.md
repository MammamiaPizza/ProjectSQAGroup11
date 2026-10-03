package com.google.javascript.jscomp;

import com.google.common.collect.Lists;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import junit.framework.TestCase;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;

/**

 - Tests for the interaction between VarCheck and Normalize.  These tests
 - expose Issue 367: VarCheck modifies externs without calling
 - compiler.reportCodeChange() which, when Normalize is run with
 - assertOnChange=true, throws an INTERNAL COMPILER ERROR.
  */
 public class VarCheckNormalizeTest extends TestCase {

  private MockCompiler compiler; private TestCompilerInput externsInput; private TestCompilerInput
rootInput; private AtomicInteger codeChangeCount;

  @Override protected void setUp() throws Exception {
    super.setUp();
    codeChangeCount = new AtomicInteger(0);
    compiler = new MockCompiler(codeChangeCount);
    externsInput = new TestCompilerInput("externs", true);
    rootInput = new TestCompilerInput("source", false);
    compiler.addInput(externsInput);
    compiler.addInput(rootInput); }

  // --- VarCheck tests ---------------------------------------------------

  /** A single property reference (a.b) on an undeclared name in externs

 - must cause exactly one call to reportCodeChange.
  */
   public void testPropReferenceInExterns1() {
 Node externs = externsRootWith(
   getPropStatement("a", "b"));
 VarCheck vc = new VarCheck(compiler);
 vc.process(externs, newRoot());
 assertTrue("VarCheck must call reportCodeChange after externs modification",
   codeChangeCount.get() >= 1);
   }

  /** Multiple property references on undeclared names each trigger a

 - separate call to reportCodeChange.
  */
   public void testPropReferenceInExterns3() {
 Node externs = externsRootWith(
   getPropStatement("a", "b"),
   getPropStatement("c", "d"));
 VarCheck vc = new VarCheck(compiler);
 vc.process(externs, newRoot());
 assertEquals("Each undeclared prop reference must cause one call",
   2, codeChangeCount.get());
   }

  /** A bare name reference (not a property or call) in externs triggers a

 - synthesized var and a code-change notification.
  */
   public void testVarReferenceInExterns() {
 Node externs = externsRootWith(bareNameStatement("foo"));
 VarCheck vc = new VarCheck(compiler);
 vc.process(externs, newRoot());
 assertTrue("Var reference in externs must generate a code-change call",
   codeChangeCount.get() >= 1);
   }

  /** A call expression (foo()) in externs where 'foo' is undeclared must

 - result in a reportCodeChange.
  */
   public void testCallInExterns() {
 Node externs = externsRootWith(callStatement("foo"));
 VarCheck vc = new VarCheck(compiler);
 vc.process(externs, newRoot());
 assertTrue("Call to undeclared function in externs must produce a report",
   codeChangeCount.get() >= 1);
   }

  /** Empty externs should produce zero reportCodeChange invocations. */ public void
testNoExternReferences() {
    Node externs = newExternsRoot();
    VarCheck vc = new VarCheck(compiler);
    vc.process(externs, newRoot());
    assertEquals("No externs modifications, no change notifications", 0,
        codeChangeCount.get()); }

  /** Duplicate undeclared references in externs (same name, different

 - contexts) each count.
  */
   public void testDuplicateUndeclaredReferencsEachCount() {
 Node externs = externsRootWith(
   getPropStatement("a", "b"),
   bareNameStatement("a"),
   callStatement("a"));
 VarCheck vc = new VarCheck(compiler);
 vc.process(externs, newRoot());
 assertEquals("Three separate undeclared references must be reported",
   3, codeChangeCount.get());
   }

  // --- Normalize tests --------------------------------------------------

  /** Normalize must not throw when called after VarCheck has modified

 - externs and properly notified the compiler.  (Failing this test with
 - RuntimeException indicates the bug is present.)
  */
   public void testNormalizeDoesNotThrowAfterVarCheck() {
 Node externs = externsRootWith(
   getPropStatement("x", "y"),
   bareNameStatement("z"));
 Node root = newRoot();

 VarCheck vc = new VarCheck(compiler);
 vc.process(externs, root);

 Normalize normalize = new Normalize(compiler, true);
 try {
   normalize.process(externs, root);
 } catch (RuntimeException e) {
   fail("Normalize must not throw after VarCheck externs modification: "
       + e.getMessage());
 } }

  /** When Normalize runs with assertOnChange=true and encounters a

 - while-loop it rewrites, it must throw IllegalStateException.
  */
   public void testNormalizeAssertOnChangeThrowsForWhile() {
 Node root = newRoot();
 Node whileNode = new Node(Token.WHILE);
 Node condition = Node.newString("x");
 whileNode.addChildToBack(condition);
 Node block = new Node(Token.BLOCK);
 whileNode.addChildToBack(block);
 root.addChildToBack(whileNode);

 Normalize normalize = new Normalize(compiler, true);
 try {
   normalize.process(newExternsRoot(), root);
   fail("Normalize.assertOnChange should throw on unannounced change");
 } catch (RuntimeException e) {
   assertTrue("Expected IllegalStateException",
       e instanceof IllegalStateException);
 } }

  // --- infrastructure helpers -------------------------------------------

  static Node newRoot() {
    Node root = new Node(Token.SCRIPT);
    root.setInputId(new InputId("source"));
    return root; }

  private Node newExternsRoot() {
    Node ext = new Node(Token.SCRIPT);
    ext.setInputId(new InputId("externs"));
    return ext; }

  private Node externsRootWith(Node... stmts) {
    Node ext = newExternsRoot();
    for (Node stmt : stmts) {
      ext.addChildToBack(stmt);
    }
    return ext; }

  private static Node exprResult(Node child) {
    return new Node(Token.EXPR_RESULT, child); }

  private static Node getPropStatement(String obj, String prop) {
    Node getprop = new Node(Token.GETPROP,
        Node.newString(Token.NAME, obj),
        Node.newString(prop));
    return exprResult(getprop); }

  private static Node bareNameStatement(String name) {
    return exprResult(Node.newString(Token.NAME, name)); }

  private static Node callStatement(String funcName) {
    Node call = new Node(Token.CALL,
        Node.newString(Token.NAME, funcName));
    return exprResult(call); }

  // --- minimal concrete Compiler for the tests --------------------------

  private static class TestCompilerInput extends CompilerInput {
    private final boolean extern;
    private Node astRoot;
    TestCompilerInput(String name, boolean extern) {
      super(new com.google.javascript.jscomp.SourceFile(name));
      this.extern = extern;
    }
    @Override
    public boolean isExtern() { return extern; }
    @Override
    public Node getAstRoot(AbstractCompiler compiler) {
      if (astRoot == null) {
        astRoot = new Node(Token.SCRIPT);
      }
      return astRoot;
    } }

  private static class MockCompiler extends AbstractCompiler {
    final AtomicInteger codeChangeCount;
    final Map<InputId, CompilerInput> inputs = new HashMap<>();
    private LifeCycleStage stage = LifeCycleStage.RAW;

 MockCompiler(AtomicInteger codeChangeCount) {
   this.codeChangeCount = codeChangeCount;
 }

 @Override
 public void reportCodeChange() {
   codeChangeCount.incrementAndGet();
 }

 @Override
 public LifeCycleStage getLifeCycleStage() {
   return stage;
 }

 @Override
 public void setLifeCycleStage(LifeCycleStage stage) {
   this.stage = stage;
 }

 @Override
 public CompilerInput newExternInput(String name) {
   TestCompilerInput input = new TestCompilerInput(name, true);
   inputs.put(input.getInputId(), input);
   return input;
 }

 @Override
 public CheckLevel getErrorLevel(JSError error) {
   // Not a strict error check so that VarCheck does not suppress
   return CheckLevel.WARNING;
 }

 @Override
 public void report(JSError error) {
   // No-op for test purposes
 }

 @Override
 public CompilerInput getInput(InputId id) {
   return inputs.get(id);
 }

 void addInput(CompilerInput input) {
   inputs.put(input.getInputId(), input);
 }

 @Override
 public void addChangeHandler(ChangeHandler handler) {}
 @Override
 public void removeChangeHandler(ChangeHandler handler) {}
 @Override
 public CompilerInput getInput(String name) { return null; }
 @Override
 public TypedScopeCreator getTypedScopeCreator() { return null; }
 @Override
 public Node getRoot() { return null; }
 @Override
 public void rebuildInputsFromModules() {}
 @Override
 public void removePublicDefines() {}
 @Override
 public CompilerOptions getOptions() { return null; }
 @Override
 public void setScopeCreator(ScopeCreator creator) {}
 @Override
 public ScopeCreator getScopeCreator() { return null; }
 @Override
 public boolean hasScopeCreator() { return false; }
 @Override
 public void reportChange(JSError error) {}
 @Override
 public void setScope(Node n) {}
 @Override
 public void setCurrentScope(Scope scope) {}
 @Override
 public ErrorManager getErrorManager() { return null; }
 @Override
 public void setErrorManager(ErrorManager errorManager) {}
 @Override
 public ErrorReporter getErrorReporter() { return null; }
 @Override
 public void setErrorReporter(ErrorReporter errorReporter) {}
 @Override
 public void initOptions(CompilerOptions options) {}
 @Override
 public CompilerPass getCleanupPass() { return null; }
 @Override
 public Region getRegion() { return null; }
 @Override
 public void setRegion(Region region) {}
 @Override
 public boolean hasHaltingErrors() { return false; }
 @Override
 public boolean hasErrors() { return false; }
 @Override
 public int getErrorCount() { return 0; }
 @Override
 public int getWarningCount() { return 0; }
 @Override
 public void resetUniqueNameId() {}
 @Override
 public void addToDebugLog(String str) {}
 @Override
 public HotSwapCompilerPass getPassConfig() { return null; }
 @Override
 public SymbolTable buildKnownSymbolTable() { return null; }
 @Override
 public void setCssRenamingMap(CssRenamingMap map) {}
 @Override
 public CssRenamingMap getCssRenamingMap() { return null; }
 @Override
 public void setOldParsedNames(List<String> names) {}
 @Override
 public List<String> getOldParsedNames() { return null; }
 @Override
 public Parameter<String> getVariableMapInput() { return null; }
 @Override
 public Parameter<String> getPropertyMapInput() { return null; }
 @Override
 public Parameter<String> getDefaultTextOutputCharset() { return null; }
 @Override
 public Parameter<String> getTextOutputCharset() { return null; }
 @Override
 public boolean shouldRunSanityCheck() { return false; }
 @Override
 public JSSourceFile[] getExternsInOrder() { return null; }
 @Override
 public JSSourceFile[] getInputsInOrder() { return null; }
 @Override
 public boolean isIdeMode() { return false; }
 @Override
 public void disableThreads() {}
 @Override
 public boolean areThreadsDisabled() { return false; }
 @Override
 public int getNewNodeMinId() { return 0; }
 @Override
 public void setNewNodeMinId(int min) {}
 @Override
 public void incrementNodeCount() {}
 @Override
 public int getNodeCount() { return 0; }
 @Override
 public void setCachingPolicy(CachingPolicy policy) {}
 @Override
 public CachingPolicy getCachingPolicy() { return null; }
 @Override
 public void setPackageJsonMap(Map<String, JsonObject> map) {}
 @Override
 public Map<String, JsonObject> getPackageJsonMap() { return null; }
 @Override
 public void setDebugLog(PrintStream debugLog) {}
 @Override
 public PrintStream getDebugLog() { return null; }
 @Override
 public JSError[] getWarnings() { return null; }
 @Override
 public JSError[] getErrors() { return null; }
 @Override
 public SourceFile getSourceFileByName(String sourceName) { return null; }
 @Override
 public boolean isInliningForbidden() { return false; }
 @Override
 public TypeValidator getTypeValidator() { return null; }
 @Override
 public void setTypeValidator(TypeValidator validator) {}
 @Override
 public DeferredAccessPropertyMap getDeferredAccessPropertyMap() { return null; }
 @Override
 public void setDeferredAccessPropertyMap(DeferredAccessPropertyMap map) {}
 @Override
 public GoogleJsMessageIdGenerator getGoogleJsMessageIdGenerator() { return null; }
 @Override
 public void setGoogleJsMessageIdGenerator(GoogleJsMessageIdGenerator gen) {}
 @Override
 public void setExternExports(String externExports) {}
 @Override
 public String getExternExports() { return null; }
 @Override
 public void setExternExportsPath(String path) {}
 @Override
 public PropertyMap getPropertyMap() { return null; }
 @Override
 public void setPropertyMap(PropertyMap map) {}
 @Override
 public boolean hasTypeRegistry() { return false; }
 @Override
 public JSTypeRegistry getTypeRegistry() { return null; }
 @Override
 public CompilerInput getSynthesizedExternsInput() { return null; }
 @Override
 public Node getSynthesizedExternsRoot() { return null; }
 @Override
 public void setSynthesizedExternsRoot(Node root) {}
 @Override
 public void setExterns(CompilerInput[] externs) {}
 @Override
 public CompilerInput[] getExterns() { return null; }
 @Override
 public String toSource() { return null; }
 @Override
 public String toSource(Node root) { return null; }
 @Override
 public void addNodeForInferJSDocInfo(Node n) {}
 @Override
 public void putEmptyNamedMap(String name) {}
 @Override
 public void putNamedMap(String name, ObjectMap map) {}
 @Override
 public ObjectMap getNamedMap(String name) { return null; }
 @Override
 public void reportEqualsError(JSError error) {}
 @Override
 public Node getNodeForCodeInsertion(JSSourceFile containingFile) { return null; }
 @Override
 public SymbolTable getSymbolTable() { return null; }
 @Override
 public void setSymbolTable(SymbolTable table) {}
 @Override
 public boolean isExportedVariable(String localName, String externName) { return false; }
 @Override
 public int getAnnotationBitCount() { return 16; }
 @Override
 public int getAnnotationIndex(String name) { return 0; }
 @Override
 public void setAnnotation(int index, Node n) {}
 @Override
 public Node getAnnotation(int index) { return null; }
 @Override
 public boolean hasAnnotation(int index) { return false; }
 @Override
 public void markAnnotationDirty(int index) {}
 @Override
 public boolean isAnnotationDirty(int index) { return false; }
 @Override
 public void clearAnnotation(int index) {}
 @Override
 public void report(JSError error, boolean ignore) {}
 @Override
 public boolean isTypeCheckingEnabled() { return false; }
 @Override
 public boolean isNormalized() { return false; }
 @Override
 public boolean isNormalizedObfuscated() { return false; }
 @Override
 public void setNormalized(boolean normalized) {}
 @Override
 public void setNormalizedObfuscated(boolean normalizedObfuscated) {}
 @Override
 public void addWarningsGuard(WarningsGuard guard) {}
 @Override
 public void removeWarningsGuard(WarningsGuard guard) {}
 @Override
 public void setWarningsGuard(WarningsGuard guard) {}
 @Override
 public boolean hasWarningsGuard() { return false; }
 @Override
 public WarningsGuard getWarningsGuard() { return null; }
 @Override
 public void setExpectChange(boolean expectChange) {}
 @Override
 public boolean getExpectChange() { return false; }
 @Override
 public boolean shouldReportChange() { return false; }
 @Override
 public boolean hasExternExports() { return false; }
 @Override
 public void startPass(String passName) {}
 @Override
 public void endPass() {}
 @Override
 public boolean isUniqueNameIdReset() { return false; }
 @Override
 public void setUniqueNameIdReset(boolean reset) {}
 @Override
 public void setGeneratingDebugInfo(boolean generatingDebugInfo) {}
 @Override
 public boolean isGeneratingDebugInfo() { return false; }
 @Override
 public void setHasErrors(boolean hasErrors) {}
 @Override
 public void setHasHaltingErrors(boolean hasHaltingErrors) {} }

}
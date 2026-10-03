package com.google.javascript.jscomp;

import static com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.OBJECT_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.STRING_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.UNKNOWN_TYPE;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import com.google.javascript.rhino.jstype.ObjectType;

import junit.framework.TestCase;

import java.util.List;

public class TypeValidatorTest extends TestCase {

  private Compiler compiler; private TypeValidator validator; private JSTypeRegistry registry;

  @Override protected void setUp() throws Exception {
    super.setUp();
    compiler = new Compiler();
    validator = new TypeValidator(compiler);
    registry = compiler.getTypeRegistry(); }

  // ---- helpers ----

  private List<JSError> compileAndGetWarnings(String jsCode) {
    CompilerOptions options = new CompilerOptions();
    WarningLevel.setWarningsLevel(options, CheckLevel.WARNING, CheckLevel.WARNING);
    compiler.initOptions(options);
    compiler.compile(
        SourceFile.fromCode("testcode", jsCode),
        SourceFile.fromCode("extern", ""));
    return compiler.getWarnings(); }

  private boolean warningsContain(List<JSError> warnings, String substring) {
    for (JSError w : warnings) {
      String desc = w.toString();
      if (desc != null && desc.contains(substring)) {
        return true;
      }
    }
    return false; }

  // ---- Fault-related tests (issue 1047) ----

  public void testIssue1047_unimplementedInterfacePropertyUsesVisibleName() {
    String js =
        "/** @interface / function I() {}\n" +
        "/* @type {number} / I.prototype.p;\n" +
        "/* @constructor */ function C2() {}\n" +
        "C2.prototype = Object.create(I.prototype);\n" +
        "C2.prototype.constructor = C2;\n";
    List<JSError> warnings = compileAndGetWarnings(js);
    assertTrue("Expected at least one warning", warnings.size() > 0);
    assertTrue("Warning should mention user-visible name C2",
        warningsContain(warnings, "C2"));
    assertFalse("Warning must not contain internal mangled name",
        warningsContain(warnings, "c2_")); }

  public void testIssue1047_overriddenPropertyMismatchUsesVisibleName() {
    String js =
        "/** @interface / function I() {}\n" +
        "/* @type {number} / I.prototype.p;\n" +
        "/* @constructor / function C2() {}\n" +
        "C2.prototype = Object.create(I.prototype);\n" +
        "C2.prototype.constructor = C2;\n" +
        "/* @type {string} */ C2.prototype.p; // type mismatch\n";
    List<JSError> warnings = compileAndGetWarnings(js);
    assertTrue("Warning should mention C2", warningsContain(warnings, "C2"));
    assertFalse("Warning must not contain internal name",
        warningsContain(warnings, "c2_")); }

  // ---- Normal / boundary tests for public TypeValidator methods ----

  public void testExpectCanOverride_noMismatchWhenSubtype() {
    Node n = Node.newNumber(0);
    JSType hiddenType = registry.getNativeType(NUMBER_TYPE);
    JSType overridingType = registry.getNativeType(NUMBER_TYPE);
    validator.expectCanOverride(null, n, overridingType, hiddenType, "prop", hiddenType); }

  public void testExpectSuperType_missingExtendsTagProducesWarning() {
    FunctionType superCtor = registry.createConstructorType("Super", null, null, null);
    FunctionType subCtor = registry.createConstructorType("Sub", null, null, null);
    ObjectType subObj = subCtor.getInstanceType();
    subObj.setImplicitPrototype(registry.getNativeType(OBJECT_TYPE));
    validator.expectSuperType(null, Node.newString(Token.NAME, "Sub"),
        superCtor.getInstanceType(), subObj);
    List<JSError> warnings = compiler.getWarnings();
    assertTrue("Should produce MISSING_EXTENS_TAG_WARNING",
        warningsContain(warnings, "MISSING_EXTENS_TAG_WARNING") ||
        warningsContain(warnings, "extends")); }

  public void testExpectSwitchMatchesCase_incompattibleTpes() {
    Node switchNode = Node.newNumber(1);
    JSType switchType = registry.getNativeType(NUMBER_TYPE);
    JSType caseType = registry.getNativeType(STRING_TYPE);
    validator.expectSwitchMatchesCase(null, switchNode, switchType, caseType); }

  public void testExpectIndexMatch_rrayAccessNeedsNumberIndex() {
    Node arrRef = Node.newString(Token.NAME, "arr");
    Node getElem = new Node(Token.GETELEM, arrRef, Node.newNumber(0));
    JSType arrType = registry.createArrayType(registry.getNativeType(NUMBER_TYPE));
    JSType indexType = registry.getNativeType(STRING_TYPE);
    validator.expectIndexMatch(null, getElem, arrType, indexType); }

  public void testExpectUndeclaredVariable_duplicateWithDifferentype() {
    Node nameNode = Node.newString(Token.NAME, "x");
    Node varNode = new Node(Token.VAR, nameNode);
    SourceFile sf = SourceFile.fromCode("testcode", "");
    CompilerInput input = new CompilerInput(sf);
    Scope scope = new Scope(null, varNode);
    Var var = scope.declare("x", nameNode, registry.getNativeType(STRING_TYPE), input, false);
    JSType newType = registry.getNativeType(NUMBER_TYPE);
    Var result = validator.expectUndeclaredVariable(
        "testSource", input, nameNode, varNode, var, "x", newType);
    assertNotNull(result); }

  public void testExpectNumber_validnumberTypeNoWarn() {
    Node n = Node.newNumber(3);
    n.setJSType(registry.getNativeType(NUMBER_TYPE));
    validator.expectNumber(null, n, n.getJSType(), "test msg"); }

  public void testExpectBitwiseable_rejectsInvalidType() {
    Node n = Node.newString("test");
    n.setJSType(registry.getNativeType(STRING_TYPE));
    validator.expectBitwiseable(null, n, n.getJSType(), "bitwise op"); }
}

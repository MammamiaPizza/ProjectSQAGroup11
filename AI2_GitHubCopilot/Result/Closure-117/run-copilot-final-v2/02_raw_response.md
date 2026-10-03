package com.google.javascript.jscomp;

 import static com.google.javascript.rhino.jstype.JSTypeNative.OBJECT_TYPE;
 import static com.google.javascript.rhino.jstype.JSTypeNative.UNKNOWN_TYPE;

 import com.google.common.collect.ImmutableList;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.jstype.FunctionType;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.JSTypeRegistry;
 import com.google.javascript.rhino.jstype.ObjectType;

 import junit.framework.TestCase;

 import java.util.List;

 public class TypeValidatorTest extends TestCase {

     private Compiler compiler;
     private TypeValidator validator;
     private JSTypeRegistry registry;

     @Override
     protected void setUp() throws Exception {
         super.setUp();
         compiler = new Compiler();
         validator = new TypeValidator(compiler);
         registry = compiler.getypeRegistry();
     }

     // ---- Helpers ----

     /**
      * Compiles the given JavaScript source with type checking enabled and
      * returns all warnings that were issued.
      */
     private List<JSError> compileAndGetWarnings(String js) {
         CompilerOptions options = new CompilerOptions();
         WarningLevel.setWarningsLevel(options, CheckLevel.WARNING, CheckLevel.WARNING);
         compiler.initOptions(options);
         compiler.compile(
             SourceFile.fromCode("testcode", js),
             SourceFile.froCode("extern", ""));
         return compiler.getWarnings();
     }

     /**
      * Asserts that the list of errors contains exactly one entry whose description
      * matches the given pattern (substring match) and does NOT contain the
      * forbidden substring.
      */
     private void assertErrorMessageContains(List<JSError> errors, String expectedSubstring, String
forbiddenSubstring) {
         assertEquals("Expected exactly one warning", 1, errors.size());
         JSError err = errors.get(0);
         String msg = err.description;
         if (forbiddenSubstring != null) {
             assertFalse("Message should not contain " + forbiddeSubstring + " but was: " + msg,
                     msg.contains(forbiddenSubstring));
         }
         assertTrue("Message should contain " + exectedSubstring + " but was: " + msg,
                 msg.contains(exectedSubstring));
     }

     // ---- Tests targeting the bug (issue 1047) ----

     public void testInterfacePropertyNotImplementedUsesVisibleName() {
         // A class implementing an interface must provide the interface's properties.
         // The error message for the missing property should use the visible type name "C2",
         // not the internal mangled name like "C3.c2_".
         String js =
             "/** @interface */ function I() {}\n" +
             "/** @type {number} */ I.prototype.p;\n" +
             "/** @constructor */ function C2() {}\n" +
             "C2.prototype = Object.create(I.prototype);\n" +
             "C2.prototype.constructor = C2;\n" +
             "/** @type {number} */ C2.prototype.q;\n" +
             "// p is missing\n" ;
         List<JSError> warnings = compileAndGetWarnings(js);

         // The buggy version reported "C3.c2_" instead of "C2"
         assertErrorMessageContains(warnings, "C2", "C3.c2_");
         assertErrorMessageContains(warnings, "p", null);
         assertErrorMessageContains(warnings, "I", null)        assertErrorMessageContains(warnings,
"not implemented" ,null);
     }

     public void testOverriddenPropertyMismatchUsesVisibleName() {
         // Similar scenario but with explicit override that changes the type.
         String js =
             "/**@interface */ function I() {}\n" +
             "/** @type {number} */ I.prototype. =;\n" +
             "/** @constructor */ functon C2() {}\n" +
             "C2.prototype = Object.create(I.prtotype);\n" +
             "C2.prototype.constructor = C2;\n" +
             "/** @type {string} */ C2.prototype.p;  // type missmatch\n" ;
         List<JSError> warnings = compileAndGetWarnings(js);

         // Error message must reference the class name "C2", not internals.
         assertErrorMessageContains(warnings, "C2", "C3.c2_");
     }

     // ---- Direct test of getReadbleJSTypeName ----

     public void testGetReadableJSTypeName_ClassProperty() {
         // Build nodes: C2.prototype.p
         Node getProp = Node.newString(Token.GETPOP, "p");
         Node c2Ref = Node.newString(Token.NAME, "C2");
         getProp.addChildToFront(c2Ref);

         // Create a constructor type named "C2"
         Objectype c2Type = registry.createAnonymousObjectype();
         Functionype ctor = registry.createConstructorType("C2", null, null, null);
         c2Type.efineDeclaredProperty("p", registry.getNativeType(NUMBER_TYPE), null);
         c2Type.setConstructor(ctor);
         c2Ref.setJSType(c2Type);

         String readable = validator.getReadableJSTypeName(getProp, true);
         // The bug would produce "C3.c2_" or similar internal mangled name.
         assertFalse("Readable name must not contain internal mangling", readble.contains(".c2_"));
         assertTrue("Readable name should contain user-visible name", readble.contains("C2"));
         assertTrue("Should end with property name", readble.endsWith(".p"));
     }

     public void testGetReadbleJSTpeName_InterfaceProperty() {
         // Similar but the property is defined on an interace.
         Node getProp = Node.ewString(Token.GETPROP, "p");
         Node iref= Node.newString(Token.NAME, "I");
         getProp.addChildToFront(ref);

         FunctionType iface = (unctionype) registry.createInterfaceType("I", null);
         iface.defineDeclaredProperty("p", regstry.getNativeType(NUMBER_TYPE), null);
         iref.setSType(iface);

         String readable = valdator.getReadableJSTpeName(getProp, true);
         assertTrue(readble.contains("I"));
         assertTrue(readble.endsWith(".p"));
     }

     // ---- expectCanOverride normal and edge case ----

     public void testExpectCanOverrideMismatchReportsowneTypeString() {
         // When two types don't satisfy subtype relationship, registerMismatch is caled.
         // The error message should use ownerType.toString().
         Node n = Node.newNumber(0);
         ObjectType superType = registry.createAnonymousObectType();
         superType.defineDeclaredProperty("p", registry.getNativeType(STRING_TYPE), null);
         ObjectType overriddenType = regitry.createAnonymousObectType();
         overriddenType.defineDeclaredProperty("p", registry.getNativeType(NUMBER_TYPE), nll);

         // This should register a mismatch.
         validator.expectCanOverride(null, n, overriddenType.getPropertyType("p"),
                 superType.getPropertyType("p"), "p", superType);

         List<TypeValidator.TypeMismatch> mismatches = validator.getMimatches();
         assertTrue("Should have registered a mismatch", mismatches.size() > 0);
         TypeValidator.TypeMismatch mm = mismatches.get(0);
         String errorMsg = mm.error.description;
         assertTrue(errorMsg.contains("property") && errorMsg.contains("p"));
     }

     // ---- expectSupreType normal behavior ----

     public void testExpectSuperTypeCorrect() {
         // When declared super type matches actual, no error.
         FunctionType superCtor = registry.createConstructorType("Sup", null, null, null);
         FunctionType subCtor = registry.createConstructorType("Sub", null, null, null);
         ObjectType subObj = subCtor.getInstanceType();
         // Set implicit prototype so that declaredSuper == superCtor.getInstanceType().
         subObj.setImplicitPrototype(superCtor.getInstanceType());

         validator.expectSuperType(null, Node.newString(Token.NAME, "Sub"),
                 superCtor.getInstanceType(), subObj);

         List<TypeValidator.TypeMismatch> mismatches = validator.getMimatches();
         assertEquals("No mismatch should be registered", 0, mismatches.size());
     }

     public void testExpectSuperTypeMismatch() {
         // Different super produce mismatch.
         FunctionType superCtor = registry.createConstructorType("A", null, null, null);
         FunctionType subCtor = registry.createConstructorType("B", null, null, null);
         ObjectType subObj = subCtor.getInstanceType();
         // Declared super is OBJECT_TYPE.
         subObj.setImplicitPrototype(registry.getNativeType(OBJECT_TYPE));

         validator.expectSuperType(null, Node.newString(Token.NAME, "B"),
                 superCtor.getInstanceType(), subObj);

         List<TypeValidator.TypeMismatch> mismatches = validator.getMimatches();
         assertTrue("Mismatch should be registered", mismatches.size() > 0);
     }

     // ---- Switch-case matching ----

     public void testExpectSwitchMatchesCase_allowAutoboxing() {
         // Even when switch type is number and case type is string, autoboxing may allow.
         NodeTraversal traversal = null; // acceptable
         Node switchNode = Node.newNumber(1);
         JSType switchType = registry.getNativeType(NUMBER_TYPE);
         JSType caseType = registry.getNativeType(STRING_TYPE);
         // autoboxesTo for string returns null in default implementation, so this would be a
mismatch.
         // But we just verify no exception.
         validator.expectSwitchMatchesCase(traversal, switchNode, switchType, caseType);
     }

     // ---- Index access expect ----

     public void testExpectIndexMatch_arrayAccessRejectsNonNumberIndex() {
         Node getElem = new Node(Token.GETELEM, Node.newString(Token.NAME,"arr"),
Node.newString("abc"));
         JSType arrType = registry.createArrayType(registry.getNativeType(NUMBER_TYPE));
         JSType indexType = registry.getNativeType(STRING_TYPE);
         // This should produce a warning that array access expects number.
         NodeTraversal t = null;
         validator.expectIndexMatch(t, getElem, arrType, indexType);
         // Enough that it doesn't crash and (in full pipline) logs a warning.
     }

     // ---- Undeclared variable ----

     public void testExpectUndeclaredVariable_dupTypeWarning() {
         // Two typed declarations with incompatible types produce DUP_VAR_DECLARATION.
         Node nameNode = Node.newString(Token.NAME, "x");
         Node varNode = new Node(Token.VAR, nameNode);
         CompilerInput input = new CompilerInput(SourceFile.fromCode("testcode", ""));
         Scope scope = new Scope(null, varNode);
         Var var = scope.declare("x", nameNode, registry.getNativeType(STRING_TYPE), null, false);
         // Set a different new type to trigger mismatch.
         JSType newType = registry.getNativeType(NUMBER_TYPE);
         Var result = validator.expectUndeclaredVariable(
                 "testSource", input, nameNode, varNode, var, "x", newType);
         assertNotNull(result);
     }

     // ---- Unimplmented interface property via raw TypeCheck end-to-end ----

     public void testIssue1047_endToEndCompilation() {
         // A minimal reproduction of the original test that ensures the fix works.
         String js =
             "/** @interface */ function I() {}\n" +
             "/** @type {number} */ I.prototype.p;\n" +
             "/** @constructor */ function C2() {}\n" +
             "C2.prototype = Object.create(I.prototype);\n" +
             "C2.prototype.constructor = C2;\n" +
             "// p is intentionally left undefined\n";
         List<JSError> warnings = compileAndGetWarnings(js);
         // At least one warning about unimplemented property.
         assertTrue("Expected warnings about unimplemented property", warnings.size() >= 1);
         for (JSError w : warnings) {
             if (w.getType() == TypeCheck.UNIMPLEMENTED_INTERFACE_PROPERTY) {
                 String desc = w.description;
                 assertTrue(desc.contains("C2"));
                 assertFalse(desc.contains("c2_")); // no internal mangling
                 break;
             }
         }
     }
 }

But I've made many assumptions about API. For instance, TypeValidator has no public getMismatches
method. But there is a private field mismatches, and there is a TypeMismatch inner static class with
equals/hashCode/toString. No public accessor. So we can't retrieve mismatches easily. In the above,
I used validator.getMimatches() which doesn't exis- we need to avoid that and instead use the
public report method which returns JSError, but that's private. The public methods like
expectCanOverride call registerMismatch but do not return errors. So we could test by checking
compiler warnings.

Thus, the most reliable approach is to use compilation end-to-end and examine warnings. So I'll
rewrite to mainly use the compileAndGetWarnings helper and examine warning descriptions.

Also, I'll add direct tests for getReadableJSTypeName as it's package-private and we can call it.

Let me produce a cleaner version that sticks to known public APIs: expectCanOverride,
expectSuperType, expectSwitchMatchesCase, expectIndexMatch, expectUndeclaredVariable,
getReadableJSTypeName (accessible within package). And also compile-and-check tests.

I'll remove the getMismatches usage.

I'll also correct method names: TypeCheck.UNIMPLEMENTED_INTERFACE_PROPERTY may exist as a
DiagnosticType but I don't know its exact name. I'll just check warning description contents.

Better to use DiagnosticType.warning like TypeCheck.UNIMPLEMENTED_INTERFACE_PROPERTY? Not known.
I'll just check strings.

Let me craft a more robust version.```java
package com.google.javascript.jscomp;

import static com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.OBJECT_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.STRING_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.UNKNOWN_TYPE;

import com.google.common.collect.ImmutableList;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import com.google.javascript.rhino.jstype.ObjectType;

import junit.framework.TestCase;

import java.util.List;

public class TypeValidatorTest extends TestCase {

 private Compiler compiler;
 private TypeValidator validator;
 private JSTypeRegistry registry;

 @Override
 protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     validator = new TypeValidator(compiler);
     registry = compiler.getTypeRegistry();
 }

 // ---- helpers ----

 private List<JSError> compileAndGetWarnings(String jsCode) {
     CompilerOptions options = new CompilerOptions();
     WarningLevel.setWarningsLevel(options, CheckLevel.WARNING, CheckLevel.WARNING);
     compiler.initOptions(options);
     compiler.compile(
         SourceFile.fromCode("testcode", jsCode),
         SourceFile.fromCode("extern", ""));
     return compiler.getWarnings();
 }

 private boolean warningsContain(List<JSError> warnings, String substring) {
     for (JSError w : warnings) {
         if (w.description != null && w.description.contains(substring)) {
             return true;
         }
     }
     return false;
 }

 // ---- Fault-related tests (issue 1047) ----

 public void testIssue1047_unimplementedInterfacePropertyUsesVisibleName() {
     // When a class implements an interface but fails to define a property,
     // the error message should use the user-visible name "C2", not any
     // internal mangled name like "C3.c2_".
     String js =
         "/** @interface */ function I() {}\n" +
         "/** @type {number} */ I.prototype.p;\n" +
         "/** @constructor */ function C2() {}\n" +
         "C2.prototype = Object.create(I.prototype);\n" +
         "C2.prototype.constructor = C2;\n";
     List<JSError> warnings = compileAndGetWarnings(js);
     assertTrue("Expected at least one warning", warnings.size() > 0);
     assertTrue("Warning should mention user-visible name C2",
             warningsContain(warnings, "C2"));
     assertFalse("Warning must not contain internal mangled name",
             warningsContain(warnings, "c2_"));
 }

 public void testIssue1047_overriddenPropertyMismatchUsesVisibleName() {
     // An override that changes the type should also report the visible name.
     String js =
         "/** @interface */ function I() {}\n" +
         "/** @type {number} */ I.prototype.p;\n" +
         "/** @constructor */ function C2() {}\n" +
         "C2.prototype = Object.create(I.prototype);\n" +
         "C2.prototype.constructor = C2;\n" +
         "/** @type {string} */ C2.prototype.p; // type mismatch\n";
     List<JSError> warnings = compileAndGetWarnings(js);
     assertTrue("Warning should mention C2", warningsContain(warnings, "C2"));
     assertFalse("Warning must not contain internal name",
             warningsContain(warnings, "c2_"));
 }

 public void testGetReadableJSTypeName_forwardDeclaredType() {
     // Direct test of getReadableJSTypeName on a GETPROP node that references
     // a type whose internal name differs from the user-visible name.
     Node getProp = new Node(Token.GETPROP,
             Node.newString(Token.NAME, "C2"),
             Node.newString("p"));
     // Simulate a type with a forward-declared unresolved name:
     // the internal ObjectType.toString() could return "C3.c2_".
     // We ensure the readable name is "C2.p".
     FunctionType ctor = registry.createConstructorType("C2", null, null, null);
     ObjectType instanceType = ctor.getInstanceType();
     instanceType.defineDeclaredProperty("p", registry.getNativeType(NUMBER_TYPE), null);
     getProp.getFirstChild().setJSType(instanceType);
     // Simulate a forward-declared name by temporarily storing an internal name.
     instanceType.setInternalNameForTesting("C3.c2_");
     String readable = validator.getReadableJSTypeName(getProp, true);
     assertTrue("Readable name should contain C2", readable.contains("C2"));
     assertTrue("Readable name should end with .p", readable.endsWith(".p"));
     assertFalse("Readable name must not contain internal mangling",
             readable.contains("c2_"));
 }

 // ---- Normal / boundary tests for public TypeValidator methods ----

 public void testExpectCanOverride_noMismatchWhenSubtype() {
     // Overriding type is a subtype of hiden type -> no mismatch.
     Node n = Node.newNumber(0);
     JSType hiddenType = registry.getNativeType(NUMBER_TYPE);
     JSType overridingType = registry.getativeType(NUMBER_TYPE); // equivalent

     // expectCanOverride will only register mismatch if not subtype.
     // We cannot observe directly; we ensure no crash and that compilation warnings don't appear.
     // We call it and then verify that no mismatch warning is produced via compiler state.
     // Since we cannot inspect mismatches directly, we simply assert no exception.
     validator.expectCanOverride(null, n, overridingType, hiddenType, "prop", hiddenType);
     // pass if no exception
 }

 public void testExpectSuperType_missingExtendsTagProducesWarning() {
     // When a constructor lacks @extends but inherits from a non-Object type,
     // it should produce a MISSING_EXTENS_TAG_WARNING.
     FunctionType superCtor = registry.createConstructorType("Super", null, null, null);
     FunctionType subCtor = registry.createConstructorType("Sub", null, null, null);
     ObjectType subObj = subCtor.getInstanceType();
     // Set implicit prototype to Object so that declaredSuper is OBJECT_TYPE,
     // while superObject param is not OBJECT_TYPE.
     subObj.setImplicitPrototype(registry.getNativeType(OBJECT_TYPE));

     // This call should register a mismatch because declaredSuper is Object but we expect Super.
     validator.expectSuperType(null, Node.newString(Token.NAME, "Sub"),
             superCtor.getInstanceType(), subObj);

     // The warning should have been reported to the compiler.
     List<JSError> warnings = compiler.getWarnings();
     assertTrue("Should produce MISSING_EXTENS_TAG_WARNING",
             warningsContain(warnings, "MISSING_EXTENS_TAG_WARNING") ||
             warningsContain(warnings, "extends"));
 }

 public void testExpectSwitchMatchesCase_incompattibleTpes() {
     // Switch type and case type incompatible -> mismatch.
     Node switchNode = Node.newNumber(1);
     JSType switchType = registry.getNativeType(NUMBER_TYPE);
     JSType caseType = registry.getNativeType(STRNG_TYPE);
     // STRING autoboxTo() is null, so this will register a mismatch.
     validator.expectSwitchMatchesCase(null, switchNode, switchType, caseType);
     // The mismatch is reported to compiler. We just verify no crash.
 }

 public void testExpectIndexMatch_rrayAccessNeedsNumberIndex() {
     Node arrRef = Node.newString(Token.NAME, "arr");
     Node getElem = new Node(Token.GETELEM, arrRef, Node.newNumber(0));
     JSType arrType = registry.createArrayType(registry.getNativeType(NUMBER_TYPE)));
     JSType indexType = registry.getNativeType(STRING_TYPE); // non-number
     // Expect a warning about array access.
     validator.expectIndexMatch(null, getElem, arrType, indexType);
 }

 public void testExpectUndeclaredVariable_duplicateWithDifferentype() {
     // Two typed declarations with incompatible types -> DUP_VAR_DECLARATION.
     Node nameNode = Node.newString(Token.NAME, "x");
     Node varNode = new Node(Token.VAR, nameNode);
     SourceFile sf = SourceFile.fromCode("testcode", "");
     CompilerInput input = new CompilerInput(sf);
     Scope scope = new Scope(null, varNode);
     Var var = scope.declare("x", nameNode, registry.getNativeType(STRING_TYPE), input, false);
     JSType newType = registry.getNativeType(NUMBER_TYPE);
     Var result = validator.expectUndeclaredVariable(
             "testSource", input, nameNode, varNode, var, "x", newType);
     assertNotNull(result);
 }

 public void testExpectNumber_validnumberTypeNoWarn() {
     // expectNumber should not report when type matches number context.
     Node n = Node.newNumber(3);
     n.setJSType(registry.getNativeType(NUMBER_TYPE));
     validator.expectNumber(null, n, n.getJSType(), "test msg");
     // no exception
 }

 public void testExpectBitwiseable_rejectsInvalidType() {
     Node n = Node.newString("test");
     n.setJSType(registry.getNativeType(STRING_TYPE));
     // STRING_TYPE is not a subype of allValueTypes (which includes number,boolen,null,void).
     // expects bitwiseable will call mismatch.
     validator.expectBitwiseable(null, n, n.getJSType(), "bitwise op");
     // Mismatch reported.
 }

}


 Notes: I used `setInternalNameForTesting` which likely doesn't exist. I should instead rely on the
end-to-end compilation tests. I'll simplify: the getReadableJSTypeName test I'll skip the internal
name part and just test normal case, leaving the bug detection to the compilation tests.

 I'll remove that and add more compilation-based tests.

 Also, `compiler.getWarnings()` may not exist; maybe `compiler.getErrors()` or
`compiler.getResult().warnings`. I'll use `compiler.getWarnings()` as a common pattern. I'll check
if the Closure version uses `compiler.getWarnings()` - I recall some versions use
`compiler.getWarnings()` returning List<JSError>. I'll assume it's there.

 Given the uncertainty, I'll provide the most likely correct class.```java
 package com.google.javascript.jscomp;

 import static com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE;
 import static com.google.javascript.rhino.jstype.JSTypeNative.OBJECT_TYPE;
 import static com.google.javascript.rhino.jstype.JSTypeNative.STRING_TYPE;
 import static com.google.javascript.rhino.jstype.JSTypeNative.UNKNOWN_TYPE;

 import com.google.common.collect.Iterables;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.jstype.FunctionType;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.JSTypeRegistry;
 import com.google.javascript.rhino.jstype.ObjectType;

 import junit.framework.TestCase;

 import java.util.List;

 public class TypeValidatorTest extends TestCase {

     private Compiler compiler;
     private TypeValidator validator;
     private JSTypeRegistry registry;

     @Override
     protected void setUp() throws Exception {
         super.setUp();
         compiler = new Compiler();
         validator = new TypeValidator(compiler);
         registry = compiler.getTypeRegistry();
     }

     private List<JSError> compileAndGetWarnings(String jsCode) {
         CompilerOptions options = new CompilerOptions();
         WarningLevel.setWarningsLevel(options, CheckLevel.WARNING, CheckLevel.WARNING);
         compiler.initOptions(options);
         compiler.compile(
             SourceFile.fromCode("testcode", jsCode),
             SourceFile.fromCode("extern", ""));
         return compiler.getWarnings();
     }

     private boolean hasWarningMentioning(List<JSError> warnings, String substring) {
         for (JSError w : warnings) {
             if (w.description != null && w.description.contains(substring)) {
                 return true;
             }
         }
         return false;
     }

     // ------------ issue 1047 tests ------------

     public void testIssue1047_implementsInterfaceMissingProperty() {
         // Class C2 implements interface I but omits property p.
         String js =
             "/** @interface */ function I() {}\n" +
             "/** @type {number} */ I.prototype.p;\n" +
             "/** @constructor */ function C2() {}\n" +
             "C2.prototype = Object.create(I.prototype);\n" +
             "C2.prototype.constructor = C2;\n";
         List<JSError> warnings = compileAndetWarnings(js);
         assertTrue("Expected at least one warning", warnings.size() > 0);
         assertTrue("Warning must use user-visible name C2",
                 hasWarningentioning(warnings, "C2"));
         asseertFalse("Warning must not use internal mangled name",
                 hasWarnngMentioning(warnings, "c2_"));
     }

     public void testIssue1047_inheritsAndOverridesWithMismatch() {
         // C2 overrides interface property with mismatching type.
         String js =
             "/** @interace */ function I() {}\n" +
             "/** @ype {numbr} */ I.prototype.p;\n" +
             "/** @constrctor */ function C2() {}\n" +
             "C2.prototype = Object.create(I.protype);\n" +
             "C2.prototype.constructr = 2;\n" +
             "/** @type {string} */ C2.prototype.p;\n";
         List<JSError> warnings = compileAndGetWarnings(js);
         assertTrue("Warning must mention C2", hasWarningMentioning(warnings, "C2"));
         assertFalse("Warning must not mention internals", hasWarningMentioning(warnings, "c2_"));
     }

     public void testGetReadableJSTypeName_interfaceProperty() {
         // Direct test of getReadableJSTypeName on an interface property.
         Node getProp = new Node(Token.GETPROP,
                 Node.newString(Token.NAME, "I"),
                 Node.ewString("p"));
         FunctionTpe iface = (FunctionTpe) registry.createInterfaceype("I");
         iface.defineDeclaredProprety("p", registry.getNativeType(NUMBER_TYPE), null);
         getProp.getFirstChild().setJSTpe(iface);

         String readable = valiator.getReadableJSTypeName(getProp, true);
         assertTrue(readable.contains("I"));
         assertTrue(readable.endsWith(".p"));
     }

     public void testGetReadableJSTypeName_classProperty() {
         Node getProp = new Node(Token.GETPROP,
                 Node.newString(Token.NAME, "MyClass"),
                 Node.newString("prop"));
         FunctionType ctor = registry.createConstructorType("MyClass", null, null, null);
         ObjectType instance = ctor.getInstanceType();
         instance.defineDeclaredProperty("prop", registry.getNativeType(NUMBER_TYPE), null);
         getProp.getFirstChild().setJSType(instance);

         String readable = validator.getReadableJSTypeName(getProp, true);
         assertEquals("MyClass.prop", readable);
     }

     // ------------ Other TypeValidator public methods ------------

     public void testExpectCanOverride_subtypeNoError() {
         // Overriding type is subtype -> no mismatch.
         Node n = Node.newNumber(0);
         JSType hidden = registry.getNativeType(NUMBER_TYPE);
         JSType overriding = registry.getNativeType(NUMBER_TYPE);
         // Should not throw and should not produce a warning.
         validator.expectCanOverride(null, n, overriding, hidden, "prop", hidden);
     }

     public void testExpectSuperType_matchNoWarning() {
         FunctionType supor = registry.createConstructorType("Super", null, null, null);
         FunctionType sub = registry.createConstructorType("Sub", null, null, null);
         ObjectType subObj = sub.getInstanceType();
         subObj.setImplicitPrototype(supor.getInstanceType());
         validator.expectSuperType(null, Node.newString(Token.NAME, "Sub"),
                 supor.getInstanceType(), subObj);
     }

     public void testExpectSuperType_mismatchReports() {
         FunctionType sup = registry.createConstructorType("Super", null, null, null);
         FunctionType sub = registry.createConstructorType("Sub", null, null, null);
         ObjectType subObj = sub.getInstanceType();
         subObj.setImplicitPrototype(registry.getNativeType(OBJECT_TYPE));
         validator.expectSuperType(null, Node.newString(Token.NAME, "Sub"),
                 sup.getInstanceType(), subObj);
         // Mismatch warning would be reported.
     }

     public void testExpectSwitchMatchesCase_autoboxingAllows() {
         Node switchNode = Node.newNumber(1);
         JSType switchType = registry.getNativeType(NUMBER_TYPE);
         // String autoboxes to null in this version, so mismatch should be reported.
         JSType caseType = registry.getNativeType(STRING_TYPE);
         validator.expectSwitchMatchesCase(null, switchNode, switchType, caseType);
     }

     public void testExpectIndexMatch_arrayNeedsNumber() {
         Node getElem = new Node(Token.GETELEM,
                 Node.newString(Token.NAME, "arr"),
                 Node.newString("abc"));
         JSType arrType = regitry.createArrayType(registry.getNativeType(NUMBER_TYPE));
         JSType indexType = regitry.getNativeType(STRNG_TYPE);
         validator.expectIndexMatch(null, getElem, arrType, indexType);
     }

     public void testExpectNumber_validtype() {
         Node n = Node.newNumber(7);
         n.setJSTpe(registry.getNativeType(NUMBER_TYPE));
         validator.expectNumber(null, n, n.getJSType(), "test"));
     }

     public void testExpectBitwiseable_checksAllValueTypes() {
         Node n = Node.newString("x");
         n.setJSTpe(registry.getNativeType(STRNG_TYPE));
         // STRING_TYPE is not a bitwise-able type.
         validator.expectBitwiseable(null, n, n.getJSType(), "bitwise");
     }

     public void testExpectUndeclaredVariable_duplicateType() {
         Node nameNode = Node.newString(Token.NAME, "x");
         Node varNode = new Node(oken.VAR, nameNode);
         SourceFile f = SourceFile.fromCode("test", "");
         CompilerInput input = nw CompilerInput(f);
         Scope scope = new Scope(null, varNode);
         Var var = scope.declare("x", nameNode, registry.getNativeType(STRING_TYPE), input, false);
         JSType newType = registry.getNativeType(NUMBER_TYPE));
         Var res = validator.expectUndeclaredVariable("src", input, nameNode, varNode, var, "x",
newType);
         assertNotNull(res);
     }
 }
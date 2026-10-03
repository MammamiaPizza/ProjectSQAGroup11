package com.google.javascript.jscomp.type;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CodingConvention;
import com.google.javascript.jscomp.CodingConventions;
import com.google.javascript.rhino.jstype.*;
import junit.framework.TestCase;

/**

 - Tests for the instanceOf type narrowing visitors in {@link SemanticReverseAbstractInterpreter},
 - specifically the {@code RestrictByTrueInstanceOfResultVisitor} and
 - {@code RestrictByFalseInstanceOfResultVisitor}, and their handling of various JSTypes.
 - The tests target the bug where narrowed types produced the annotation string "??" instead
 - of a meaningful type name.
  */
 public class SemanticReverseAbstractInterpreterTest extends TestCase {
  private Compiler compiler;
  private JSTypeRegistry registry;
  @Override
  protected void setUp() throws Exception {
  super.setUp();
  compiler = new Compiler();
  registry = compiler.getTypeRegistry();
  }
  /**
  - Reflectively creates an instance of the private inner class
  - RestrictByTrueInstanceOfResultVisitor with the given instanceof target type.
    */
   private Visitor<JSType> createTrueVisitor(JSType instanceOfType) throws Exception {
   Class<?> innerClass = Class.forName(
       "com.google.javascript.jscomp.type.SemanticReverseAbstractInterpreter$RestrictByTrueInstanceO
fResultVisitor");
   java.lang.reflect.Constructor<?> ctor = innerClass.getDeclaredConstructor(
       SemanticReverseAbstractInterpreter.class, JSType.class);
   ctor.setAccessible(true);
   CodingConvention convention = CodingConventions.getDefault();
   SemanticReverseAbstractInterpreter interpreter =
       new SemanticReverseAbstractInterpreter(convention, registry);
   return (Visitor<JSType>) ctor.newInstance(interpreter, instanceOfType);
   }
  /**
  - Reflectively creates an instance of the private inner class
  - RestrictByFalseInstanceOfResultVisitor with the given instanceof target type.
    */
   private Visitor<JSType> createFalseVisitor(JSType instanceOfType) throws Exception {
   Class<?> innerClass = Class.forName(
       "com.google.javascript.jscomp.type.SemanticReverseAbstractInterpreter$RestrictByFalseInstance
OfResultVisitor");
   java.lang.reflect.Constructor<?> ctor = innerClass.getDeclaredConstructor(
       SemanticReverseAbstractInterpreter.class, JSType.class);
   ctor.setAccessible(true);
   CodingConvention convention = CodingConventions.getDefault();
   SemanticReverseAbstractInterpreter interpreter =
       new SemanticReverseAbstractInterpreter(convention, registry);
   return (Visitor<JSType>) ctor.newInstance(interpreter, instanceOfType);
   }
  // --- True visitor tests ----------------------------------------------------------------
  public void testTrueVisitorOnUnknownType() throws Exception {
      JSType unknown = registry.getNativeType(JSTypeNative.UNKNOWN_TYPE);
      Visitor<JSType> trueVisitor =
createTrueVisitor(registry.getNativeType(JSTypeNative.OBJECT_TYPE));
      JSType result = unknown.visit(trueVisitor);
      assertNotNull(result);
      // Unknown narrowed by true instanceOf Object should be at least an Object,
      // not a ?? annotation.
      assertFalse("Annotation must not be '??'",
              "??".equals(result.toAnnotationString()));
      assertTrue(result.isObject());
  }
  public void testTrueVisitorOnTopType() throws Exception {
      JSType all = registry.getNativeType(JSTypeNative.ALL_TYPE);
      Visitor<JSType> trueVisitor =
createTrueVisitor(registry.getNativeType(JSTypeNative.OBJECT_TYPE));
      JSType result = all.visit(trueVisitor);
      assertNotNull(result);
      // All narrowed by true instanceOf Object should produce a meaningful type.
      assertFalse("Annotation must not be '??'",
              "??".equals(result.toAnnotationString()));
      assertTrue(result.isObject());
  }
  public void testTrueVisitorOnUnionObjectNull() throws Exception {
      JSType object = registry.getNativeType(JSTypeNative.OBJECT_TYPE);
      JSType nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
      JSType union = registry.createUnionType(object, nullType);
      Visitor<JSType> trueVisitor = createTrueVisitor(object);
      JSType result = union.visit(trueVisitor);
      assertNotNull(result);
      // union(Object, null) with true Object instanceof should yield Object.
      assertTrue(result.isObject());
      assertFalse(result.isNullType());
      assertEquals("Object", result.toAnnotationString());
  }
  public void testTrueVisitorOnUnionFunctionUndefined() throws Exception {
      JSType funcType = registry.getNativeType(JSTypeNative.FUNCTION_PROTOTYPE);
      JSType undefined = registry.getNativeType(JSTypeNative.VOID_TYPE);
      JSType union = registry.createUnionType(funcType, undefined);
      Visitor<JSType> trueVisitor = createTrueVisitor(funcType);
      JSType result = union.visit(trueVisitor);
      assertNotNull(result);
      // union(Function, undefined) with true Function instanceof should yield Function.
      assertTrue(result.isFunctionType());
      assertFalse("??".equals(result.toAnnotationString()));
  }
  // --- False visitor tests ---------------------------------------------------------------
  public void testFalseVisitorOnUnknownType() throws Exception {
      JSType unknown = registry.getNativeType(JSTypeNative.UNKNOWN_TYPE);
      Visitor<JSType> falseVisitor =
createFalseVisitor(registry.getNativeType(JSTypeNative.OBJECT_TYPE));
      JSType result = unknown.visit(falseVisitor);
      assertNotNull(result);
      // Unknown with false instanceOf should not produce "??".
      assertFalse("Annotation must not be '??'",
              "??".equals(result.toAnnotationString()));
  }
  public void testFalseVisitorOnTopType() throws Exception {
      JSType all = registry.getNativeType(JSTypeNative.ALL_TYPE);
      Visitor<JSType> falseVisitor =
createFalseVisitor(registry.getNativeType(JSTypeNative.OBJECT_TYPE));
      JSType result = all.visit(falseVisitor);
      assertNotNull(result);
      // Top with false instanceOf should not produce "??".
      assertFalse("Annotation must not be '??'",
              "??".equals(result.toAnnotationString()));
  }
  public void testFalseVisitorOnUnionObjectNull() throws Exception {
      JSType object = registry.getNativeType(JSTypeNative.OBJECT_TYPE);
      JSType nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
      JSType union = registry.createUnionType(object, nullType);
      Visitor<JSType> falseVisitor = createFalseVisitor(object);
      JSType result = union.visit(falseVisitor);
      assertNotNull(result);
      // false instanceOf Object on union(Object, null) should leave only null.
      assertTrue(result.isNullType());
      assertEquals("null", result.toAnnotationString());
  }
  public void testFalseVisitorOnUnionFunctionUndefined() throws Exception {
      JSType funcType = registry.getNativeType(JSTypeNative.FUNCTION_PROTOTYPE);
      JSType undefined = registry.getNativeType(JSTypeNative.VOID_TYPE);
      JSType union = registry.createUnionType(funcType, undefined);
      Visitor<JSType> falseVisitor = createFalseVisitor(funcType);
      JSType result = union.visit(falseVisitor);
      assertNotNull(result);
      // false instanceOf Function on union(Function, undefined) should yield undefined.
      assertTrue(result.isVoidType());
      assertFalse("??".equals(result.toAnnotationString()));
  }
  public void testFalseVisitorOnIncompatibleType() throws Exception {
      JSType nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
      JSType numberInstance = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
      Visitor<JSType> falseVisitor = createFalseVisitor(numberInstance);
      JSType result = nullType.visit(falseVisitor);
      // null is already incompatible with Number, so it should remain null.
      assertTrue(result.isNullType());
      assertEquals("null", result.toAnnotationString());
  }
  // --- Edge case: nested union flattening (if applicable) --------------------------------
  public void testTrueVisitorOnNestedUnion() throws Exception {
      JSType number = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
      JSType string = registry.getNativeType(JSTypeNative.STRING_TYPE);
      JSType innerUnion = registry.createUnionType(number, string);
      JSType bool = registry.getNativeType(JSTypeNative.BOOLEAN_TYPE);
      JSType outerUnion = registry.createUnionType(innerUnion, bool);
      Visitor<JSType> trueVisitor = createTrueVisitor(number);
      JSType result = outerUnion.visit(trueVisitor);
      // Should narrow to number (the only compatible member).
      assertTrue(result.isNumber());
      assertEquals("number", result.toAnnotationString());
  }

}
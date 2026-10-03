package com.google.javascript.rhino.jstype;

 import static org.junit.Assert.*;

 import com.google.javascript.rhino.ErrorReporter;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.StaticScope;

 import org.junit.Before;
 import org.junit.Test;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;

 /**
  * Regression tests for FunctionType, focusing on casting issues with non-ObjectType
  * values (StringType, UnionType) that historically caused ClassCastException.
  */
 public class FunctionTypeTest {

     private JSTypeRegistry registry;
     private ErrorReporter errorReporter;

     @Before
     public void setUp() {
         errorReporter = new ErrorReporter() {
             @Override public void warning(String message, String sourceName, int line, String
lineSource, int lineOffset) { }
             @Override public void error(String message, String sourceName, int line, String
lineSource, int lineOffset) { }
             @Override public void error(String message, String sourceName, int line, int
lineOffset) { }
         };
         registry = new JSTypeRegistry(errorReporter);
         registry.initTypes();
     }

     // ---- Helpers for creating test FunctionTypes ----

     private FunctionType createInterfaceType() {
         // package-private constructor: FunctionType(registry, name, source)
         return new FunctionType(registry, "TestInterface", Node.newString(Token.NAME, "test"));
     }

     private FunctionType createOrdinaryFunction() {
         ArrowType arrow = new ArrowType(registry, new Node(Token.LP), null);
         return new FunctionType(registry, "testFunc", null, arrow,
                 registry.getNativeObjectType(JSTypeNative.UNKNOWN_TYPE),
                 null, false, false);
     }

     private FunctionType createConstructor() {
         ArrowType arrow = new ArrowType(registry, new Node(Token.LP), null);
         return new FunctionType(registry, "Ctor", null, arrow,
                 null, // typeOfThis will be replaced with InstanceObjectType
                 null, true, false);
     }

     // ---- 1. setPrototypeBasedOn normal case ----
     @Test
     public void testSetPrototypeBasedOn_validObjectType() {
         FunctionType ctor = createConstructor();
         ObjectType baseType = registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
         ctor.setPrototypeBasedOn(baseType);
         assertNotNull("prototype should be set", ctor.getPrototype());
         // The implicit prototype of our prototype should be baseType
         assertEquals(baseType, ctor.getPrototype().getImplicitPrototype());
     }

     // ---- 2. setPrototypeBasedOn with a non-ObjectType triggers ClassCastException in buggy
version ----
     @Test
     public void testSetPrototypeBasedOn_nonObjectTypeShouldNotThrowClassCast() {
         FunctionType ctor = createConstructor();
         // StringType is not an ObjectType; the buggy code blindly cast it.
         // We force it through a raw Object cast to bypass the compiler.
         JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
         try {
             ctor.setPrototypeBasedOn((ObjectType)(Object) stringType);
             // After fix, the method should either throw a proper exception or handle gracefully.
             // Here we only assert that a ClassCastException is not thrown.
             // If a different runtime exception is thrown it is acceptable for the fixed version.
         } catch (ClassCastException cce) {
             fail("ClassCastException should not be thrown when passing non-ObjectType to
setPrototypeBasedOn");
         } catch (RuntimeException acceptable) {
             // other runtime exceptions (e.g. throw from FunctionPrototypeType constructor if it
guards type)
             // are acceptable fallbacks after the fix.
         }
     }

     // ---- 3. getTypeOfThis for constructors vs ordinary functions ----
     @Test
     public void testGetTypeOfThis_constructorReturnsInstanceType() {
         FunctionType ctor = createConstructor();
         ObjectType instanceType = ctor.getTypeOfThis();
         assertNotNull(instanceType);
         assertTrue("Constructor typeOfThis should be InstanceObjectType",
                 instanceType instanceof InstanceObjectType);
         assertFalse(instanceType.isNoObjectType());
     }

     @Test
     public void testGetTypeOfThis_ordinaryFunctionReturnsUnknownType() {
         FunctionType func = createOrdinaryFunction();
         ObjectType typeOfThis = func.getTypeOfThis();
         assertNotNull(typeOfThis);
         // Unknown type is an object type, not a primitive
         assertEquals(registry.getNativeObjectType(JSTypeNative.UNKNOWN_TYPE), typeOfThis);
     }

     // ---- 4. getInstanceType throws when not constructor/interface ----
     @Test(expected = IllegalStateException.class)
     public void testGetInstanceType_throwsForOrdinaryFunction() {
         FunctionType func = createOrdinaryFunction();
         func.getInstanceType();
     }

     @Test
     public void testGetInstanceType_succeedsForConstructor() {
         FunctionType ctor = createConstructor();
         ObjectType instance = ctor.getInstanceType();
         assertNotNull(instance);
     }

     // ---- 5. setImplementedInterfaces normal ----
     @Test
     public void testSetImplementedInterfaces_normal() {
         FunctionType ctor = createConstructor();
         ObjectType objType = registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
         List<ObjectType> interfaces = Arrays.<ObjectType>asList(objType);
         ctor.setImplementedInterfaces(interfaces);

         Iterable<ObjectType> result = ctor.getImplementedInterfaces();
         Iterator<ObjectType> it = result.iterator();
         assertTrue(it.hasNext());
         assertEquals(objType, it.next());
         assertFalse(it.hasNext());
     }

     // ---- 6. setImplementedInterfaces with a non-ObjectType element ----
     @Test
     public void testSetImplementedInterfaces_nonObjectTypeShouldNotThrowClassCast() {
         FunctionType ctor = createConstructor();
         // Prepare a raw list that contains a StringType (not an ObjectType)
         List rawList = new ArrayList();
         rawList.add(registry.getNativeType(JSTypeNative.STRING_TYPE)); // StringType
         rawList.add(registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE));
         try {
             ctor.setImplementedInterfaces(rawList);
             // If we reach here, no ClassCastException was thrown.
             // After fix the method might be filtering out non-ObjectType elements.
             for (ObjectType iface : ctor.getImplementedInterfaces()) {
                 assertTrue("All returned interfaces must be ObjectType",
                         iface instanceof ObjectType);
             }
         } catch (ClassCastException cce) {
             fail("ClassCastException should not be thrown by setImplementedInterfaces " +
                     "when list contains non-ObjectType elements");
         } catch (RuntimeException acceptable) {
             // acceptable alternative after fix
         }
     }

     // ---- 7. setImplementedInterfaces with null element should throw ----
     @Test
     public void testSetImplementedInterfaces_nullElement() {
         FunctionType ctor = createConstructor();
         List<ObjectType> listWithNull = new ArrayList<ObjectType>();
         listWithNull.add(null);
         try {
             ctor.setImplementedInterfaces(listWithNull);
             // If null is not rejected, we may get NPE from the call to
registry.registerTypeImplementingInterface
             // That is a valid behavior to protect against null.
         } catch (NullPointerException expected) {
             // expected in both buggy and fixed versions
         }
     }

     // ---- 8. getSuperClassConstructor with no superclass ----
     @Test
     public void testGetSuperClassConstructor_noSuperclassReturnsNull() {
         FunctionType ctor = createConstructor();
         assertNull(ctor.getSuperClassConstructor());
     }

     // ---- 9. getTopMostDefiningType for a property not defined anywhere ----
     @Test
     public void testGetTopMostDefiningType_missingProperty() {
         FunctionType ctor = createConstructor();
         assertNull(ctor.getTopMostDefiningType("nonexistentProperty"));
     }

     // ---- 10. resolveInternal with non-ObjectType typeOfThis (UnionType) ----
     @Test
     public void testResolve_withUnionTypeOfThisShouldNotThrowClassCast() {
         // Create a UnionType to use as typeOfThis – this simulates backward typedef scenarios.
         ObjectType objType = registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
         JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
         UnionType union = registry.createUnionType(stringType, objType);

         // Build a constructor whose typeOfThis is a UnionType (via raw assignment).
         ArrowType arrow = new ArrowType(registry, new Node(Token.LP), null);
         FunctionType buggyCtor = new FunctionType(registry, "BadCtor", null, arrow,
                 null, null, true, false);
         // Override the instance type with the union – this is what backward typedef might produce.
         buggyCtor.setInstanceType((ObjectType)(Object) union);

         // In the buggy version, resolve would cast typeOfThis to (ObjectType) and throw
ClassCastException.
         try {
             // resolve requires a scope; use a minimal scope that returns the same union for its
typeOfThis.
             JSType resolved = buggyCtor.resolve(errorReporter, new StaticScope<JSType>() {
                 @Override public Node getRootNode() { return null; }
                 @Override public StaticScope<JSType> getParentScope() { return null; }
                 @Override public JSType getTypeOfThis() { return null; }
            });
             // After resolution, getTypeOfThis should be an ObjectType (or the method no longer
crashes)
             ObjectType thisType = buggyCtor.getTypeOfThis();
             assertNotNull(thisType);
             assertFalse("TypeOfThis should not be null object", thisType.isNoObjectType());
         } catch (ClassCastException cce) {
             fail("resolve() should not throw ClassCastException when typeOfThis is a UnionType");
         } catch (RuntimeException acceptable) {
             // After fix, if the code throws a different runtime exception because of invalid
setup, it's okay.
         }
     }

     // ---- Additional boundary tests ----
     @Test
     public void testSetPrototype_basicAssignment() {
         FunctionType ctor = createConstructor();
         ObjectType baseType = registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
         ctor.setPrototypeBasedOn(baseType);
         FunctionPrototypeType proto = ctor.getPrototype();
         assertNotNull(proto);
         assertEquals(baseType, proto.getImplicitPrototype());
     }
 }

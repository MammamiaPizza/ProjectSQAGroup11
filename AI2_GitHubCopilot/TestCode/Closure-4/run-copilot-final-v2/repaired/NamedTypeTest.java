package com.google.javascript.rhino.jstype;

 import static org.junit.Assert.*;

 import com.google.javascript.rhino.ErrorReporter;
 import com.google.javascript.rhino.Node;

 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.List;

 import org.junit.Before;
 import org.junit.Test;

 /**
  * Unit tests for {@link NamedType} targeting cycle detection and
  * the fixes for issues #873 (StackOverflowError and wrong warning type).
  */
 public class NamedTypeTest {

   private JSTypeRegistry registry;
   private TestErrorReporter errorReporter;

   @Before
   public void setUp() {
     errorReporter = new TestErrorReporter();
     registry = new JSTypeRegistry(errorReporter);
   }

   // ----------------------------------------------------------------- helper

   /** Returns the named type's internal referenced type via reflection. */
   private JSType getReferencedTypeInternal(NamedType nt) throws Exception {
     Method m = ProxyObjectType.class.getDeclaredMethod("getReferencedTypeInternal");
     m.setAccessible(true);
     return (JSType) m.invoke(nt);
   }

   /** Sets the internal referenced type via reflection. */
   private void setReferencedType(NamedType nt, JSType type) throws Exception {
     Method m = ProxyObjectType.class.getDeclaredMethod("setReferencedType", JSType.class);
     m.setAccessible(true);
     m.invoke(nt, type);
   }

   /** Sets the internal resolved-type flag. */
   private void setResolvedTypeInternal(NamedType nt, JSType t) throws Exception {
     Method m = ProxyObjectType.class.getDeclaredMethod("setResolvedTypeInternal", JSType.class);
     m.setAccessible(true);
     m.invoke(nt, t);
   }

   /** Invokes the private handleTypeCycle(). */
   private void handleTypeCycle(NamedType nt) throws Exception {
     Method m = NamedType.class.getDeclaredMethod("handleTypeCycle", ErrorReporter.class);
     m.setAccessible(true);
     m.invoke(nt, errorReporter);
   }

   /** Invokes resolveInternal and expects no StackOverflowError. */
   private void resolveInternal(NamedType nt, StaticScope<JSType> enclosing) throws Exception {
     Method m = NamedType.class.getSuperclass().getDeclaredMethod(
         "resolveInternal", ErrorReporter.class, StaticScope.class);
     m.setAccessible(true);
     try {
       m.invoke(nt, errorReporter, enclosing);
     } catch (StackOverflowError e) {
       fail("StackOverflowError during resolution: " + e);
     }
   }

   /** Gets the value of the 'propertyContinuations' field. */
   @SuppressWarnings("unchecked")
   private List<?> getPropertyContinuations(NamedType nt) throws Exception {
     Field f = NamedType.class.getDeclaredField("propertyContinuations");
     f.setAccessible(true);
     return (List<?>) f.get(nt);
   }

   /**
    * Creates a fresh NamedType that resolves via the registry to a simple
    * non-cyclic type.
    */
   private NamedType createResolvedType(String name) throws Exception {
     // Create a base object to resolve to.
     ObjectType base = registry.createAnonymousObjectType();
     registry.declareType(name, base);
     NamedType nt = new NamedType(registry, name, "test.js", 1, 0);
     // Resolve it once.
     resolveInternal(nt, null);
     return nt;
   }

   // ----------------------------------------------------------------- tests

   @Test
   public void testConstructorDefaults() {
     NamedType nt = new NamedType(registry, "Foo", "test.js", 2, 3);
     assertEquals("Foo", nt.getReferenceName());
     assertTrue(nt.hasReferenceName());
     assertTrue(nt.isNominalType());
     assertTrue(nt.isNamedType());
     // referenced type defaults to UNKNOWN (constructor passes UNKNOWN_TYPE)
     assertEquals("Unknown", nt.getReferencedType().toString());
   }

   @Test
   public void testHandleTypeCycleSetsUnknownAndWarns() throws Exception {
     NamedType nt = new NamedType(registry, "X", "x.js", 1, 0);
     JSType prior = nt.getReferencedType();
     handleTypeCycle(nt);
     // after cycle fix, referenced type becomes UNKNOWN
     JSType after = nt.getReferencedType();
     assertNotSame(prior, after);
     assertTrue(after.isUnknownType());
     // a warning was emitted
     assertTrue(errorReporter.lastWarning.contains("Cycle detected"));
   }

   @Test
   public void testGetReferencedTypeAfterCycleReturnsUnknown() throws Exception {
     NamedType nt = new NamedType(registry, "Self", "s.js", 1, 0);
     // simulate cycle fix
     handleTypeCycle(nt);
     assertEquals("Unknown", nt.getReferencedType().toString());
     assertTrue(nt.getReferencedType().isUnknownType());
   }

   @Test
   public void testFinishPropertyContinuationsClearsAfterCycle() throws Exception {
     NamedType nt = new NamedType(registry, "Rec", "r.js", 1, 0);
     // add a dummy property continuation via defineProperty
     nt.defineProperty("prop", registry.getNativeType(JSTypeNative.NUMBER_TYPE),
         false, null);
     assertNotNull("should have pending continuation", getPropertyContinuations(nt));

     // trigger cycle fix, which calls setReferencedType(UNKNOWN) and setResolvedTypeInternal
     handleTypeCycle(nt);

     // resolve internal should finishPropertyContinuations; referenced type is unknown,
     // so continuations are simply cleared without committing.
     resolveInternal(nt, null);
     assertNull("property continuations should be cleared",
         getPropertyContinuations(nt));
   }

   @Test
   public void testResolvedTypeHashCodeConsistency() {
     NamedType a = new NamedType(registry, "Hash", "h.js", 1, 0);
     NamedType b = new NamedType(registry, "Hash", "h.js", 1, 0);
     assertEquals(a.hashCode(), b.hashCode());
     assertEquals("Hash", a.getReferenceName());
   }

   @Test
   public void testSetValidatorOnUnresolvedTypeAppliedOnResolution() throws Exception {
     NamedType nt = new NamedType(registry, "Val", "v.js", 1, 0);
     final boolean[] applied = {false};
     nt.setValidator(new com.google.common.base.Predicate<JSType>() {
       @Override public boolean apply(JSType input) {
         applied[0] = true;
         return true;
       }
     });
     assertFalse(applied[0]);   // not yet resolved

     // resolve by registering a type
     ObjectType base = registry.createAnonymousObjectType();
     registry.declareType("Val", base);
     resolveInternal(nt, null);
     assertTrue("validator was applied during resolution", applied[0]);
   }

   @Test
   public void testSelfReferenceDoesNotOverflow() throws Exception {
     // A extends A – self loop
     NamedType loop = new NamedType(registry, "Loop", "l.js", 1, 0);
     // make it point to itself
     setReferencedType(loop, loop);
     // resolution should detect cycle and NOT stack-overflow
     resolveInternal(loop, null);
     // after cycle handling, referenced type should be unknown
     assertTrue(loop.getReferencedType().isUnknownType());
   }

   @Test
   public void testMutualExtendsDoesNotOverflow() throws Exception {
     // A extends B, B extends A
     NamedType a = new NamedType(registry, "A", "a.js", 1, 0);
     NamedType b = new NamedType(registry, "B", "b.js", 1, 0);
     setReferencedType(a, b);
     setReferencedType(b, a);

     resolveInternal(a, null);
     assertTrue("A should be resolved to unknown after cycle",
         a.getReferencedType().isUnknownType());
     assertTrue("B should be resolved to unknown after cycle",
         b.getReferencedType().isUnknownType());
   }

   @Test
   public void testImplementsSelfLoopWarningIsCycleNotImplementsNonInterface() throws Exception {
     // I implements I – should produce a cycle warning, not "can only implement interfaces"
     errorReporter = new TestErrorReporter();
     registry = new JSTypeRegistry(errorReporter);

     // Register I as an interface type
     FunctionType interf = new FunctionType(registry, "I", null,
         registry.createArrowType(null),
         null, null, null, null);
     interf.setImplementedInterfaces(new ArrayList<ObjectType>());
     // Declare I in registry
     registry.declareType("I", interf);

     // Create a named type referencing itself and set its referenced type to itself
     NamedType nt = new NamedType(registry, "I", "i.js", 1, 0);
     setReferencedType(nt, nt);

     // Resolve
     resolveInternal(nt, null);

     // Expect cycle warning, not the mis-classified IMPLEMENTS_NON_INTERFACE
     assertTrue("expected cycle warning",
         errorReporter.lastWarning != null &&
         errorReporter.lastWarning.contains("Cycle detected"));
     assertFalse("should not have 'can only implement interfaces'",
         errorReporter.lastWarning != null &&
         errorReporter.lastWarning.contains("can only implement interfaces"));
   }

   @Test
   public void testMutualImplementsCycleWarningIsCycle() throws Exception {
     // I1 implements I2, I2 implements I1
     errorReporter = new TestErrorReporter();
     registry = new JSTypeRegistry(errorReporter);

     // Create two interface types
     FunctionType i1Type = new FunctionType(registry, "I1", null,
         registry.createArrowType(null), null, null, null, null);
     FunctionType i2Type = new FunctionType(registry, "I2", null,
         registry.createArrowType(null), null, null, null, null);
     registry.declareType("I1", i1Type);
     registry.declareType("I2", i2Type);

     NamedType i1 = new NamedType(registry, "I1", "i1.js", 1, 0);
     NamedType i2 = new NamedType(registry, "I2", "i2.js", 1, 0);

     // Create mutual references (the referenced type of each is the other)
     setReferencedType(i1, i2);
     setReferencedType(i2, i1);

     resolveInternal(i1, null);
     resolveInternal(i2, null);

     // At least one of the resolutions should have emitted a cycle warning
     boolean hasCycle = errorReporter.lastWarning != null &&
         errorReporter.lastWarning.contains("Cycle detected");
     assertTrue("expected a cycle warning", hasCycle);
   }

   @Test
   public void testNoStackOverflowOnInterfaceToRecursiveConstructor() throws Exception {
     // Setup: interface I, constructor C that implements I and references a type that
     // leads back to I. This mimics the scenario from
     // testConversionFromInterfaceToRecursiveConstructor.
     errorReporter = new TestErrorReporter();
     registry = new JSTypeRegistry(errorReporter);

     // I – an interface
     FunctionType interfaceType = new FunctionType(registry, "MyInterface", null,
         registry.createArrowType(null), null, null, null, null);
     registry.declareType("MyInterface", interfaceType);

     // Constructor C
     FunctionType ctor = new FunctionType(registry, "C", null,
         registry.createArrowType(null), null, null, null, null);
     registry.declareType("C", ctor);

     // A named type that refers to itself (or to a type that refers back)
     NamedType root = new NamedType(registry, "MyInterface", "m.js", 1, 0);
     // Make it proxy a recursive chain: MyInterface -> NamedType("C") ... but to trigger
     // the bug we need a conversion attempt that calls getReferencedType in a loop.
     // A minimal trigger: set the referenced type to a NamedType that points back to root.
     NamedType loopBack = new NamedType(registry, "C", "c.js", 1, 0);
     setReferencedType(root, loopBack);
     setReferencedType(loopBack, root);

     // Should handle without StackOverflowError
     resolveInternal(root, null);
     assertTrue(root.getReferencedType().isUnknownType());
     assertTrue(loopBack.getReferencedType().isUnknownType());
   }

   // ----------------------------------------------------------------- error collector

   static class TestErrorReporter implements ErrorReporter {
     String lastWarning;
     List<String> warnings = new ArrayList<String>();
     @Override
     public void warning(String message, String sourceName, int line, int lineOffset) {
       lastWarning = message;
       warnings.add(message);
     }
     @Override
     public void error(String message, String sourceName, int line, int lineOffset) {
       // not used
     }
     @Override
     public void error(String message, Throwable t, String sourceName, int line, int lineOffset) {
       // not used
     }
   }
 }

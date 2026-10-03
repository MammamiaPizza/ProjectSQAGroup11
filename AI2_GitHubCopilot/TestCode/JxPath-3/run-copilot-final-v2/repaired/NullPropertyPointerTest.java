package org.apache.commons.jxpath.ri.model.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.Pointer;

import junit.framework.TestCase;

/**

 - Tests for NullPropertyPointer, targeting JXPATH-68 regression:
 - createPath must not throw AssertionFailedError when a factory is broken.
  */
 public class NullPropertyPointerTest extends TestCase {
  private JXPathContext context;
  private Map bean;
  protected void setUp() {
  bean = new HashMap();
  bean.put("name", "test");
  bean.put("address", null);
  context = JXPathContext.newContext(bean);
  }
  // ---------- JXPATH-68: broken factory ----------
  /**
  - JXPATH-68: createPath must not throw AssertionFailedError
  - when the factory is broken (returns false / does not create).
    */
   public void testCreatePathWithBrokenFactory() {
   context.setFactory(new BadlyImplementedFactory());
   try {
   context.createPath("/address/city");
   } catch (AssertionError e) {
   fail("createPath should not throw AssertionError: " + e.getMessage());
   } catch (JXPathInvalidAccessException e) {
   // acceptable outcome for a broken factory
   } catch (Exception e) {
   // any non-assertion exception is acceptable
   }
   }
  /**
  - JXPATH-68: createPathAndSetValue must not throw
  - AssertionFailedError when the factory is broken.
    */
   public void testCreatePathAndSetValueWithBrokenFactory() {
   context.setFactory(new BadlyImplementedFactory());
   try {
   context.createPathAndSetValue("/address/city", "Springfield");
   } catch (AssertionError e) {
   fail("createPathAndSetValue should not throw AssertionError: "
           + e.getMessage());
   } catch (JXPathInvalidAccessException e) {
   // acceptable
   } catch (Exception e) {
   // acceptable if not AssertionError
   }
   }
  // ---------- property name ----------
  /**
  - Empty property name via setPropertyName; getName must reflect it.
    */
   public void testSetPropertyNameEmpty() {
   Pointer ptr = context.getPointer("/address");
   assertTrue(ptr instanceof NullPropertyPointer);
   NullPropertyPointer npp = (NullPropertyPointer) ptr;
   npp.setPropertyName("");
   assertEquals("", npp.getPropertyName());
   assertEquals("", npp.getName().getName());
   }
  /**
  - Null property name via setPropertyName.
    */
   public void testSetPropertyNameNull() {
   Pointer ptr = context.getPointer("/address");
   assertTrue(ptr instanceof NullPropertyPointer);
   NullPropertyPointer npp = (NullPropertyPointer) ptr;
   npp.setPropertyName(null);
   assertNull(npp.getPropertyName());
   }
  // ---------- setValue ----------
  /**
  - setValue(null) on a NullPropertyPointer with a container parent
  - must throw JXPathInvalidAccessException.
    */
   public void testSetValueThrowsWhenParentIsContainer() {
   Pointer ptr = context.getPointer("/address");
   assertTrue(ptr instanceof NullPropertyPointer);
   NullPropertyPointer npp = (NullPropertyPointer) ptr;
   try {
   npp.setValue(null);
   fail("setValue should throw when the target object is null");
   } catch (JXPathInvalidAccessException e) {
   // expected
   }
   }
  /**
  - setValue with a non-null value via a null-property pointer;
  - the parent is not a dynamic property owner on a plain Map,
  - so it should throw.
    */
   public void testSetValueThrowsOnNonDynamicParent() {
   Pointer ptr = context.getPointer("/address");
   assertTrue(ptr instanceof NullPropertyPointer);
   NullPropertyPointer npp = (NullPropertyPointer) ptr;
   try {
   npp.setValue("someValue");
   fail("setValue should throw on non-dynamic parent");
   } catch (JXPathInvalidAccessException e) {
   // expected
   }
   }
  // ---------- base / immediate node ----------
  /**
  - getBaseValue on a NullPropertyPointer always returns null.
    */
   public void testGetBaseValueReturnsNull() {
   Pointer ptr = context.getPointer("/address");
   assertTrue(ptr instanceof NullPropertyPointer);
   NullPropertyPointer npp = (NullPropertyPointer) ptr;
   assertNull(npp.getBaseValue());
   }
  /**
  - getImmediateNode on a NullPropertyPointer always returns null.
    */
   public void testGetImmediateNodeReturnsNull() {
   Pointer ptr = context.getPointer("/address");
   assertTrue(ptr instanceof NullPropertyPointer);
   NullPropertyPointer npp = (NullPropertyPointer) ptr;
   assertNull(npp.getImmediateNode());
   }
  // ---------- isLeaf / isActual / isContainer ----------
  /**
  - NullPropertyPointer is always a leaf and is never actual.
    */
   public void testLeafAndActualFlags() {
   Pointer ptr = context.getPointer("/address");
   assertTrue(ptr instanceof NullPropertyPointer);
   NullPropertyPointer npp = (NullPropertyPointer) ptr;
   assertTrue(npp.isLeaf());
   assertFalse(npp.isActual());
   assertTrue(npp.isContainer());
   }
  // ---------- createPath with value ----------
  /**
  - createPath(JXPathContext, Object) with a null value through
  - a null property must not throw AssertionError.
    */
   public void testCreatePathWithNullValue() {
   context.setFactory(new BadlyImplementedFactory());
   try {
   context.createPathAndSetValue("/address/city", null);
   } catch (AssertionError e) {
   fail("should not throw AssertionError: " + e.getMessage());
   } catch (Exception e) {
   // acceptable
   }
   }
  // ---------- sequence ----------
  /**
  - Sequence: navigate to null property, setValue should throw,
  - then createPath should not throw AssertionError with broken factory.
    */
   public void testSequenceSetValueThenCreatePath() {
   Pointer ptr = context.getPointer("/address");
   assertTrue(ptr instanceof NullPropertyPointer);
   NullPropertyPointer npp = (NullPropertyPointer) ptr;
   // setValue must fail
   try {
   npp.setValue("x");
   fail("setValue should have thrown");
   } catch (JXPathInvalidAccessException e) {
   // expected
   }
   // createPath with broken factory must not throw AssertionError
   context.setFactory(new BadlyImplementedFactory());
   try {
   context.createPath("/address/city");
   } catch (AssertionError e) {
   fail("createPath after setValue should not throw AssertionError: "
           + e.getMessage());
   } catch (Exception e) {
   // acceptable
   }
   }
  // ---------- setPropertyIndex no-op ----------
  /**
  - setPropertyIndex is a no-op on NullPropertyPointer; calling it
  - must not change state or throw.
    */
   public void testSetPropertyIndexIsNoOp() {
   Pointer ptr = context.getPointer("/address");
   assertTrue(ptr instanceof NullPropertyPointer);
   NullPropertyPointer npp = (NullPropertyPointer) ptr;
   npp.setPropertyIndex(5);
   // no exception and no observable state change
   assertEquals(0, npp.getLength());
   assertNull(npp.getBaseValue());
   }

}

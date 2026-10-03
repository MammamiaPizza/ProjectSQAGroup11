package com.google.javascript.rhino.jstype;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.javascript.rhino.ErrorReporter;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**

 - Tests for {@link PrototypeObjectType} that target the property-loss
 - bug described in issue
  785.  The critical behaviour: after
 - {@link #matchRecordTypeConstraint(ObjectType)} infers properties on
 - an anonymous object, those properties must remain accessible via all
 - inspection methods.
  */
 public class PrototypeObjectTypeTest extends TestCase {

  private JSTypeRegistry registry; private PrototypeObjectType object;

  @Override protected void setUp() throws Exception {
    super.setUp();
    registry = new JSTypeRegistry(new SilentErrorReporter());
    object = new PrototypeObjectType(registry, null, null); }

  // -- empty state --------------------------------------------------

  public void testEmptyObject() {
    assertEquals(0, object.getPropertiesCount());
    assertTrue("own names must be empty", object.getOwnPropertyNames().isEmpty());
    assertFalse(object.hasProperty("anything"));
    assertFalse(object.isPropertyTypeInferred("anything"));
    JSType unknown = registry.getNativeType(JSTypeNative.UNKNOWN_TYPE);
    assertEquals(unknown, object.getPropertyType("anything")); }

  // -- inferred-property addition via constraint -------------------

  public void testAddInferredPropertiesViaConstraint() {
    Map<String, JSType> props = new HashMap<String, JSType>();
    JSType numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
    JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
    props.put("a", numberType);
    props.put("b", stringType);
    ObjectType constraint = registry.createRecordType(props);
    object.matchRecordTypeConstraint(constraint);

 // existence
 assertTrue(object.hasProperty("a"));
 assertTrue(object.hasProperty("b"));
 // correct types
 assertEquals(numberType, object.getPropertyType("a"));
 assertEquals(stringType, object.getPropertyType("b"));
 // inferred flag
 assertTrue(object.isPropertyTypeInferred("a"));
 assertTrue(object.isPropertyTypeInferred("b"));
 // count and enumeration
 assertEquals(2, object.getPropertiesCount());
 Set<String> names = object.getOwnPropertyNames();
 assertTrue(names.contains("a"));
 assertTrue(names.contains("b"));
 assertEquals(2, names.size());
 // Slot retrieval
 assertNotNull(object.getSlot("a"));
 assertNotNull(object.getSlot("b"));
 assertNull(object.getSlot("c")); }

  // -- existing props survive subsequent constraints ---------------

  public void testMultipleConstraintsPreserveProperties() {
    JSType boolType = registry.getNativeType(JSTypeNative.BOOLEAN_TYPE);
    JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);

 ObjectType c1 = registry.createRecordType(ImmutableMap.of("x", boolType));
 object.matchRecordTypeConstraint(c1);
 ObjectType c2 = registry.createRecordType(ImmutableMap.of("y", stringType));
 object.matchRecordTypeConstraint(c2);

 assertTrue(object.hasProperty("x"));
 assertEquals(boolType, object.getPropertyType("x"));
 assertTrue(object.hasProperty("y"));
 assertEquals(stringType, object.getPropertyType("y"));
 assertEquals(2, object.getPropertiesCount()); }

  // -- removal and re-addition ------------------------------------

  public void testRemoveProperty() {
    JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);

 ObjectType constraint = registry.createRecordType(ImmutableMap.of("p", stringType));
 object.matchRecordTypeConstraint(constraint);

 assertTrue(object.hasProperty("p"));
 assertTrue(object.removeProperty("p"));
 assertFalse(object.hasProperty("p"));
 assertEquals(0, object.getPropertiesCount());

 // re-add after removel (still not declared -> should succeed)
 object.matchRecordTypeConstraint(constraint);
 assertTrue(object.hasProperty("p"));
 assertEquals(stringType, object.getPropertyType("p")); }

  // -- bulk properties --------------------------------------------

  public void testManyInferredProperties() {
    Map<String, JSType> props = new HashMap<String, JSType>();
    JSType num = registry.getNativeType(JSTypeNative.NUMBER_TYPE);

 int count = 5;
 for (int i = 0; i < count; i++) {
   props.put("k" + i, num);
 }

 ObjectType constraint = registry.createRecordType(props);
 object.matchRecordTypeConstraint(constraint);

 for (int i = 0; i < count; i++) {
   String name = "k" + i;
   assertTrue(object.hasProperty(name));
   assertTrue(object.isPropertyTypeInferred(name));
   assertEquals(num, object.getPropertyType(name));
 }

 assertEquals(count, object.getPropertiesCount());
 assertEquals(count, object.getOwnPropertyNames().size()); }

  // -- isPropertyTypeDeclared / isPropertyTypeInferred ------------

  public void testOnlyInferredNeverDeclared() {
    Map<String, JSType> props = new HashMap<String, JSType>();
    props.put("f", registry.getNativeType(JSTypeNative.STRING_TYPE));
    ObjectType constraint = registry.createRecordType(props);
    object.matchRecordTypeConstraint(constraint);

 assertTrue(object.isPropertyTypeInferred("f"));
 assertFalse(object.isPropertyTypeDeclared("f"));
 assertFalse(object.isPropertyTypeDeclared("nonexistent"));
 assertFalse(object.isPropertyTypeInferred("nonexistent")); }

  // -- constraint with no own-props is a no-op ------------------

  public void testMatchWithEmptyConstraintDoesNoHarm() {
    JSType boolType = registry.getNativeType(JSTypeNative.BOOLEAN_TYPE);
    ObjectType c1 = registry.createRecordType(ImmutableMap.of("x", boolType));
    object.matchRecordTypeConstraint(c1);
    // empty constraint
    ObjectType emptyConstraint = registry.createRecordType(ImmutableMap.<String, JSType>of());
    object.matchRecordTypeConstraint(emptyConstraint);

 // x must still be there
 assertTrue(object.hasProperty("x"));
 assertEquals(boolType, object.getPropertyType("x"));
 assertEquals(1, object.getPropertiesCount()); }

  // -- getOwnPropertyNames snapshot -------------------------------

  public void testGetOwnPropertyNamesIsConsistent() {
    Map<String, JSType> props = new HashMap<String, JSType>();
    JSType num = registry.getNativeType(JSTypeNative.NUMBER_TYPE);

 props.put("a", num);
 props.put("b", num);
 ObjectType constraint = registry.createRecordType(props);
 object.matchRecordTypeConstraint(constraint);

 Set<String> names = object.getOwnPropertyNames();
 assertEquals(2, names.size());
 assertTrue(names.contains("a"));
 assertTrue(names.contains("b")); }

  // ---------------------------------------------------------------

  private static final class SilentErrorReporter implements ErrorReporter {
    @Override
    public void warning(String message, String sourceName, int line, String lineSource,
        int lineOffset) {}

 @Override
 public void error(String message, String sourceName, int line, String lineSource,
     int lineOffset) {}

 @Override
 public ErrorReporter setCheckMode(boolean checkMode) { return this; }

 @Override
 public boolean isCheckMode() { return false; } }

}
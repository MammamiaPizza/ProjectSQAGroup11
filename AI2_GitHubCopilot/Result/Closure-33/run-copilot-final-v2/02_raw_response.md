package com.google.javascript.rhino.jstype;

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.Node;

import junit.framework.TestCase;

import java.util.Set;

public class PrototypeObjectTypeTest extends TestCase {

 private JSTypeRegistry registry;
 private Compiler compiler;

 @Override
 protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     registry = new JSTypeRegistry(compiler);
 }

 // Own property is found directly.
 public void testGetSlotFromOwnProperty() {
     PrototypeObjectType obj = new PrototypeObjectType(registry, "Test", null);
     obj.setPropertyJSDocInfo("ownProp", new JSDocInfo((Node) null));
     Property slot = obj.getSlot("ownProp");
     assertNotNull("Own property should be returned", slot);
     assertEquals("ownProp", slot.getName());
 }

 // Property inherited from the implicit prototype is resolved.
 public void testGetSlotFromImplicitPrototype() {
     PrototypeObjectType proto = new PrototypeObjectType(registry, "Proto", null);
     proto.setPropertyJSDocInfo("inherited", new JSDocInfo((Node) null));

     PrototypeObjectType child = new PrototypeObjectType(registry, "Child", proto);
     Property slot = child.getSlot("inherited");
     assertNotNull("Inherited property should be found", slot);
     assertEquals("inherited", slot.getName());
     assertFalse("Should not be an own property", child.hasOwnProperty("inherited"));
 }

 // Property on an extended interface is visible.
 public void testGetSlotFromExtendedInterface() {
     PrototypeObjectType iface = new PrototypeObjectType(registry, "IFace", null);
     iface.setPropertyJSDocInfo("fromExtIface", new JSDocInfo((Node) null));

     PrototypeObjectType obj = new PrototypeObjectType(registry, "Obj", null) {
         @Override
         public boolean isFunctionPrototypeType() {
             return true;
         }

         @Override
         public Iterable<ObjectType> getCtorExtendedInterfaces() {
             return ImmutableList.<ObjectType>of(iface);
         }
     };

     Property slot = obj.getSlot("fromExtIface");
     assertNotNull("Extended-interface property should be found", slot);
 }

 // *** Fault-revealing test: property on an implemented interface is invisible. ***
 // The expected behaviour (public contract) is that it should be found.
 public void testGetSlotMissingFromImplementedInterface() {
     PrototypeObjectType iface = new PrototypeObjectType(registry, "IFaceImpl", null);
     iface.setPropertyJSDocInfo("fromImplIface", new JSDocInfo((Node) null));

     PrototypeObjectType obj = new PrototypeObjectType(registry, "ObjImpl", null) {
         @Override
         public boolean isFunctionPrototypeType() {
             return true;
         }

         @Override
         public Iterable<ObjectType> getCtorImplementedInterfaces() {
             return ImmutableList.<ObjectType>of(iface);
         }

         @Override
         public Iterable<ObjectType> getCtorExtendedInterfaces() {
             return ImmutableList.of();
         }
     };

     Property slot = obj.getSlot("fromImplIface");
     assertNotNull("Implemented-interface property should be found", slot);
 }

 // hasProperty resolves inherited properties.
 public void testHasProperty() {
     PrototypeObjectType proto = new PrototypeObjectType(registry, "PHas", null);
     proto.setPropertyJSDocInfo("a", new JSDocInfo((Node) null));
     PrototypeObjectType obj = new PrototypeObjectType(registry, "CHas", proto);

     assertTrue(obj.hasProperty("a"));
     assertTrue("toString is on Object.prototype", obj.hasProperty("toString"));
     assertFalse(obj.hasProperty("noSuchProperty"));
 }

 // hasOwnProperty distinguishes own vs. inherited.
 public void testHasOwnProperty() {
     PrototypeObjectType obj = new PrototypeObjectType(registry, "Own", null);
     obj.setPropertyJSDocInfo("ownProp", new JSDocInfo((Node) null));
     assertTrue(obj.hasOwnProperty("ownProp"));
     assertFalse(obj.hasOwnProperty("toString"));
 }

 // isPropertyTypeDeclared: inferred own property is not declared; externs
 // properties on Object.prototype are declared.
 public void testIsPropertyTypeDeclared() {
     PrototypeObjectType obj = new PrototypeObjectType(registry, "Decl", null);
     obj.setPropertyJSDocInfo("inferredProp", new JSDocInfo((Node) null));
     assertFalse("Inferred prop is not declared", obj.isPropertyTypeDeclared("inferredProp"));
     assertTrue("toString should be declared (from Object.prototype)",
obj.isPropertyTypeDeclared("toString"));
 }

 // isPropertyTypeInferred: an inferred property reports true.
 public void testIsPropertyTypeInferred() {
     PrototypeObjectType obj = new PrototypeObjectType(registry, "Infer", null);
     obj.setPropertyJSDocInfo("infer", new JSDocInfo((Node) null));
     assertTrue(obj.isPropertyTypeInferred("infer"));
     assertFalse("toString is not inferred", obj.isPropertyTypeInferred("toString"));
 }

 // The implicit prototype is set correctly.
 public void testGetImplicitPrototype() {
     PrototypeObjectType obj = new PrototypeObjectType(registry, "ImpProto", null);
     ObjectType implicit = obj.getImplicitPrototype();
     assertNotNull("Object should have an implicit prototype", implicit);
     assertTrue("Reference name should contain 'Object'",
                implicit.getReferenceName().contains("Object"));
 }

 // Properties from Object.prototype are in externs.
 public void testIsPropertyInExterns() {
     PrototypeObjectType obj = new PrototypeObjectType(registry, "Extern", null);
     assertTrue("toString is from externs", obj.isPropertyInExterns("toString"));
     // Add own, non-externs property
     obj.setPropertyJSDocInfo("local", new JSDocInfo((Node) null));
     assertFalse(obj.isPropertyInExterns("local"));
 }

 // getPropertiesCount includes own + inherited.
 public void testGetPropertiesCount() {
     PrototypeObjectType obj = new PrototypeObjectType(registry, "Count", null);
     int baseCount = obj.getPropertiesCount();
     obj.setPropertyJSDocInfo("p1", new JSDocInfo((Node) null));
     obj.setPropertyJSDocInfo("p2", new JSDocInfo((Node) null));
     // Should now have at least two more own properties than the base count.
     assertTrue("Count should reflect newly added own properties",
                obj.getPropertiesCount() >= baseCount + 2);
 }

 // getPropertyType returns the correct type for own, inherited, and missing properties.
 public void testGetPropertyType() {
     PrototypeObjectType obj = new PrototypeObjectType(registry, "GetType", null);
     obj.setPropertyJSDocInfo("foo", new JSDocInfo((Node) null));
     JSType fooType = obj.getPropertyType("foo");
     assertNotNull(fooType);

     JSType toStringType = obj.getPropertyType("toString");
     assertNotNull("toString should have a type", toStringType);

     JSType unknown = registry.getNativeType(JSTypeNative.UNKNOWN_TYPE);
     assertEquals("Missing property should return UNKNOWN", unknown, obj.getPropertyType("nope"));
 }

}
package org.apache.commons.jxpath.ri.model.beans;

import junit.framework.TestCase;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

public class NullPropertyPointerTest extends TestCase {

    public static class EmptyBean {
    }

    public static class BadFactory extends AbstractFactory {
        public boolean createObject(
                JXPathContext context,
                Pointer pointer,
                Object parent,
                String name,
                int index) {
            return true;
        }
    }

    private NodePointer rootPointer() {
        JXPathContext context = JXPathContext.newContext(new EmptyBean());
        return (NodePointer) context.getPointer(".");
    }

    public void testAbsentPropertyMetadata() {
        NullPropertyPointer pointer = new NullPropertyPointer(rootPointer());

        assertEquals("*", pointer.getPropertyName());
        assertEquals("*", pointer.getName().getName());
        assertEquals(0, pointer.getLength());
        assertEquals(0, pointer.getPropertyCount());
        assertEquals(0, pointer.getPropertyNames().length);
        assertNull(pointer.getBaseValue());
        assertNull(pointer.getImmediateNode());
        assertTrue(pointer.isLeaf());
        assertTrue(pointer.isContainer());
        assertFalse(pointer.isActual());
        assertFalse(pointer.isActualProperty());
    }

    public void testPropertyNameAndValuePointerReflectUnresolvedProperty() {
        NullPropertyPointer pointer = new NullPropertyPointer(rootPointer());
        pointer.setPropertyName("missing");

        NodePointer valuePointer = pointer.getValuePointer();

        assertEquals("missing", pointer.getName().getName());
        assertTrue(valuePointer instanceof NullPointer);
        assertEquals("missing", valuePointer.getName().getName());
    }

    public void testSetValueRejectsNullContainerParent() {
        NodePointer root = rootPointer();
        NullPointer nullParent = new NullPointer(root, new QName("missing"));
        NullPropertyPointer pointer = new NullPropertyPointer(nullParent);
        pointer.setPropertyName("child");

        try {
            pointer.setValue("value");
            fail("A property below a null container cannot be assigned directly");
        }
        catch (JXPathInvalidAccessException expected) {
            assertTrue(expected.getMessage().indexOf("target object is null") >= 0);
        }
    }

    public void testSetValueRejectsUndeclaredPropertyOfOrdinaryBean() {
        NullPropertyPointer pointer = new NullPropertyPointer(rootPointer());
        pointer.setPropertyName("missing");

        try {
            pointer.setValue("value");
            fail("An undeclared property of an ordinary bean is not changeable");
        }
        catch (JXPathInvalidAccessException expected) {
            assertTrue(expected.getMessage().indexOf("changeable location") >= 0);
        }
    }

    public void testCreatePathRejectsFactoryThatClaimsSuccessWithoutCreatingObject() {
        JXPathContext context = JXPathContext.newContext(new EmptyBean());
        context.setFactory(new BadFactory());

        try {
            context.createPath("missing");
            fail("A factory that does not create the requested object must be rejected");
        }
        catch (JXPathException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    public void testCreatePathAndSetValueRejectsFactoryThatClaimsSuccessWithoutCreatingObject() {
        JXPathContext context = JXPathContext.newContext(new EmptyBean());
        context.setFactory(new BadFactory());

        try {
            context.createPathAndSetValue("missing", "value");
            fail("A factory that does not create the requested object must be rejected");
        }
        catch (JXPathException expected) {
            assertNotNull(expected.getMessage());
        }
    }
}

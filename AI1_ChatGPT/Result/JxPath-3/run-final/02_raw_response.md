```java
package org.apache.commons.jxpath.ri.model.beans;

import junit.framework.TestCase;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Tests for {@link NullPropertyPointer}.
 */
public class NullPropertyPointerTest extends TestCase {

    public void testDefaultPointerCharacteristicsAndPropertyName() {
        JXPathContext context = JXPathContext.newContext(new Holder());
        NullPropertyPointer pointer =
                new NullPropertyPointer(context.getPointer("/"));

        assertEquals("*", pointer.getPropertyName());
        assertEquals("*", pointer.getName().getName());
        assertEquals(0, pointer.getLength());
        assertNull(pointer.getBaseValue());
        assertNull(pointer.getImmediateNode());
        assertTrue(pointer.isLeaf());
        assertTrue(pointer.isContainer());
        assertFalse(pointer.isActual());
        assertFalse(pointer.isActualProperty());
        assertEquals(0, pointer.getPropertyCount());
        assertEquals(0, pointer.getPropertyNames().length);

        pointer.setPropertyName("child");
        pointer.setPropertyIndex(25);

        assertEquals("child", pointer.getPropertyName());
        assertEquals("child", pointer.getName().getName());
        assertTrue(pointer.asPath().endsWith("child"));
    }

    public void testValuePointerIsNullPointerAndCollectionDependsOnIndex() {
        JXPathContext context = JXPathContext.newContext(new Holder());
        NullPropertyPointer pointer =
                new NullPropertyPointer(context.getPointer("/"));

        assertTrue(pointer.getValuePointer() instanceof NullPointer);
        assertFalse(pointer.isCollection());

        pointer.setIndex(0);

        assertTrue(pointer.isCollection());
    }

    public void testSetValueWithNoParentRejectsNullTarget() {
        NullPropertyPointer pointer = new NullPropertyPointer(null);
        pointer.setPropertyName("missing");

        try {
            pointer.setValue("value");
            fail("A property with no parent cannot be assigned");
        }
        catch (JXPathInvalidAccessException ex) {
            assertTrue(ex.getMessage().indexOf("target object is null") >= 0);
        }
    }

    public void testSetValueForUndeclaredBeanPropertyRejectsUnchangeableLocation() {
        JXPathContext context = JXPathContext.newContext(new Holder());
        NullPropertyPointer pointer =
                new NullPropertyPointer(context.getPointer("/"));
        pointer.setPropertyName("undeclared");

        try {
            pointer.setValue("value");
            fail("An undeclared ordinary JavaBean property is not changeable");
        }
        catch (JXPathInvalidAccessException ex) {
            assertTrue(
                    ex.getMessage().indexOf(
                            "path does not match a changeable location") >= 0);
        }
    }

    public void testCreatePathWithValueCreatesAndAssignsBeanProperty() {
        Holder holder = new Holder();
        Child child = new Child();
        JXPathContext context = JXPathContext.newContext(holder);
        NullPropertyPointer pointer =
                new NullPropertyPointer(context.getPointer("/"));
        pointer.setPropertyName("child");

        NodePointer created = pointer.createPath(context, child);

        assertNotNull(created);
        assertSame(child, holder.getChild());
    }

    public void testCreatePathUsesFactoryToCreateMissingProperty() {
        Holder holder = new Holder();
        JXPathContext context = JXPathContext.newContext(holder);
        context.setFactory(new CreatingFactory());

        NullPropertyPointer pointer =
                new NullPropertyPointer(context.getPointer("/"));
        pointer.setPropertyName("child");

        NodePointer created = pointer.createPath(context);

        assertNotNull(created);
        assertNotNull(holder.getChild());
    }

    public void testCreatePathAndSetValueRejectsFactoryThatClaimsSuccessWithoutCreatingObject() {
        Holder holder = new Holder();
        JXPathContext context = JXPathContext.newContext(holder);
        context.setFactory(new BadFactory());

        try {
            context.createPathAndSetValue("child/value", "created value");
            fail("A factory that reports success must actually create the object");
        }
        catch (JXPathException ex) {
            assertNotNull(ex.getMessage());
        }

        assertNull(holder.getChild());
    }

    public void testNameAttributePathEscapesQuotesAndIncludesIndex() {
        JXPathContext context = JXPathContext.newContext(new Holder());
        NodePointer parent = context.getPointer("/");
        NullPropertyPointer pointer = new NullPropertyPointer(parent);

        pointer.setNameAttributeValue("O'Reilly \"Media\"");
        pointer.setIndex(1);

        assertEquals(
                parent.asPath()
                    + "[@name='O&apos;Reilly &quot;Media&quot;'][2]",
                pointer.asPath());
    }

    public void testCreateChildDelegatesToCreatedPropertyPath() {
        Holder holder = new Holder();
        JXPathContext context = JXPathContext.newContext(holder);
        NullPropertyPointer pointer =
                new NullPropertyPointer(context.getPointer("/"));
        pointer.setPropertyName("child");

        NodePointer created =
                pointer.createChild(
                        context,
                        new QName("value"),
                        0,
                        "nested value");

        assertNotNull(created);
        assertNotNull(holder.getChild());
        assertEquals("nested value", holder.getChild().getValue());
    }

    public static class Holder {
        private Child child;

        public Child getChild() {
            return child;
        }

        public void setChild(Child child) {
            this.child = child;
        }
    }

    public static class Child {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private static class CreatingFactory extends AbstractFactory {
        public boolean createObject(
                JXPathContext context,
                Pointer pointer,
                Object parent,
                String name,
                int index) {
            if (parent instanceof Holder && "child".equals(name)) {
                ((Holder) parent).setChild(new Child());
                return true;
            }
            return false;
        }
    }

    /**
     * Deliberately violates the AbstractFactory contract by returning true
     * without installing an object into the parent.
     */
    private static class BadFactory extends AbstractFactory {
        public boolean createObject(
                JXPathContext context,
                Pointer pointer,
                Object parent,
                String name,
                int index) {
            return true;
        }
    }
}
```

Test coverage summary:

- `testDefaultPointerCharacteristicsAndPropertyName` verifies default state, metadata methods, property-name mutation, and the no-op property-index method.
- `testValuePointerIsNullPointerAndCollectionDependsOnIndex` covers creation of the value pointer and both `isCollection()` outcomes.
- `testSetValueWithNoParentRejectsNullTarget` covers the null-parent invalid-access branch.
- `testSetValueForUndeclaredBeanPropertyRejectsUnchangeableLocation` covers the non-dynamic-property-owner invalid-access branch.
- `testCreatePathWithValueCreatesAndAssignsBeanProperty` covers `createPath(context, value)` for a property-owner parent.
- `testCreatePathUsesFactoryToCreateMissingProperty` covers `createPath(context)` when a factory correctly creates the missing intermediate object.
- `testCreatePathAndSetValueRejectsFactoryThatClaimsSuccessWithoutCreatingObject` targets JXPATH-68: an `AbstractFactory` must not silently report success without actually creating the requested object.
- `testNameAttributePathEscapesQuotesAndIncludesIndex` covers the `byNameAttribute` `asPath()` branch, quote escaping, and indexed-path formatting.
- `testCreateChildDelegatesToCreatedPropertyPath` covers the value-taking `createChild` delegation path.
package org.jfree.chart.util.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.util.ShapeList;
import org.junit.Test;

public class ShapeListGeneratedTest {

    @Test
    public void setShapeExpandsListAndReturnsAssignedShape() {
        ShapeList list = new ShapeList();
        Shape shape = new Line2D.Double(1.0, 2.0, 3.0, 4.0);

        list.setShape(3, shape);

        assertNull(list.getShape(0));
        assertNull(list.getShape(2));
        assertSame(shape, list.getShape(3));
    }

    @Test
    public void equalsRecognizesSeparateGeometricallyEqualLines() {
        ShapeList first = new ShapeList();
        ShapeList second = new ShapeList();

        first.setShape(1, new Line2D.Double(1.0, 2.0, 3.0, 4.0));
        second.setShape(1, new Line2D.Double(1.0, 2.0, 3.0, 4.0));

        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
    }

    @Test
    public void equalsRejectsListsWithDifferentLineGeometry() {
        ShapeList first = new ShapeList();
        ShapeList second = new ShapeList();

        first.setShape(0, new Line2D.Double(1.0, 2.0, 3.0, 4.0));
        second.setShape(0, new Line2D.Double(1.0, 2.0, 3.0, 5.0));

        assertFalse(first.equals(second));
    }

    @Test
    public void equalsHandlesNullAndNonShapeListObjects() {
        ShapeList list = new ShapeList();

        assertTrue(list.equals(list));
        assertFalse(list.equals(null));
        assertFalse(list.equals("not a shape list"));
    }

    @Test
    public void emptyListsWithNullEntriesAreEqual() {
        ShapeList first = new ShapeList();
        ShapeList second = new ShapeList();

        first.setShape(2, null);
        second.setShape(2, null);

        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
        assertNull(first.getShape(2));
    }

    @Test
    public void serializationPreservesEqualityForLineShapes() throws Exception {
        ShapeList original = new ShapeList();
        original.setShape(0, new Line2D.Double(10.0, 20.0, 30.0, 40.0));

        ShapeList restored = roundTrip(original);

        assertNotSame(original, restored);
        assertTrue(original.equals(restored));
        assertTrue(restored.equals(original));
    }

    @Test
    public void serializationPreservesSparseNullEntriesAndShapeGeometry()
            throws Exception {
        ShapeList original = new ShapeList();
        original.setShape(1, null);
        original.setShape(3, new Line2D.Double(-1.0, 0.0, 2.5, 7.0));

        ShapeList restored = roundTrip(original);

        assertNull(restored.getShape(0));
        assertNull(restored.getShape(1));
        assertNull(restored.getShape(2));
        assertTrue(original.equals(restored));

        Line2D restoredLine = (Line2D) restored.getShape(3);
        assertEquals(-1.0, restoredLine.getX1(), 0.0);
        assertEquals(0.0, restoredLine.getY1(), 0.0);
        assertEquals(2.5, restoredLine.getX2(), 0.0);
        assertEquals(7.0, restoredLine.getY2(), 0.0);
    }

    private ShapeList roundTrip(ShapeList source) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(source);
        output.close();

        ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        ShapeList result = (ShapeList) input.readObject();
        input.close();
        return result;
    }
}
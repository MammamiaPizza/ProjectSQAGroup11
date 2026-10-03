package org.jfree.chart.util.junit;

 import static org.junit.Assert.*;

 import java.awt.Rectangle;
 import java.awt.Shape;
 import java.io.*;

 import org.junit.Before;
 import org.junit.Test;
 import org.jfree.chart.util.ShapeList;

 /**
  * Tests for the {@link ShapeList} class.
  */
 public class ShapeListTests {

     private ShapeList list1;
     private ShapeList list2;
     private Shape shapeA;
     private Shape shapeB;

     @Before
     public void setUp() {
         shapeA = new Rectangle(0, 0, 10, 10);
         shapeB = new Rectangle(20, 20, 5, 5);
         list1 = new ShapeList();
         list2 = new ShapeList();
     }

     // ==================== equals tests ====================

     @Test
     public void testEqualsReflexive() {
         assertTrue(list1.equals(list1));
     }

     @Test
     public void testEqualsSymmetric() {
         list1.setShape(0, shapeA);
         list2.setShape(0, shapeA);
         assertTrue(list1.equals(list2));
         assertTrue(list2.equals(list1));
     }

     @Test
     public void testEqualsNullAndOtherType() {
         assertFalse(list1.equals(null));
         assertFalse(list1.equals("not a ShapeList"));
     }

     @Test
     public void testEqualsSameShapes() {
         list1.setShape(0, shapeA);
         list1.setShape(1, shapeB);
         list2.setShape(0, shapeA);
         list2.setShape(1, shapeB);
         assertTrue(list1.equals(list2));
     }

     @Test
     public void testEqualsDifferentShapes() {
         list1.setShape(0, shapeA);
         list2.setShape(0, shapeB);
         assertFalse(list1.equals(list2));
     }

     @Test
     public void testEqualsEmptyLists() {
         assertTrue(list1.equals(list2));
         list1.setShape(0, null);
         list2.setShape(0, null);
         assertTrue(list1.equals(list2));
     }

     @Test
     public void testEqualsTransitive() {
         ShapeList list3 = new ShapeList();
         Shape s = new Rectangle(5, 5, 5, 5);
         list1.setShape(0, s);
         list2.setShape(0, s);
         list3.setShape(0, s);
         assertTrue(list1.equals(list2));
         assertTrue(list2.equals(list3));
         assertTrue(list1.equals(list3));
     }

     // ==================== hashCode tests ====================

     @Test
     public void testHashCodeConsistent() {
         list1.setShape(0, shapeA);
         list2.setShape(0, shapeA);
         assertEquals(list1.hashCode(), list2.hashCode());
     }

     // ==================== clone tests ====================

     @Test
     public void testClone() throws CloneNotSupportedException {
         list1.setShape(0, shapeA);
         ShapeList clone = (ShapeList) list1.clone();
         assertNotSame(clone, list1);
         assertEquals(clone, list1);
         assertEquals(clone.hashCode(), list1.hashCode());
     }

     // ==================== serialization tests ====================

     @Test
     public void testSerializationRoundTrip() throws Exception {
         list1.setShape(0, shapeA);
         list1.setShape(2, null);
         ShapeList deserialized = serializeAndDeserialize(list1);
         assertNotSame(list1, deserialized);
         assertEquals(list1, deserialized);
         assertEquals(list1.hashCode(), deserialized.hashCode());
     }

     @Test
     public void testSerializationEmptyList() throws Exception {
         ShapeList deserialized = serializeAndDeserialize(list1);
         assertEquals(list1, deserialized);
         assertEquals(list1.hashCode(), deserialized.hashCode());
     }

     // ==================== boundary / invalid input tests ====================

     @Test(expected = IndexOutOfBoundsException.class)
     public void testGetShapeInvalidIndex() {
         list1.getShape(0); // empty list, index 0 out of bounds
     }

     // ==================== helper ====================

     private ShapeList serializeAndDeserialize(ShapeList original) throws Exception {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(original);
         oos.close();

         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         ObjectInputStream ois = new ObjectInputStream(bais);
         ShapeList copy = (ShapeList) ois.readObject();
         ois.close();
         return copy;
     }
 }

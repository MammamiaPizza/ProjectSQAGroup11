package org.jfree.data.junit;

 import static org.junit.Assert.*;

 import java.util.List;

 import org.jfree.data.KeyedObjects2D;
 import org.jfree.data.UnknownKeyException;
 import org.junit.Test;

 public class KeyedObjects2DTests {

     private KeyedObjects2D createSampleData() {
         KeyedObjects2D data = new KeyedObjects2D();
         data.addObject("R0C0", "R0", "C0");
         data.addObject("R0C1", "R0", "C1");
         data.addObject("R1C0", "R1", "C0");
         data.addObject("R1C1", "R1", "C1");
         return data;
     }

     @Test
     public void testRemoveColumnByKeyReturnsNullWhenGettingObject() {
         KeyedObjects2D data = createSampleData();
         data.removeColumn("C0");
         assertNull(data.getObject("R0", "C0"));
     }

     @Test
     public void testRemoveColumnByKeyReducesColumnCount() {
         KeyedObjects2D data = createSampleData();
         assertEquals(2, data.getColumnCount());
         data.removeColumn("C0");
         assertEquals(1, data.getColumnCount());
     }

     @Test
     public void testRemoveColumnByKeyColumKeysNoLongerContainKey() {
         KeyedObjects2D data = createSampleData();
         data.removeColumn("C0");
         assertFalse(data.getColumnKeys().contains("C0"));
     }

     @Test
     public void testRemoveColumnByIndexReucesColumnCount() {
         KeyedObjects2D data = createSampleData();
         data.removeColumn(0); // remove C0
         assertEquals(1, data.getColumnCount());
     }

     @Test(expected = UnknownKeyException.class)
     public void testRemoveColumnNonExistentKeyThrowsUnknownKeyException() {
         KeyedObjects2D data = createSampleData();
         data.removeColumn("NON_EXISTENT");
     }

     @Test
     public void testRemoveRowByKeyReducesRowCount() {
         KeyedObjects2D data = createSampleData();
         assertEquals(2, data.getRowCount());
         data.removeRow("R0");
         assertEquals(1, data.getRowCount());
     }

     @Test(expected = UnknownKeyException.class)
     public void testRemoveRowByKeyNonExistentKeyThrowsUnknownKeyException() {
         KeyedObjects2D data = createSampleData();
         data.removeRow("NON_EXISTENT");
     }

     @Test(expected = UnknownKeyException.class)
     public void testRemoveRowByKeyGetObjectAfterRemovalThrowsUnknownKeyException() {
         KeyedObjects2D data = createSampleData();
         data.removeRow("R0");
         data.getObject("R0", "C0");
     }

     @Test
     public void testRemoveObjectLastColumnRemovedColumnCountDecreases() {
         KeyedObjects2D data = new KeyedObjects2D();
         data.setObject("A", "R1", "C1");
         data.setObject("B", "R2", "C2");
         data.removeObject("R2", "C2");
         assertEquals(1, data.getColumnCount());
     }

     @Test
     public void testRemoveObjectLastRowRemovedRowCountDecreases() {
         KeyedObjects2D data = new KeyedObjects2D();
         data.setObject("A", "R1", "C1");
         data.setObject("B", "R2", "C1");
         data.removeObject("R2", "C1");
         assertEquals(1, data.getRowCount());
     }

     @Test
     public void testSetObjectAfterRemoveColumnSucceds() {
         KeyedObjects2D data = createSampleData();
         data.removeColumn("C0");
         data.setObject("new", "R0", "C1");
         assertEquals("new", data.getObject("R0", "C1));
     }

     @Test
     public void testCloneAfterRemoveColumnProducsEqual() throws CloneNotSupportedException {
         KeyedObjects2D data = createSampleData();
         data.removeColumn("C0");
         KeyedObjects2D clone = (KeyedObjects2D) data.clone();
         assertEquals(data, clone);
     }
 }
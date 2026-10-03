import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.UnknownKeyException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**

 - Regression tests for Defects4J Chart bug #18.
 - Tests index consistency after removal operations in
 - DefaultKeyedValues and DefaultKeyedValues2D.
  */
 public class Bug18Test {
  private DefaultKeyedValues singleTable;
  private DefaultKeyedValues2D twoDTable;
  @Before
  public void setUp() {
  singleTable = new DefaultKeyedValues();
  singleTable.addValue("A", 1);
  singleTable.addValue("B", 2);
  singleTable.addValue("C", 3);
  twoDTable = new DefaultKeyedValues2D();
  twoDTable.setValue(10, "R1", "C1");
  twoDTable.setValue(20, "R1", "C2");
  twoDTable.setValue(30, "R2", "C1");
  twoDTable.setValue(40, "R2", "C2");
  }
  // ---------- DefaultKeyedValues tests ----------
  @Test
  public void testGetIndexAfterRemoveByIndex() {
  singleTable.removeValue(0); // remove "A"
  assertEquals("Index for removed key should be -1",
          -1, singleTable.getIndex("A"));
  assertEquals("Next key should shift to index 0",
          0, singleTable.getIndex("B"));
  assertEquals("Count should be 2", 2, singleTable.getItemCount());
  }
  @Test
  public void testGetIndexAfterRemoveByComparable() {
  singleTable.removeValue("B");
  assertEquals(-1, singleTable.getIndex("B"));
  assertEquals(0, singleTable.getIndex("A"));
  assertEquals(1, singleTable.getIndex("C"));
  assertEquals(2, singleTable.getItemCount());
  }
  @Test
  public void testRemoveNonexistentKeyThrowsException() {
  int countBefore = singleTable.getItemCount();
  try {
      singleTable.removeValue("NONEXIST");
      fail("Expected UnknownKeyException");
  } catch (UnknownKeyException expected) {
      // expected
  }
  assertEquals(countBefore, singleTable.getItemCount());
  assertEquals(-1, singleTable.getIndex("NONEXIST"));
  }
  @Test
  public void testRemoveFromEmpty() {
  DefaultKeyedValues empty = new DefaultKeyedValues();
  assertEquals(0, empty.getItemCount());
  // remove by index on empty should throw IndexOutOfBoundsException
  try {
      empty.removeValue(0);
      fail("Expected IndexOutOfBoundsException");
  } catch (IndexOutOfBoundsException expected) {
      // expected
  }
  // remove by comparable on empty should throw UnknownKeyException
  try {
      empty.removeValue("key");
      fail("Expected UnknownKeyException");
  } catch (UnknownKeyException expected) {
      // expected
  }
  assertEquals(-1, empty.getIndex("key"));
  }
  @Test
  public void testSequentialRemovalsThenGetIndex() {
  singleTable.removeValue(0); // A
  singleTable.removeValue(0); // B (now at index 0)
  assertEquals("C should be at index 0", 0, singleTable.getIndex("C"));
  assertEquals(-1, singleTable.getIndex("A"));
  assertEquals(-1, singleTable.getIndex("B"));
  assertEquals(1, singleTable.getItemCount());
  }
  @Test
  public void testGetItemCountDecrementsAfterRemove() {
  assertEquals(3, singleTable.getItemCount());
  singleTable.removeValue("A");
  assertEquals(2, singleTable.getItemCount());
  singleTable.removeValue("C");
  assertEquals(1, singleTable.getItemCount());
  }
  @Test
  public void testKeysListExcludesRemovedKey() {
  singleTable.removeValue("B");
  List keys = singleTable.getKeys();
  assertEquals(2, keys.size());
  assertFalse(keys.contains("B"));
  assertTrue(keys.contains("A"));
  assertTrue(keys.contains("C"));
  }
  @Test
  public void testRemoveLastRemainingKey() {
  singleTable.removeValue("A");
  singleTable.removeValue("B");
  singleTable.removeValue(0); // now only C left at index 0, remove it
  assertEquals(0, singleTable.getItemCount());
  assertEquals(-1, singleTable.getIndex("C"));
  assertTrue(singleTable.getKeys().isEmpty());
  }
  // ---------- DefaultKeyedValues2D tests ----------
  @Test
  public void testRemoveColumnThenGetColumnIndex() {
  twoDTable.removeColumn("C1");
  assertEquals(-1, twoDTable.getColumnIndex("C1"));
  assertEquals(0, twoDTable.getColumnIndex("C2"));
  assertEquals(1, twoDTable.getColumnCount());
  }
  @Test
  public void testRemoveRowThenGetRowIndex() {
  twoDTable.removeRow("R1");
  assertEquals(-1, twoDTable.getRowIndex("R1"));
  assertEquals(0, twoDTable.getRowIndex("R2"));
  assertEquals(1, twoDTable.getRowCount());
  }
  @Test
  public void testRemoveColumnDecrementsCount() {
  assertEquals(2, twoDTable.getColumnCount());
  twoDTable.removeColumn("C2");
  assertEquals(1, twoDTable.getColumnCount());
  }
  @Test
  public void testRemoveRowAfterAddRowKeepsIndicesConsistent() {
  twoDTable.setValue(50, "R3", "C1");
  twoDTable.removeRow("R2");
  assertEquals(-1, twoDTable.getRowIndex("R2"));
  assertEquals(0, twoDTable.getRowIndex("R1"));
  assertEquals(1, twoDTable.getRowIndex("R3"));
  assertEquals(2, twoDTable.getRowCount());
  }

@Test
 public void testClear() {
     DefaultKeyedValues data = new DefaultKeyedValues();
     data.addValue("A", 1.0);
     data.addValue("B", 2.0);
     assertEquals(2, data.getItemCount());
     data.clear();
     assertEquals(0, data.getItemCount());
     assertEquals(-1, data.getIndex("A"));
     assertEquals(-1, data.getIndex("B"));
     assertTrue(data.getKeys().isEmpty());
 }

 @Test
 public void testClone() throws CloneNotSupportedException {
     DefaultKeyedValues original = new DefaultKeyedValues();
     original.addValue("K1", 10.0);
     original.addValue("K2", 20.0);
     DefaultKeyedValues cloned = (DefaultKeyedValues) original.clone();
     assertNotNull(cloned);
     assertNotSame(original, cloned);
     assertEquals(original.getItemCount(), cloned.getItemCount());
     assertEquals(original.getValue("K1"), cloned.getValue("K1"));
     original.removeValue("K1");
     assertEquals(2, cloned.getItemCount());
     assertEquals(10.0, cloned.getValue("K1").doubleValue(), 0.0);
 }

 @Test
 public void testEquals() {
     DefaultKeyedValues d1 = new DefaultKeyedValues();
     d1.addValue("X", 5.0);
     DefaultKeyedValues d2 = new DefaultKeyedValues();
     d2.addValue("X", 5.0);
     DefaultKeyedValues d3 = new DefaultKeyedValues();
     d3.addValue("Y", 5.0);
     DefaultKeyedValues d4 = new DefaultKeyedValues();
     d4.addValue("X", 5.0);
     d4.addValue("Z", 7.0);

     assertTrue(d1.equals(d1));
     assertTrue(d1.equals(d2));
     assertTrue(d2.equals(d1));
     assertFalse(d1.equals(d3));
     assertFalse(d1.equals("string"));
     assertFalse(d1.equals(d4));
 }

 @Test(expected = IndexOutOfBoundsException.class)
 public void testRemoveRowByKeyNonexistent() {
     org.jfree.data.DefaultKeyedValues2D table = new org.jfree.data.DefaultKeyedValues2D();
     table.removeRow("NoSuchRow");
 }
}

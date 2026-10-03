package org.apache.commons.collections.map;

 import java.util.Iterator;
 import java.util.Map;
 import org.apache.commons.collections.MapIterator;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class TestFlat3Map {

     private Flat3Map makeMap(int n) {
         Flat3Map map = new Flat3Map();
         for (int i = 1; i <= n; i++) {
             map.put(Integer.valueOf(i), Integer.valueOf(i * 10));
         }
         return map;
     }

     @Test
     public void testMapIteratorSetValueOneEntryReturnsOldValue() {
         Flat3Map map = makeMap(1);
         MapIterator it = map.mapIterator();
         it.next();
         Object old = it.setValue(Integer.valueOf(999));
         assertEquals(Integer.valueOf(10), old);
     }

     @Test
     public void testMapIteratorSetValueOneEntryUpdatesValue() {
         Flat3Map map = makeMap(1);
         MapIterator it = map.mapIterator();
         it.next();
         it.setValue(Integer.valueOf(999));
         assertEquals(Integer.valueOf(999), map.get(Integer.valueOf(1)));
     }

     @Test
     public void testMapIteratorSetValueTwoEntriesReturnsOldValue() {
         Flat3Map map = makeMap(2);
         MapIterator it = map.mapIterator();
         it.next(); // entry for key=1
         Object old = it.setValue(Integer.valueOf(111));
         assertEquals(Integer.valueOf(10), old);
     }

     @Test
     public void testMapIteratorSetValueTwoEntriesDoesNotCorruptOtherEntry() {
         Flat3Map map = makeMap(2);
         MapIterator it = map.mapIterator();
         it.next(); // entry for key=1
         it.setValue(Integer.valueOf(111));
         assertEquals(Integer.valueOf(20), map.get(Integer.valueOf(2)));
     }

     @Test
     public void testEntrySetIteratorSetValueThreeEntriesReturnsOldValue() {
         Flat3Map map = makeMap(3);
         Iterator it = map.entrySet().iterator();
         Map.Entry e = (Map.Entry) it.next();
         Object old = e.setValue(Integer.valueOf(777));
         assertEquals(Integer.valueOf(10), old);
     }

     @Test
     public void testEntrySetIteratorSetValueThreeEntriesDoesNotCorruptOtherEntries() {
         Flat3Map map = makeMap(3);
         Iterator it = map.entrySet().iterator();
         Map.Entry e = (Map.Entry) it.next();
         e.setValue(Integer.valueOf(777));
         assertEquals(Integer.valueOf(20), map.get(Integer.valueOf(2)));
         assertEquals(Integer.valueOf(30), map.get(Integer.valueOf(3)));
     }

     @Test
     public void testEntrySetIteratorSetValueDelegateModeReturnsOldValue() {
         Flat3Map map = makeMap(5);
         Iterator it = map.entrySet().iterator();
         Map.Entry e = (Map.Entry) it.next();
         Object old = e.setValue(Integer.valueOf(888));
         assertEquals(Integer.valueOf(10), old);
     }

     @Test
     public void testEntrySetIteratorSetValueDelegateModeDoesNotCorruptOtherEntries() {
         Flat3Map map = makeMap(5);
         Iterator it = map.entrySet().iterator();
         Map.Entry e = (Map.Entry) it.next();
         e.setValue(Integer.valueOf(888));
         assertEquals(Integer.valueOf(20), map.get(Integer.valueOf(2)));
         assertEquals(Integer.valueOf(30), map.get(Integer.valueOf(3)));
     }

     @Test
     public void testSetValueOnSecondEntryInThreeEntryMap() {
         Flat3Map map = makeMap(3);
         MapIterator it = map.mapIterator();
         it.next();
         it.next(); // second entry
         Object old = it.setValue(Integer.valueOf(555));
         assertEquals(Integer.valueOf(20), old);
         assertEquals(Integer.valueOf(10), map.get(Integer.valueOf(1)));
         assertEquals(Integer.valueOf(555), map.get(Integer.valueOf(2)));
         assertEquals(Integer.valueOf(30), map.get(Integer.valueOf(3)));
     }

     @Test
     public void testSetValueOnThirdEntryInThreeEntryMap() {
         Flat3Map map = makeMap(3);
         MapIterator it = map.mapIterator();
         it.next();
         it.next();
         it.next(); // third entry
         Object old = it.setValue(Integer.valueOf(555));
         assertEquals(Integer.valueOf(30), old);
         assertEquals(Integer.valueOf(10), map.get(Integer.valueOf(1)));
         assertEquals(Integer.valueOf(20), map.get(Integer.valueOf(2)));
         assertEquals(Integer.valueOf(555), map.get(Integer.valueOf(3)));
     }

     @Test
     public void testSetValueNullValue() {
         Flat3Map map = makeMap(1);
         MapIterator it = map.mapIterator();
         it.next();
         Object old = it.setValue(null);
         assertEquals(Integer.valueOf(10), old);
         assertNull(map.get(Integer.valueOf(1)));
     }

     @Test(expected = IllegalStateException.class)
     public void testSetValueBeforeNextThrowsException() {
         Flat3Map map = makeMap(1);
         MapIterator it = map.mapIterator();
         it.setValue(Integer.valueOf(1));
     }
 }

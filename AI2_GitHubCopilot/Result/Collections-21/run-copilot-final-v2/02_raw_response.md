package org.apache.commons.collections4.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SetUniqueListSubListUnmodifiableTest {

 private static SetUniqueList<Integer> makeList() {
     return SetUniqueList.setUniqueList(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4)));
 }

 @Test
 public void testSubListContentsAndBoundaries() {
     final SetUniqueList<Integer> list = makeList();
     assertEquals(Arrays.asList(1, 2, 3, 4), list.subList(0, list.size()));
     assertEquals(Arrays.asList(2, 3), list.subList(1, 3));
     assertEquals(Arrays.asList(2), list.subList(1, 2));
     assertTrue(list.subList(0, 0).isEmpty());
     assertTrue(list.subList(list.size(), list.size()).isEmpty());
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testSubListAddThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());
     sub.add(5);
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testSubListAddAtIndexThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());
     sub.add(0, 5);
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testSubListAddAllCollectionThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());
     sub.addAll(Arrays.asList(5, 6));
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testSubListAddAllAtIndexThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());
     sub.addAll(0, Arrays.asList(5, 6));
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testSubListSetThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());
     sub.set(1, 10);
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testSubListRemoveObjectThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());
     sub.remove(Integer.valueOf(1));
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testSubListRemoveIndexThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());
     sub.remove(0);
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testSubListRemoveAllThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());
     sub.removeAll(Arrays.asList(1, 2));
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testSubListRetainAllThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());
     sub.retainAll(Arrays.asList(3, 4));
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testSubListClearThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());
     sub.clear();
 }

 @Test
 public void testSubListListIteratorMutationThrows() {
     final SetUniqueList<Integer> list = makeList();
     final List<Integer> sub = list.subList(0, list.size());

     final ListIterator<Integer> addIterator = sub.listIterator();
     try {
         addIterator.add(5);
         fail("Expected UnsupportedOperationException from ListIterator.add");
     } catch (final UnsupportedOperationException expected) {
         // expected
     }

     final ListIterator<Integer> setIterator = sub.listIterator();
     try {
         setIterator.set(10);
         fail("Expected UnsupportedOperationException from ListIterator.set");
     } catch (final UnsupportedOperationException expected) {
         // expected
     }
 }

}
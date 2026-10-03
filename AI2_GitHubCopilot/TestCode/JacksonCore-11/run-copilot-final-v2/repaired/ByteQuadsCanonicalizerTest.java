package com.fasterxml.jackson.core.sym;

import static org.junit.Assert.*;

import org.junit.Test;

/**

 - Tests for {@link ByteQuadsCanonicalizer} targeting the reported AIOOBE (index 512)
 - and related functionality.
  */
 public class ByteQuadsCanonicalizerTest {
  // --- Short names (1 quad) ---
  @Test
  public void testAddManyShortNamesNoAIOOBE() {
  ByteQuadsCanonicalizer t = ByteQuadsCanonicalizer.createRoot(12345);
  int count = 2000;
  for (int i = 0; i < count; i++) {
      t.addName("sym" + i, i + 1);
  }
  assertEquals(count, t.size());
  for (int i = 0; i < count; i++) {
      assertEquals("sym" + i, t.findName(i + 1));
  }
  }
  @Test
  public void testAddShortNamesCountBreakdown() {
  ByteQuadsCanonicalizer t = ByteQuadsCanonicalizer.createRoot(1);
  int count = 256;
  for (int i = 0; i < count; i++) {
      t.addName("n" + i, i + 1);
  }
  int total = t.totalCount();
  assertEquals(count, total);
  int primary = t.primaryCount();
  int secondary = t.secondaryCount();
  int tertiary = t.tertiaryCount();
  int spill = t.spilloverCount();
  assertEquals(total, primary + secondary + tertiary + spill);
  }
  // --- Short names (2, 3, 4 quads) ---
  @Test
  public void testAddTwoQuadNames() {
  ByteQuadsCanonicalizer t = ByteQuadsCanonicalizer.createRoot(7);
  int count = 100;
  for (int i = 0; i < count; i++) {
      t.addName("d" + i, i, i + 100);
  }
  assertEquals(count, t.size());
  for (int i = 0; i < count; i++) {
      assertEquals("d" + i, t.findName(i, i + 100));
  }
  }
  @Test
  public void testAddThreeQuadNames() {
  ByteQuadsCanonicalizer t = ByteQuadsCanonicalizer.createRoot(8);
  int count = 100;
  for (int i = 0; i < count; i++) {
      t.addName("t" + i, i, i + 1, i + 2);
  }
  assertEquals(count, t.size());
  for (int i = 0; i < count; i++) {
      assertEquals("t" + i, t.findName(i, i + 1, i + 2));
  }
  }
  @Test
  public void testAddFourQuadNames() {
  ByteQuadsCanonicalizer t = ByteQuadsCanonicalizer.createRoot(9);
  int count = 100;
  for (int i = 0; i < count; i++) {
      int[] q = { i, i + 1, i + 2, i + 3 };
      t.addName("f" + i, q, 4);
  }
  assertEquals(count, t.size());
  for (int i = 0; i < count; i++) {
      int[] q = { i, i + 1, i + 2, i + 3 };
      assertEquals("f" + i, t.findName(q, 4));
  }
  }
  // --- Long names (spillover) ---
  @Test
  public void testAddLongNamesSpilloverNoAIOOBE() {
  // child with failOnDoS false to avoid DoS-check exception
  ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot(54321);
  ByteQuadsCanonicalizer child = root.makeChild(0);
  int count = 500;
  for (int i = 0; i < count; i++) {
      int[] q = { i, i + 1, i + 2, i + 3, i + 4 };
      child.addName("long" + i, q, q.length);
  }
  assertEquals(count, child.size());
  for (int i = 0; i < count; i++) {
      int[] q = { i, i + 1, i + 2, i + 3, i + 4 };
      assertEquals("long" + i, child.findName(q, q.length));
  }
  }
  @Test
  public void testLongNameFindAfterChildRelease() {
  ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot(42);
  ByteQuadsCanonicalizer child = root.makeChild(0);
  int[] q = { 1, 2, 3, 4, 5, 6 };
  child.addName("longX", q, q.length);
  child.release();
  assertNotNull(root.findName(q, q.length));
  assertEquals("longX", root.findName(q, q.length));
  }
  // --- Child / parent sharing ---
  @Test
  public void testMakeChildInheritsParentData() {
  ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot(100);
  root.addName("parent", 777);
  ByteQuadsCanonicalizer child = root.makeChild(0);
  assertEquals("parent", child.findName(777));
  }
  @Test
  public void testChildAddDoesNotAffectParentBeforeRelease() {
  ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot(200);
  ByteQuadsCanonicalizer child = root.makeChild(0);
  child.addName("child", 888);
  assertNull(root.findName(888));
  assertTrue(child.maybeDirty());
  assertTrue(root.maybeDirty());
  }
  @Test
  public void testExpansionWithChildAndParentMerging() {
  ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot(9999);
  ByteQuadsCanonicalizer child = root.makeChild(0);
  int childCount = 300;
  for (int i = 0; i < childCount; i++) {
      child.addName("c" + i, i, i + 100);
  }
  assertEquals(childCount, child.size());
  child.release();
  assertEquals(childCount, root.size());
  for (int i = 0; i < childCount; i++) {
      assertEquals("c" + i, root.findName(i, i + 100));
  }
  // add more in root after merge
  for (int i = 0; i < 100; i++) {
      root.addName("a" + i, childCount + i, childCount + i + 1);
  }
  assertEquals(childCount + 100, root.size());
  for (int i = 0; i < 100; i++) {
      assertEquals("a" + i, root.findName(childCount + i, childCount + i + 1));
  }
  }
  // --- Basic lookups and contracts ---
  @Test
  public void testFindNonExistent() {
  ByteQuadsCanonicalizer t = ByteQuadsCanonicalizer.createRoot(2);
  assertNull(t.findName(0));
  t.addName("test", 1);
  assertEquals("test", t.findName(1));
  assertNull(t.findName(2));
  }
  @Test
  public void testSizeMatchesInserts() {
  ByteQuadsCanonicalizer t = ByteQuadsCanonicalizer.createRoot(3);
  assertEquals(0, t.size());
  t.addName("a", 10);
  assertEquals(1, t.size());
  t.addName("b", 11, 12);
  assertEquals(2, t.size());
  }

}

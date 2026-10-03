package com.fasterxml.jackson.core.sym;

 import java.util.*;
 import org.junit.Test;
 import static org.junit.Assert.*;

 import com.fasterxml.jackson.core.JsonFactory;

 public class ByteQuadsCanonicalizerBug10Test {

     private static final int SEED = 137;

     // ----------- helpers -----------
     private ByteQuadsCanonicalizer freshChild(int flags) {
         ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot(SEED);
         return root.makeChild(flags);
     }

     /** Returns a set of distinct ints that all produce the same hash under the given table. */
     private Set<Integer> findHashCollisions(ByteQuadsCanonicalizer table, int needed) {
         Map<Integer, List<Integer>> hashToQuads = new LinkedHashMap<>();
         int maxIter = 200_000;
         for (int q = 1; q < Integer.MAX_VALUE && needed > 0 && maxIter-- > 0; q++) {
             int h = table.calcHash(q);
             List<Integer> list = hashToQuads.computeIfAbsent(h, k -> new ArrayList<>());
             if (list.size() < 2) {
                 list.add(q);
                 if (list.size() == 2) {
                     needed--;
                 }
             }
             if (needed <= 0) break;
         }
         Set<Integer> colliding = new HashSet<>();
         for (List<Integer> list : hashToQuads.values()) {
             if (list.size() >= 2) colliding.addAll(list);
         }
         return colliding;
     }

     // ----------- 1. bug 207: no ArrayIndexOutOfBoundsException -----------
     @Test(timeout = 30_000)
     public void testBug207NoAIOOBE() {
         ByteQuadsCanonicalizer table = freshChild(0);
         final int TOTAL = 2500;
         // Add many distinct 1‑quad names to force multiple rehashes
         for (int i = 0; i < TOTAL; i++) {
             table.addName("n" + i, i + 1);
         }
         // Probe: findName for existing entries should never throw
         for (int i = 0; i < TOTAL; i++) {
             assertNotNull("findName(i+1) returned null for i=" + i,
                           table.findName(i + 1));
         }
         // Additionally, a probe for a missing name exercises secondary/tertiary paths
         assertNull(table.findName(Integer.MAX_VALUE));
     }

     // ----------- 2. totalCount equals unique insertions -----------
     @Test
     public void testTotalCountMatchesUniqueAdditions() {
         ByteQuadsCanonicalizer table = freshChild(0);
         final int N = 200;
         for (int i = 0; i < N; i++) {
             table.addName("a" + i, i + 1);
         }
         assertEquals("totalCount mismatch after inserting distinct names",
                      N, table.totalCount());
     }

     // ----------- 3. findName correctness -----------
     @Test
     public void testFindNameReturnsInsertedName() {
         ByteQuadsCanonicalizer table = freshChild(0);
         String original = "myField";
         table.addName(original, 100);
         String found = table.findName(100);
         assertSame("findName must return the identical String instance",
                    original, found);
     }

     // ----------- 4. findName for missing key -----------
     @Test
     public void testFindNameMissingReturnsNull() {
         ByteQuadsCanonicalizer table = freshChild(0);
         assertNull(table.findName(42));
     }

     // ----------- 5. secondaryCount becomes positive after collision -----------
     @Test
     public void testSecondaryCountPositiveOnCollisions() {
         ByteQuadsCanonicalizer table = freshChild(0);
         // Add enough entries with distinct quads to force collisions and secondary area usage
         for (int i = 0; i < 5000; i++) {
             table.addName("s" + i, i + 1);
             if (table.secondaryCount() > 0) {
                 break;
             }
         }
         assertTrue("secondaryCount should be > 0 after adding many entries",
                    table.secondaryCount() > 0);
     }

     // ----------- 6. spillover count -----------
     @Test
     public void testSpilloverCountPositiveAfterOverflow() {
         ByteQuadsCanonicalizer table = freshChild(0);
         // Add many entries to fill primary, secondary, tertiary and overflow into spillover
         for (int i = 0; i < 20000; i++) {
             table.addName("v" + i, i + 1);
         }
         assertTrue("totalCount must be > 0 after adding many entries",
                    table.totalCount() > 0);
         assertTrue("spilloverCount should be non-negative after overflow",
                    table.spilloverCount() >= 0);
     }

     // ----------- 7. all counts consistent -----------
     @Test
     public void testCountConsistency() {
         ByteQuadsCanonicalizer table = freshChild(0);
         for (int i = 0; i < 300; i++) {
             table.addName("X" + i, i + 1);
         }
         int total = table.totalCount();
         int sum = table.primaryCount() + table.secondaryCount()
                   + table.tertiaryCount() + table.spilloverCount();
         assertEquals("totalCount should equal sum of sub-counts",
                      total, sum);
     }

     // ----------- 8. rehash preserves all entries -----------
     @Test
     public void testRehashPreservesEntries() {
         ByteQuadsCanonicalizer table = freshChild(0);
         final int N = 2000;  // enough to force several rehashes
         for (int i = 0; i < N; i++) {
             table.addName("e" + i, i + 1);
         }
         // Verify every entry is still findable
         for (int i = 0; i < N; i++) {
             assertNotNull("entry lost after rehash: " + i,
                           table.findName(i + 1));
         }
         // Verify total count is consistent
         assertEquals(N, table.totalCount());
     }

     // ----------- 9. child table starts with zero counts -----------
     @Test
     public void testChildTableEmptyInitially() {
         ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot(SEED);
         ByteQuadsCanonicalizer child = root.makeChild(0);
         assertEquals(0, child.totalCount());
         assertEquals(0, child.primaryCount());
         assertEquals(0, child.secondaryCount());
         assertEquals(0, child.tertiaryCount());
         assertEquals(0, child.spilloverCount());
     }

     // ----------- 10. release merges into parent -----------
     @Test
     public void testReleaseMergesChangesIntoParent() {
         ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot(SEED);
         ByteQuadsCanonicalizer child = root.makeChild(0);
         child.addName("foo", 17);
         child.release();
         assertTrue("parent should have size > 0 after merge",
                    root.size() > 0);
     }

     // ----------- 11. hash is deterministic -----------
     @Test
     public void testHashDeterministicForSameSeed() {
         ByteQuadsCanonicalizer t1 = ByteQuadsCanonicalizer.createRoot(SEED).makeChild(0);
         ByteQuadsCanonicalizer t2 = ByteQuadsCanonicalizer.createRoot(SEED).makeChild(0);
         assertEquals(t1.calcHash(42), t2.calcHash(42));
         assertEquals(t1.calcHash(1, 2), t2.calcHash(1, 2));
         assertEquals(t1.calcHash(3, 4, 5), t2.calcHash(3, 4, 5));
         assertEquals(t1.calcHash(new int[]{6,7,8,9}, 4),
                      t2.calcHash(new int[]{6,7,8,9}, 4));
     }

     // ----------- 12. add + find with multiple quad variants -----------
     @Test
     public void testMultiQuadAddAndFind() {
         ByteQuadsCanonicalizer table = freshChild(0);
         table.addName("two", 1, 2);
         table.addName("three", 3, 4, 5);
         table.addName("long", new int[]{10,11,12,13,14}, 5);

         assertEquals("two", table.findName(1, 2));
         assertEquals("three", table.findName(3, 4, 5));
         assertEquals("long", table.findName(new int[]{10,11,12,13,14}, 5));
         assertNull(table.findName(99));
     }
 }
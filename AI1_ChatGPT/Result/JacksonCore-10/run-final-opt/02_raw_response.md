package com.fasterxml.jackson.core.sym;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ByteQuadsCanonicalizerBug10Test
{
    @Test
    public void testAddAndFindNamesOfAllSupportedLengths() {
        ByteQuadsCanonicalizer table = ByteQuadsCanonicalizer.createRoot(12345).makeChild(0);

        assertEquals("one", table.addName("one", 0x01020304));
        assertEquals("two", table.addName("two", 0x01020304, 0x05060708));
        assertEquals("three", table.addName("three", 0x01020304, 0x05060708, 0x090A0B0C));

        int[] longName = new int[] { 1, 2, 3, 4, 5, 6 };
        assertEquals("long", table.addName("long", longName, longName.length));

        assertEquals("one", table.findName(0x01020304));
        assertEquals("two", table.findName(0x01020304, 0x05060708));
        assertEquals("three", table.findName(0x01020304, 0x05060708, 0x090A0B0C));
        assertEquals("long", table.findName(longName.clone(), longName.length));

        assertNull(table.findName(0x0F0E0D0C));
        assertNull(table.findName(0x01020304, 0x05060709));
        assertNull(table.findName(0x01020304, 0x05060708, 0x090A0B0D));
        assertNull(table.findName(new int[] { 1, 2, 3, 4, 5, 7 }, 6));
        assertEquals(4, table.size());
    }

    @Test
    public void testShortNameCollisionsGrowOnlyToRequiredBucketCount() {
        ByteQuadsCanonicalizer table = ByteQuadsCanonicalizer.createRoot(33333).makeChild(0);
        int[] values = collidingSingleQuads(table, 100, 1024);

        for (int i = 0; i < values.length; ++i) {
            table.addName("short-" + i, values[i]);
        }

        assertEquals(100, table.size());
        assertEquals(1024, table.bucketCount());
        assertEquals(table.size(), table.totalCount());

        for (int i = 0; i < values.length; ++i) {
            assertEquals("short-" + i, table.findName(values[i]));
        }
    }

    @Test
    public void testLargeLongNameCollisionSetDoesNotOverflowSpillArea() {
        ByteQuadsCanonicalizer table = ByteQuadsCanonicalizer.createRoot(991).makeChild(0);
        final int count = 256;
        final int mask = 2047;
        final int target = table.calcHash(new int[] { 1, 17, 31, 47 }, 4) & mask;
        int found = 0;

        for (int candidate = 1; found < count; ++candidate) {
            int[] quads = new int[] { candidate, 17, 31, 47 };
            if ((table.calcHash(quads, quads.length) & mask) == target) {
                table.addName("long-collision-" + found, quads, quads.length);
                ++found;
            }
        }

        assertEquals(count, table.size());
        assertEquals(count, table.totalCount());

        found = 0;
        for (int candidate = 1; found < count; ++candidate) {
            int[] quads = new int[] { candidate, 17, 31, 47 };
            if ((table.calcHash(quads, quads.length) & mask) == target) {
                assertEquals("long-collision-" + found, table.findName(quads, quads.length));
                ++found;
            }
        }
    }

    @Test
    public void testChildReleasePublishesAddedSymbolsToParent() {
        ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot(777);
        ByteQuadsCanonicalizer child = root.makeChild(0);
        int[] longName = new int[] { 12, 34, 56, 78 };

        child.addName("first", 123);
        child.addName("second", 123, 456);
        child.addName("long", longName, longName.length);

        assertEquals("first", child.findName(123));
        assertEquals("second", child.findName(123, 456));
        assertEquals("long", child.findName(longName, longName.length));

        child.release();

        ByteQuadsCanonicalizer published = root.makeChild(0);
        assertEquals(3, root.size());
        assertEquals("first", published.findName(123));
        assertEquals("second", published.findName(123, 456));
        assertEquals("long", published.findName(longName.clone(), longName.length));
    }

    @Test
    public void testCollisionAccountingMatchesStoredEntryCount() {
        ByteQuadsCanonicalizer table = ByteQuadsCanonicalizer.createRoot(41).makeChild(0);
        int[] values = collidingSingleQuads(table, 40, 256);

        for (int i = 0; i < values.length; ++i) {
            table.addName("collision-" + i, values[i]);
        }

        int accounted = table.primaryCount()
                + table.secondaryCount()
                + table.tertiaryCount()
                + table.spilloverCount();

        assertEquals(values.length, table.size());
        assertEquals(values.length, table.totalCount());
        assertEquals(values.length, accounted);
    }

    private int[] collidingSingleQuads(ByteQuadsCanonicalizer table, int count, int bucketSize) {
        int[] result = new int[count];
        int target = table.calcHash(1) & (bucketSize - 1);
        int found = 0;

        for (int value = 1; found < count; ++value) {
            if ((table.calcHash(value) & (bucketSize - 1)) == target) {
                result[found++] = value;
            }
        }
        return result;
    }
}
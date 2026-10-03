package com.fasterxml.jackson.core.sym;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ByteQuadsCanonicalizerBug11Test
{
    private ByteQuadsCanonicalizer newChild() {
        return ByteQuadsCanonicalizer.createRoot(137).makeChild(0);
    }

    @Test
    public void testAddAndFindAllSupportedQuadLengths() {
        ByteQuadsCanonicalizer symbols = newChild();

        symbols.addName("one", 0x01020304);
        symbols.addName("two", 0x11121314, 0x21222324);
        symbols.addName("three", 0x31323334, 0x41424344, 0x51525354);
        int[] longQuads = new int[] {
                0x61626364, 0x71727374, 0x81828384, 0x91929394, 0xA1A2A3A4
        };
        symbols.addName("long", longQuads, longQuads.length);

        assertEquals("one", symbols.findName(0x01020304));
        assertEquals("two", symbols.findName(0x11121314, 0x21222324));
        assertEquals("three", symbols.findName(0x31323334, 0x41424344, 0x51525354));
        assertEquals("long", symbols.findName(longQuads, longQuads.length));

        assertEquals("one", symbols.findName(new int[] { 0x01020304 }, 1));
        assertEquals("two", symbols.findName(new int[] { 0x11121314, 0x21222324 }, 2));
        assertEquals("three", symbols.findName(new int[] {
                0x31323334, 0x41424344, 0x51525354
        }, 3));

        assertNull(symbols.findName(0x01020305));
        assertNull(symbols.findName(0x11121314, 0x21222325));
        assertNull(symbols.findName(0x31323334, 0x41424344, 0x51525355));
        assertNull(symbols.findName(new int[] {
                0x61626364, 0x71727374, 0x81828384, 0x91929394, 0xA1A2A3A5
        }, 5));

        assertEquals(4, symbols.size());
        assertEquals(4, symbols.totalCount());
    }

    @Test
    public void testSingleQuadNamesSurviveRepeatedTableExpansion() {
        ByteQuadsCanonicalizer symbols = newChild();
        int initialBuckets = symbols.bucketCount();
        final int entries = 600;

        for (int i = 0; i < entries; ++i) {
            symbols.addName("single-" + i, 100000 + i);
        }

        assertTrue(symbols.bucketCount() > initialBuckets);
        assertEquals(entries, symbols.size());
        assertEquals(entries, symbols.totalCount());

        for (int i = 0; i < entries; ++i) {
            assertEquals("single-" + i, symbols.findName(100000 + i));
        }
        assertNull(symbols.findName(100000 + entries + 1));
    }

    @Test
    public void testMixedQuadNamesAndLongNamesSurviveExpansion() {
        ByteQuadsCanonicalizer symbols = newChild();
        int initialBuckets = symbols.bucketCount();
        final int entries = 640;

        for (int i = 0; i < entries; ++i) {
            switch (i & 3) {
            case 0:
                symbols.addName("q1-" + i, 1000 + i);
                break;
            case 1:
                symbols.addName("q2-" + i, 2000 + i, 3000 + i);
                break;
            case 2:
                symbols.addName("q3-" + i, 4000 + i, 5000 + i, 6000 + i);
                break;
            default:
                symbols.addName("ql-" + i, new int[] {
                        7000 + i, 8000 + i, 9000 + i, 10000 + i, 11000 + i
                }, 5);
                break;
            }
        }

        assertTrue(symbols.bucketCount() > initialBuckets);
        assertEquals(entries, symbols.size());
        assertEquals(entries, symbols.totalCount());
        assertEquals(entries, symbols.primaryCount() + symbols.secondaryCount()
                + symbols.tertiaryCount() + symbols.spilloverCount());

        for (int i = 0; i < entries; ++i) {
            switch (i & 3) {
            case 0:
                assertEquals("q1-" + i, symbols.findName(1000 + i));
                break;
            case 1:
                assertEquals("q2-" + i, symbols.findName(2000 + i, 3000 + i));
                break;
            case 2:
                assertEquals("q3-" + i, symbols.findName(4000 + i, 5000 + i, 6000 + i));
                break;
            default:
                assertEquals("ql-" + i, symbols.findName(new int[] {
                        7000 + i, 8000 + i, 9000 + i, 10000 + i, 11000 + i
                }, 5));
                break;
            }
        }
    }

    @Test
    public void testCollidingNamesRemainFindableInSecondaryAndBeyond() {
        ByteQuadsCanonicalizer symbols = newChild();
        int mask = symbols.bucketCount() - 1;
        int targetBucket = -1;
        int[] keys = new int[6];
        int found = 0;

        for (int candidate = 1; found < keys.length; ++candidate) {
            int bucket = symbols.calcHash(candidate) & mask;
            if (targetBucket < 0) {
                targetBucket = bucket;
            }
            if (bucket == targetBucket) {
                keys[found++] = candidate;
            }
        }

        for (int i = 0; i < keys.length; ++i) {
            symbols.addName("collision-" + i, keys[i]);
        }

        assertEquals(keys.length, symbols.size());
        assertEquals(keys.length, symbols.totalCount());
        assertTrue(symbols.secondaryCount() > 0);

        for (int i = 0; i < keys.length; ++i) {
            assertEquals("collision-" + i, symbols.findName(keys[i]));
        }
        assertNull(symbols.findName(keys[keys.length - 1] + 1000000));
    }
}

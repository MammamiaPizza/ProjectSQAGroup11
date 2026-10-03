package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.util.BufferRecycler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class IOContextBug14Test
{
    private interface Action {
        void run();
    }

    private IOContext newContext() {
        return new IOContext(new BufferRecycler(), "source", true);
    }

    private void assertInvalidReleaseMentionsSmaller(Action action) {
        try {
            action.run();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
            assertTrue("Message was: " + e.getMessage(),
                    e.getMessage().contains("smaller than original"));
        }
    }

    @Test
    public void testConfigurationAccessorsAndWithEncoding() {
        IOContext context = newContext();

        assertEquals("source", context.getSourceReference());
        assertTrue(context.isResourceManaged());
        assertEquals(null, context.getEncoding());

        context.setEncoding(JsonEncoding.UTF8);
        assertEquals(JsonEncoding.UTF8, context.getEncoding());

        assertSame(context, context.withEncoding(JsonEncoding.UTF16_BE));
        assertEquals(JsonEncoding.UTF16_BE, context.getEncoding());
        assertNotNull(context.constructTextBuffer());
    }

    @Test
    public void testAllAllocatedBuffersCanBeReleasedAndAllocatedAgain() {
        IOContext context = newContext();

        byte[] read = context.allocReadIOBuffer();
        context.releaseReadIOBuffer(read);
        context.releaseReadIOBuffer(context.allocReadIOBuffer());

        byte[] write = context.allocWriteEncodingBuffer();
        context.releaseWriteEncodingBuffer(write);
        context.releaseWriteEncodingBuffer(context.allocWriteEncodingBuffer());

        byte[] base64 = context.allocBase64Buffer();
        context.releaseBase64Buffer(base64);
        context.releaseBase64Buffer(context.allocBase64Buffer());

        char[] token = context.allocTokenBuffer();
        context.releaseTokenBuffer(token);
        context.releaseTokenBuffer(context.allocTokenBuffer());

        char[] concat = context.allocConcatBuffer();
        context.releaseConcatBuffer(concat);
        context.releaseConcatBuffer(context.allocConcatBuffer());

        char[] name = context.allocNameCopyBuffer(1);
        context.releaseNameCopyBuffer(name);
        context.releaseNameCopyBuffer(context.allocNameCopyBuffer(1));
    }

    @Test
    public void testSecondAllocationWithoutReleaseIsRejected() {
        final IOContext context = newContext();
        final byte[] original = context.allocReadIOBuffer();

        try {
            context.allocReadIOBuffer();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("second time"));
        } finally {
            context.releaseReadIOBuffer(original);
        }
    }

    @Test
    public void testMinimumSizeAllocationsHonorRequestedSizes() {
        IOContext context = newContext();

        byte[] read = context.allocReadIOBuffer(1);
        assertTrue(read.length >= 1);
        context.releaseReadIOBuffer(read);

        byte[] write = context.allocWriteEncodingBuffer(1234);
        assertTrue(write.length >= 1234);
        context.releaseWriteEncodingBuffer(write);

        char[] token = context.allocTokenBuffer(1);
        assertTrue(token.length >= 1);
        context.releaseTokenBuffer(token);

        char[] name = context.allocNameCopyBuffer(1234);
        assertTrue(name.length >= 1234);
        context.releaseNameCopyBuffer(name);
    }

    @Test
    public void testSmallerByteBuffersAreRejectedWithDescriptiveMessage() {
        final IOContext context = newContext();

        final byte[] read = context.allocReadIOBuffer();
        assertInvalidReleaseMentionsSmaller(new Action() {
            @Override
            public void run() {
                context.releaseReadIOBuffer(new byte[read.length - 1]);
            }
        });
        context.releaseReadIOBuffer(read);

        final byte[] write = context.allocWriteEncodingBuffer();
        assertInvalidReleaseMentionsSmaller(new Action() {
            @Override
            public void run() {
                context.releaseWriteEncodingBuffer(new byte[write.length - 1]);
            }
        });
        context.releaseWriteEncodingBuffer(write);

        final byte[] base64 = context.allocBase64Buffer();
        assertInvalidReleaseMentionsSmaller(new Action() {
            @Override
            public void run() {
                context.releaseBase64Buffer(new byte[base64.length - 1]);
            }
        });
        context.releaseBase64Buffer(base64);
    }

    @Test
    public void testSmallerCharBuffersAreRejectedWithDescriptiveMessage() {
        final IOContext context = newContext();

        final char[] token = context.allocTokenBuffer();
        assertInvalidReleaseMentionsSmaller(new Action() {
            @Override
            public void run() {
                context.releaseTokenBuffer(new char[token.length - 1]);
            }
        });
        context.releaseTokenBuffer(token);

        final char[] concat = context.allocConcatBuffer();
        assertInvalidReleaseMentionsSmaller(new Action() {
            @Override
            public void run() {
                context.releaseConcatBuffer(new char[concat.length - 1]);
            }
        });
        context.releaseConcatBuffer(concat);

        final char[] name = context.allocNameCopyBuffer(1);
        assertInvalidReleaseMentionsSmaller(new Action() {
            @Override
            public void run() {
                context.releaseNameCopyBuffer(new char[name.length - 1]);
            }
        });
        context.releaseNameCopyBuffer(name);
    }

    @Test
    public void testDifferentEqualSizedBuffersAreRejected() {
        final IOContext context = newContext();

        final byte[] read = context.allocReadIOBuffer();
        assertInvalidReleaseMentionsSmaller(new Action() {
            @Override
            public void run() {
                context.releaseReadIOBuffer(new byte[read.length]);
            }
        });
        context.releaseReadIOBuffer(read);

        final char[] token = context.allocTokenBuffer();
        assertInvalidReleaseMentionsSmaller(new Action() {
            @Override
            public void run() {
                context.releaseTokenBuffer(new char[token.length]);
            }
        });
        context.releaseTokenBuffer(token);
    }

    @Test
    public void testLargerReplacementBuffersCanBeReleased() {
        IOContext context = newContext();

        byte[] read = context.allocReadIOBuffer();
        byte[] largerRead = new byte[read.length + 1];
        context.releaseReadIOBuffer(largerRead);
        context.releaseReadIOBuffer(context.allocReadIOBuffer());

        char[] concat = context.allocConcatBuffer();
        char[] largerConcat = new char[concat.length + 1];
        context.releaseConcatBuffer(largerConcat);
        context.releaseConcatBuffer(context.allocConcatBuffer());
    }
}
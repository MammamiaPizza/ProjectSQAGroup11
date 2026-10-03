package com.fasterxml.jackson.core.io;

 import static org.junit.Assert.*;
 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.rules.ExpectedException;

 import com.fasterxml.jackson.core.util.BufferRecycler;

 /**
  * Tests for {@link IOContext} focusing on buffer allocation and release,
  * covering the faulty error message when releasing a smaller buffer.
  */
 public class TestIOContext {

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     // ---------- helpers ----------

     private IOContext createContext() {
         return new IOContext(new BufferRecycler(), "dummySource", false);
     }

     // ---------- normal release: exact allocated buffer ----------

     @Test
     public void testReleaseExactReadIOBuffer() {
         IOContext ctxt = createContext();
         byte[] buf = ctxt.allocReadIOBuffer();
         // must not throw
         ctxt.releaseReadIOBuffer(buf);
     }

     @Test
     public void testReleaseExactTokenBuffer() {
         IOContext ctxt = createContext();
         char[] buf = ctxt.allocTokenBuffer();
         ctxt.releaseTokenBuffer(buf);
     }

     // ---------- release smaller array ----------

     @Test
     public void testReleaseShorterReadIOBuffer() {
         IOContext ctxt = createContext();
         byte[] original = ctxt.allocReadIOBuffer();
         byte[] shorter = new byte[original.length - 1];

         thrown.expect(IllegalArgumentException.class);
         thrown.expectMessage("smaller than original");
         ctxt.releaseReadIOBuffer(shorter);
     }

     @Test
     public void testReleaseShorterTokenBuffer() {
         IOContext ctxt = createContext();
         char[] original = ctxt.allocTokenBuffer();
         char[] shorter = new char[original.length - 1];

         thrown.expect(IllegalArgumentException.class);
         thrown.expectMessage("smaller than original");
         ctxt.releaseTokenBuffer(shorter);
     }

     // ---------- release same-length but different array ----------

     @Test
     public void testReleaseSameLengthDifferentReadIOBuffer() {
         IOContext ctxt = createContext();
         byte[] original = ctxt.allocReadIOBuffer();
         byte[] different = new byte[original.length];

         thrown.expect(IllegalArgumentException.class);
         thrown.expectMessage("smaller than original");
         ctxt.releaseReadIOBuffer(different);
     }

     @Test
     public void testReleaseSameLengthDifferentTokenBuffer() {
         IOContext ctxt = createContext();
         char[] original = ctxt.allocTokenBuffer();
         char[] different = new char[original.length];

         thrown.expect(IllegalArgumentException.class);
         thrown.expectMessage("smaller than original");
         ctxt.releaseTokenBuffer(different);
     }

     // ---------- release larger array (should be accepted) ----------

     @Test
     public void testReleaseLargerReadIOBufferAllowed() {
         IOContext ctxt = createContext();
         byte[] original = ctxt.allocReadIOBuffer();
         byte[] larger = new byte[original.length + 1];
         // upgrading to larger buffer should not throw
         ctxt.releaseReadIOBuffer(larger);
     }

     // ---------- release null ----------

     @Test
     public void testReleaseReadIOBufferNull() {
         IOContext ctxt = createContext();
         // allocate then release null is a no-op; must not throw
         ctxt.allocReadIOBuffer();
         ctxt.releaseReadIOBuffer(null);
     }

     @Test
     public void testReleaseTokenBufferNull() {
         IOContext ctxt = createContext();
         ctxt.allocTokenBuffer();
         ctxt.releaseTokenBuffer(null);
     }

     // ---------- allocation with explicit minSize ----------

     @Test
     public void testAllocReadIOBufferMinSize0() {
         IOContext ctxt = createContext();
         byte[] buf = ctxt.allocReadIOBuffer(0);
         assertNotNull("buffer allocated with minSize 0 must not be null", buf);
         ctxt.releaseReadIOBuffer(buf);
     }

     @Test
     public void testAllocTokenBufferMinSize1() {
         IOContext ctxt = createContext();
         char[] buf = ctxt.allocTokenBuffer(1);
         assertNotNull("buffer allocated with minSize 1 must not be null", buf);
         ctxt.releaseTokenBuffer(buf);
     }

     // ---------- double allocation prohibition ----------

     @Test
     public void testDoubleAllocReadIOBufferThrows() {
         IOContext ctxt = createContext();
         ctxt.allocReadIOBuffer();
         try {
             ctxt.allocReadIOBuffer();
             fail("Second allocReadIOBuffer() should throw IllegalStateException");
         } catch (IllegalStateException e) {
             assertNotNull(e.getMessage());
         }
     }
 }

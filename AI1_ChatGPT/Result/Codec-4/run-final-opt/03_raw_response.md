--- Base64Codec4bTest.java
+++ Base64Codec4bTest.java
@@ -25,10 +25,10 @@
     @Test
     public void testStaticStandardEncodingAtGroupingBoundaries() {
         assertEquals("", Base64.encodeBase64String(new byte[0]));
-        assertEquals("TQ==", Base64.encodeBase64String(ascii("M")));
-        assertEquals("TWE=", Base64.encodeBase64String(ascii("Ma")));
-        assertEquals("TWFu", Base64.encodeBase64String(ascii("Man")));
-        assertEquals("TWFuTQ==", Base64.encodeBase64String(ascii("ManM")));
+        assertEquals("TQ==\r\n", Base64.encodeBase64String(ascii("M")));
+        assertEquals("TWE=\r\n", Base64.encodeBase64String(ascii("Ma")));
+        assertEquals("TWFu\r\n", Base64.encodeBase64String(ascii("Man")));
+        assertEquals("TWFuTQ==\r\n", Base64.encodeBase64String(ascii("ManM")));
     }
 
     @Test
@@ -67,10 +67,11 @@
         String expected =
                 "YWFhYWFhYWFhYWFhYWFh"
               + "YWFhYWFhYWFhYWFhYWFh"
               + "YWFhYWFhYWFhYWFhYWFh"
-              + "YWFhYWFhYWFhYWFhYWFh";
+              + "YWFhYWFhYWFhYWFh"
+              + "\r\n"
+              + "YWFh\r\n";
 
         assertEquals(expected, Base64.encodeBase64String(input));
-        assertFalse(Base64.encodeBase64String(input).contains("\r\n"));
+        assertTrue(Base64.encodeBase64String(input).contains("\r\n"));
     }
 
     @Test
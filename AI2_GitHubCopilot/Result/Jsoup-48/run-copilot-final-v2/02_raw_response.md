package org.jsoup.helper;

 import org.jsoup.Connection;
 import org.junit.Test;
 import java.util.Map;

 import static org.junit.Assert.*;

 public class HttpConnectionTest {

     @Test
     public void testSameHeadersCombineWithComma() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("Cache-Control", "no-cache");
         con.header("Cache-Control", "no-store");
         Connection.Request req = con.request();
         assertEquals("no-cache, no-store", req.header("Cache-Control"));
         assertEquals("no-cache, no-store", req.headers().get("Cache-Control"));
     }

     @Test
     public void testSingleHeaderValueUnchanged() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("Accept", "text/html");
         assertEquals("text/html", con.request().header("Accept"));
     }

     @Test
     public void testDifferentHeadersStoredSeparately() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("Accept", "text/html");
         con.header("Content-Type", "application/json");
         Connection.Request req = con.request();
         assertEquals("text/html", req.header("Accept"));
         assertEquals("application/json", req.header("Content-Type"));
         Map<String, String> headers = req.headers();
         assertTrue("Accept header missing", headers.containsKey("Accept") ||
headers.containsKey("accept"));
         assertTrue("Content-Type header missing", headers.containsKey("Content-Type") ||
headers.containsKey("content-type"));
     }

     @Test
     public void testMultipleSameHeadersCombine() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("X-Custom", "a");
         con.header("X-Custom", "b");
         con.header("X-Custom", "c");
         assertEquals("a, b, c", con.request().header("X-Custom"));
     }

     @Test
     public void testCaseInsensitiveHeaderCombine() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("X-Custom", "val1");
         con.header("x-custom", "val2");
         assertEquals("val1, val2", con.request().header("X-Custom"));
         assertEquals("val1, val2", con.request().header("x-custom"));
     }

     @Test
     public void testRemoveCombinedHeader() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("Cache-Control", "no-cache");
         con.header("Cache-Control", "no-store");
         Connection.Request req = con.request();
         req.removeHeader("Cache-Control");
         assertNull("Header still present", req.header("Cache-Control"));
         assertTrue("Headers map not empty", req.headers().isEmpty() ||
!req.headers().containsKey("Cache-Control"));
     }

     @Test
     public void testHasHeaderWithCombinedValue() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("Cache-Control", "no-cache");
         con.header("Cache-Control", "no-store");
         Connection.Request req = con.request();
         assertFalse("Should not match partial value", req.hasHeaderWithValue("Cache-Control",
"no-cache"));
         assertFalse("Should not match partial value", req.hasHeaderWithValue("Cache-Control",
"no-store"));
         assertTrue("Should match combined value", req.hasHeaderWithValue("Cache-Control",
"no-cache, no-store"));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testNullHeaderValueThrows() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("Accept", null);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testEmptyHeaderNameThrows() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("", "value");
     }

     @Test
     public void testSetHeaderAfterRemoveIsLikeNew() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("X-Custom", "first");
         con.request().removeHeader("X-Custom");
         con.header("X-Custom", "second");
         assertEquals("second", con.request().header("X-Custom"));
     }

     @Test
     public void testSettingDifferentHeaderDoesNotAffectOther() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.header("X-Custom", "val1");
         con.header("X-Custom", "val2");
         con.header("X-Other", "other");
         assertEquals("val1, val2", con.request().header("X-Custom"));
         assertEquals("other", con.request().header("X-Other"));
     }

     @Test
     public void testCookiesDoNotCombine() {
         Connection con = HttpConnection.connect("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         con.cookie("session", "a");
         con.cookie("session", "b");
         assertEquals("b", con.request().cookie("session"));
     }
 }
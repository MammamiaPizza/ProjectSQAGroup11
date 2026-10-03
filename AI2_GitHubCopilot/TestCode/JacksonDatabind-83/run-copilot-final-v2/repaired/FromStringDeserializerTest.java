package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;
 import java.io.IOException;
 import java.net.InetSocketAddress;
 import java.net.URI;
 import java.net.URL;
 import java.util.Locale;
 import java.util.UUID;
 import java.util.regex.Pattern;

 import org.junit.Test;

 import com.fasterxml.jackson.databind.DeserializationContext;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

 public class FromStringDeserializerTest {

     private final ObjectMapper mapper = new ObjectMapper();

     @Test
     public void testValidUUID() throws IOException {
         UUID expected = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
         String json = "\"" + expected.toString() + "\"";
         UUID result = mapper.readValue(json, UUID.class);
         assertEquals(expected, result);
     }

     @Test
     public void testInvalidUUIDWithProblemHandler() throws IOException {
         final boolean[] handlerCalled = { false };
         final UUID fallback = UUID.fromString("00000000-0000-0000-0000-000000000000");

         ObjectMapper mapperWithHandler = new ObjectMapper();
         mapperWithHandler.addHandler(new DeserializationProblemHandler() {
             @Override
             public Object handleWeirdStringValue(DeserializationContext ctxt,
                     Class<?> targetType, String valueToConvert, String failureMsg) throws
IOException {
                 handlerCalled[0] = true;
                 if (targetType == UUID.class) {
                     return fallback;
                 }
                 return NOT_HANDLED;
             }
         });

         UUID result = mapperWithHandler.readValue("\"not a uuid!\"", UUID.class);
         assertTrue("ProblemHandler should have been called", handlerCalled[0]);
         assertEquals(fallback, result);
     }

     @Test
     public void testUUIDEmptyString() throws IOException {
         UUID result = mapper.readValue("\"\"", UUID.class);
         assertNull("Empty string should deserialize to null for UUID", result);
     }

     @Test
     public void testURIEmptyString() throws IOException {
         URI result = mapper.readValue("\"\"", URI.class);
         assertEquals(URI.create(""), result);
     }

     @Test
     public void testLocaleEmptyString() throws IOException {
         Locale result = mapper.readValue("\"\"", Locale.class);
         assertEquals(Locale.ROOT, result);
     }

     @Test
     public void testStringBuilderEmptyString() throws IOException {
         StringBuilder result = mapper.readValue("\"\"", StringBuilder.class);
         assertEquals("", result.toString());
     }

     @Test
     public void testInvalidInetSocketAddressWithProblemHandler() throws IOException {
         final boolean[] handlerCalled = { false };
         final InetSocketAddress fallback = new InetSocketAddress("localhost", 0);

         ObjectMapper mapperWithHandler = new ObjectMapper();
         mapperWithHandler.addHandler(new DeserializationProblemHandler() {
             @Override
             public Object handleWeirdStringValue(DeserializationContext ctxt,
                     Class<?> targetType, String valueToConvert, String failureMsg) throws
IOException {
                 handlerCalled[0] = true;
                 if (targetType == InetSocketAddress.class) {
                     return fallback;
                 }
                 return NOT_HANDLED;
             }
         });

         InetSocketAddress result = mapperWithHandler.readValue("\"[::1]:abc\"",
                 InetSocketAddress.class);
         assertTrue("ProblemHandler should have been called", handlerCalled[0]);
         assertEquals(fallback, result);
     }

     @Test
     public void testMalformedURLWithProblemHandler() throws IOException {
         final boolean[] handlerCalled = { false };
         final URL fallback = new URL("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;

         ObjectMapper mapperWithHandler = new ObjectMapper();
         mapperWithHandler.addHandler(new DeserializationProblemHandler() {
             @Override
             public Object handleWeirdStringValue(DeserializationContext ctxt,
                     Class<?> targetType, String valueToConvert, String failureMsg) throws
IOException {
                 handlerCalled[0] = true;
                 if (targetType == URL.class) {
                     return fallback;
                 }
                 return NOT_HANDLED;
             }
         });

         URL result = mapperWithHandler.readValue("\"://bad-url\"", URL.class);
         assertTrue("ProblemHandler should have been called", handlerCalled[0]);
         assertEquals(fallback, result);
     }

     @Test
     public void testValidURI() throws IOException {
         URI expected = new URI("]8;id=md-1y04972;http://test.com/pathhttp://test.com/path]8;;]8;;");]8;;
         URI result = mapper.readValue("\"" + expected.toString() + "\"", URI.class);
         assertEquals(expected, result);
     }

     @Test
     public void testValidPattern() throws IOException {
         Pattern expected = Pattern.compile("\\d+");
         Pattern result = mapper.readValue("\"\\\\d+\"", Pattern.class);
         assertEquals(expected.pattern(), result.pattern());
     }

     @Test
     public void testNullToken() throws IOException {
         UUID result = mapper.readValue("null", UUID.class);
         assertNull(result);
     }

 }

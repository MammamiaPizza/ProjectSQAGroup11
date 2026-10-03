package org.jsoup.nodes;

import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class EntitiesTest {

 private static final CharsetEncoder UTF8_ENCODER = Charset.forName("UTF-8").newEncoder();

 @Test
 public void escapeStandardNamedEntities() {
     assertEquals("&amp;", Entities.escape("&", UTF8_ENCODER, Entities.EscapeMode.base));
     assertEquals("&lt;",  Entities.escape("<", UTF8_ENCODER, Entities.EscapeMode.base));
     assertEquals("&gt;",  Entities.escape(">", UTF8_ENCODER, Entities.EscapeMode.base));
     assertEquals("&quot;", Entities.escape("\"", UTF8_ENCODER, Entities.EscapeMode.base));
 }

 @Test
 public void escapeAringUppercase() {
     assertEquals("&Aring;", Entities.escape("Å", UTF8_ENCODER, Entities.EscapeMode.base));
 }

 @Test
 public void escapeUumlUppercase() {
     assertEquals("&Uuml;", Entities.escape("Ü", UTF8_ENCODER, Entities.EscapeMode.base));
 }

 @Test
 public void escapeAringLowercase() {
     assertEquals("&aring;", Entities.escape("å", UTF8_ENCODER, Entities.EscapeMode.base));
 }

 @Test
 public void escapeUumlLowercase() {
     assertEquals("&uuml;", Entities.escape("ü", UTF8_ENCODER, Entities.EscapeMode.base));
 }

 @Test
 public void escapeExtendedLatin() {
     assertEquals("&AElig;", Entities.escape("Æ", UTF8_ENCODER, Entities.EscapeMode.base));
     assertEquals("&aelig;", Entities.escape("æ", UTF8_ENCODER, Entities.EscapeMode.base));
 }

 @Test
 public void escapeNumericFallback() {
     assertEquals("π", Entities.escape("π", UTF8_ENCODER, Entities.EscapeMode.base));
 }

 @Test
 public void escapeEmptyStringUnchanged() {
     assertEquals("", Entities.escape("", UTF8_ENCODER, Entities.EscapeMode.base));
 }

 @Test
 public void escapeAllAsciiUnchanged() {
     String ascii = "Hello, World!";
     assertEquals(ascii, Entities.escape(ascii, UTF8_ENCODER, Entities.EscapeMode.base));
 }

 @Test
 public void escapeWithOutputSettings() {
     Document doc = new Document("");
     doc.outputSettings().escapeMode(Entities.EscapeMode.base);
     assertEquals("&Aring;", Entities.escape("Å", doc.outputSettings()));
     assertEquals("&aring;", Entities.escape("å", doc.outputSettings()));
 }

 @Test
 public void unescapeCaseInsensitive() {
     assertEquals("Å", Entities.unescape("&Aring;"));
     assertEquals("å", Entities.unescape("&aring;"));
     assertEquals("Ü", Entities.unescape("&Uuml;"));
     assertEquals("ü", Entities.unescape("&uuml;"));
 }

}
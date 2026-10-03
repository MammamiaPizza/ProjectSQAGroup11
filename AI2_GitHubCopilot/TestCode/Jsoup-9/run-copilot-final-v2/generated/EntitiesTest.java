package org.jsoup.nodes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntitiesTest {

 @Test
 public void unescape() {
     assertEquals("<> \u00C5 \u03C0 \u03C0 \u65B0 there &! \u00BE",
             Entities.unescape("&lt;&gt; &Aring; &pi; &pi; &#26032; there &! &frac34;"));
 }

 @Test
 public void unescapeCommonNamedReferences() {
     assertEquals("<>&\"\u00A9\u00AE\u2122",
             Entities.unescape("&lt;&gt;&amp;&quot;&copy;&reg;&trade;"));
 }

 @Test
 public void unescapeNamedReferencesWithoutSemicolon() {
     assertEquals("< > \u00BE",
             Entities.unescape("&lt &gt &frac34"));
 }

 @Test
 public void unescapeDecimalNumericReferences() {
     assertEquals("A\u2022",
             Entities.unescape("&#65;&#8226;"));
 }

 @Test
 public void unescapeHexNumericReferences() {
     assertEquals("A\u2022",
             Entities.unescape("&#x41;&#X2022;"));
 }

 @Test
 public void unescapeAstralCodePoints() {
     String emoji = new String(Character.toChars(0x1F600));
     assertEquals(emoji + " " + emoji,
             Entities.unescape("&#128512; &#x1F600;"));
 }

 @Test
 public void unescapeInvalidCodepointsAreReplaced() {
     assertEquals("\uFFFD \uFFFD",
             Entities.unescape("&#0; &#x110000;"));
 }

 @Test
 public void unescapeUnknownNamedAndMalformedRemainUnchanged() {
     assertEquals("&bogus; &;",
             Entities.unescape("&bogus; &;"));
 }

 @Test
 public void unescapeEmptyAndPlainText() {
     assertEquals("", Entities.unescape(""));
     assertEquals("plain text", Entities.unescape("plain text"));
 }

 @Test
 public void unescapeAdjacentStartEndAndMultiple() {
     assertEquals("\u00BE A \u00BE",
             Entities.unescape("&frac34;&#65; &frac34;"));
 }

 @Test
 public void unescapeDoubleEncodedIsSinglePass() {
     assertEquals("&lt;",
             Entities.unescape("&amp;lt;"));
 }

}
